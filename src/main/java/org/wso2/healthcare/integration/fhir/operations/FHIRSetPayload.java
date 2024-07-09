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

import ca.uhn.fhir.validation.ValidationResult;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConnectorBase;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.consentmgt.DecisionProcessor;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.validation.FHIRPayloadValidator;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;

import javax.xml.stream.XMLStreamException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FHIRSetPayload extends FHIRConnectorBase {

    private static final Log log = LogFactory.getLog(FHIRSetPayload.class);
    @Override
    public String getOperationName() {
        return "setPayload";
    }

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        Resource resource;
        String sourceObjectId = configuredParams.get(FHIRConstants.FHIR_PARAM_OBJECT_ID);
        if (StringUtils.isNotEmpty(sourceObjectId)) {
            resource = fhirConnectorContext.getResource(sourceObjectId);
            if (resource == null) {
                throw new FHIRConnectException(
                        "Unable to find source resource with objectID (sourceObjectId) : " + sourceObjectId);
            }
        } else {
            resource = fhirConnectorContext.getContainerResource();
            if (resource == null) {
                resource = fhirConnectorContext.getTargetResource();
                if (resource == null) {
                    throw new FHIRConnectException(
                            "Unable to find target resource or container resource to serialize");
                }
            }
        }
        resource.beforeSerialize(fhirConnectorContext);

        if (messageContext instanceof Axis2MessageContext) {
            org.apache.axis2.context.MessageContext axisMsgCtx =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            String targetContentType = configuredParams.get("targetContentType");
            if (targetContentType == null || targetContentType.isEmpty()) {
                targetContentType = fhirConnectorContext.getClientAcceptMediaType();
            }

            HealthcareIntegratorConfig hConfig = HealthcareIntegratorConfig.getInstance();
            if (hConfig != null && hConfig.getFHIRServerConfig().getFHIRConnectorConfig().isEnableValidateOnSerialization()) {
                ValidationResult result = FHIRPayloadValidator
                        .validateFHIRPayload(resource, null);
                if (!result.isSuccessful()) {
                    messageContext
                            .setProperty(SynapseConstants.ERROR_CODE, FHIRConstants.ERROR_CODE_VALIDATING_PAYLOAD);
                    messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, result.toString());
                    messageContext.setProperty(SynapseConstants.ERROR_DETAIL, result.toString());
                    messageContext.setProperty(FHIRConstants.FHIR_VALIDATION_OUTCOME, result.toOperationOutcome());
                    fhirConnectorContext.setValidationOperationOutcome(result.toOperationOutcome());
                }
            }

            if (fhirConnectorContext.getConsentDecision() != null) {
                org.hl7.fhir.r4.model.Resource consentProcessedResource = DecisionProcessor
                        .processConsentDecision(fhirConnectorContext.getConsentDecision(), resource.unwrap());
                //todo refactor the resource setting method on each fhir resource to be done with a resource level abstraction.
                Method[] methods = resource.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("setFhir" + resource.unwrap().fhirType())) {
                        try {
                            method.invoke(resource, consentProcessedResource);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error("Error occurred while setting the FHIR resources for the consent decision.",
                                    e);
                        }
                        break;
                    }
                }
            }

            if (FHIRConstants.FHIR_XML_CONTENT_TYPE.equalsIgnoreCase(targetContentType) ||
                    FHIRConstants.XML_CONTENT_TYPE.equalsIgnoreCase(targetContentType) ||
                    FHIRConstants.TEXT_XML_CONTENT_TYPE.equalsIgnoreCase(targetContentType)) {
                try {
                    JsonUtil.removeJsonPayload(axisMsgCtx);
                    String payload = "";
                    IBaseResource validationOperationOutcome = fhirConnectorContext.getValidationOperationOutcome();
                    if (validationOperationOutcome != null && !(resource.unwrap() instanceof OperationOutcome)) {
                        payload = FHIRPayloadValidator
                                .getOperationOutcomePayload(validationOperationOutcome, FHIRConstants.XML_CONTENT_TYPE);
                    } else if (resource != null) {
                        payload = resource.serializeToXML();
                    }
                    OMElement omXML = AXIOMUtil.stringToOM(payload);
                    axisMsgCtx.getEnvelope().getBody().addChild(omXML);
                    axisMsgCtx.setProperty(Constants.Configuration.MESSAGE_TYPE, FHIRConstants.XML_CONTENT_TYPE);
                    axisMsgCtx.setProperty(Constants.Configuration.CONTENT_TYPE, FHIRConstants.FHIR_XML_CONTENT_TYPE);
                } catch (XMLStreamException e) {
                    throw new FHIRConnectException(
                            "Error occurred while populating XML payload in the message context", e);
                }
            } else {
                String payload = "";
                IBaseResource validationOperationOutcome = fhirConnectorContext.getValidationOperationOutcome();
                if (validationOperationOutcome != null && !(resource.unwrap() instanceof OperationOutcome)) {
                    payload = FHIRPayloadValidator
                            .getOperationOutcomePayload(validationOperationOutcome, FHIRConstants.JSON_CONTENT_TYPE);
                } else if (resource != null) {
                    payload = resource.serializeToJSON();
                }
                try {
                    JsonUtil.getNewJsonPayload(axisMsgCtx, payload, true, true);
                    axisMsgCtx.setProperty(Constants.Configuration.MESSAGE_TYPE, FHIRConstants.JSON_CONTENT_TYPE);
                    axisMsgCtx.setProperty(Constants.Configuration.CONTENT_TYPE, FHIRConstants.FHIR_JSON_CONTENT_TYPE);
                } catch (AxisFault axisFault) {
                    throw new FHIRConnectException("Error occurred while populating JSON payload in message context",
                                                                                                            axisFault);
                }
            }
            axisMsgCtx.removeProperty("NO_ENTITY_BODY");
        }
    }
}
