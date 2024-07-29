/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.healthcare.integration.fhir.operations;

import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConnectorBase;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.template.util.MsgCtxUtil;
import org.wso2.healthcare.integration.fhir.validation.FHIRPayloadValidator;

import java.util.HashMap;

public class FHIRValidatePayload extends FHIRConnectorBase {

    @Override
    public String getOperationName() {
        return "validatePayload";
    }

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        Resource resource = fhirConnectorContext.getContainerResource();
        if (resource == null) {
            String sourceObjectId = configuredParams.get(FHIRConstants.FHIR_PARAM_OBJECT_ID);
            if (sourceObjectId != null) {
                resource = fhirConnectorContext.getResource(sourceObjectId);
                if (resource == null) {
                    throw new FHIRConnectException(
                            "Unable to find source resource with objectID (sourceObjectId) : " + sourceObjectId);
                }
            } else {
                resource = fhirConnectorContext.getTargetResource();
                if (resource == null) {
                    //try to parse the message body to FHIR and validate
                    resource = MsgCtxUtil.parseFHIRResourceFromMessageCtx(messageContext);
                }
            }
        }
        ValidationResult result;
        String profile = configuredParams.get("profile");
        if (StringUtils.isNotBlank(profile)) {
            ValidationOptions validationOptions = new ValidationOptions();
            validationOptions.addProfile(profile);
            result = FHIRPayloadValidator.validateFHIRPayload(resource, validationOptions);
        } else {
            result = FHIRPayloadValidator.validateFHIRPayload(resource, null);
        }
        if (!result.isSuccessful()) {
            messageContext
                    .setProperty(SynapseConstants.ERROR_CODE, FHIRConstants.ERROR_CODE_VALIDATING_PAYLOAD);
            messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, result.toString());
            messageContext.setProperty(SynapseConstants.ERROR_DETAIL, result.toString());
            messageContext.setProperty(FHIRConstants.FHIR_VALIDATION_OUTCOME, result.toOperationOutcome());
            fhirConnectorContext.setValidationOperationOutcome(result.toOperationOutcome());
            throw new FHIRConnectException("FHIR Resource payload validation failed : " + result.toString());
        }
    }
}
