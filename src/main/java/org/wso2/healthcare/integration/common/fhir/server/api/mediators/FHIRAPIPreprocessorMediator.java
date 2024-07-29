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

package org.wso2.healthcare.integration.common.fhir.server.api.mediators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.FHIRAPIPreprocessor;
import org.wso2.healthcare.integration.common.utils.HealthcareUtils;

/**
 * Class mediator to preprocess incoming FHIR API requests
 */
public class FHIRAPIPreprocessorMediator extends AbstractMediator {

    private static final Log LOG = LogFactory.getLog(FHIRAPIPreprocessorMediator.class);
    private final FHIRAPIPreprocessor preprocessor;

    public FHIRAPIPreprocessorMediator() {
        this.preprocessor = new FHIRAPIPreprocessor();
    }

    @Override
    public boolean mediate(MessageContext messageContext) {
        try {
            preprocessor.process(messageContext);
        } catch (OpenHealthcareFHIRException e) {
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_SEVERITY, e.getSeverity().getCode());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_CODE, e.getCode().getCode());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_CODE, e.getDetail().getCode());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_SYSTEM,
                    HealthcareUtils.generateCodeSystem(e.getDetail().getSystem()));
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_DISPLAY, e.getDetail().getDisplay());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DIAGNOSTICS, e.getDiagnostic());
            throw new SynapseException(e.getMessage(), e);

        } catch (Exception e) { // Here we need to catch any exception thrown and populate properties related to error flow.

            String errMessage = "Error occurred while preprocessing FHIR API request for resource : " +
                            HealthcareUtils.getProcessedFHIRResource(messageContext);
            LOG.error(errMessage, e);
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_SEVERITY,
                    OpenHealthcareFHIRException.Severity.ERROR.getCode());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_CODE,
                    OpenHealthcareFHIRException.IssueType.PROCESSING.getCode());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_CODE,
                    OpenHealthcareFHIRException.Details.INTERNAL_SERVER_ERROR.getCode());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_SYSTEM,
                    HealthcareUtils.generateCodeSystem(OpenHealthcareFHIRException.Details.INTERNAL_SERVER_ERROR.getSystem()));
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_DISPLAY,
                    OpenHealthcareFHIRException.Details.INTERNAL_SERVER_ERROR.getDisplay());
            messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DIAGNOSTICS, "Internal Server Error");
            throw new SynapseException(errMessage, e);
        }
        return true;
    }
}
