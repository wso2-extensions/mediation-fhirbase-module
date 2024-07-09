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

package org.wso2.healthcare.integration.common.mediators;

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.utils.HealthcareUtils;

/**
 * A generic class that sets FHIR error properties to message context and throws a SynapseException
 */
public class FHIRSynapseExceptionHanlder extends AbstractMediator {

    private String errorSeverity;
    private String errorCode;
    private String errorDetailCode;
    private String errorDetailSystem;
    private String errorDetailDisplay;
    private String errorDiagnostic;

    @Override
    public boolean mediate(MessageContext messageContext) {

        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_SEVERITY, errorSeverity);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_CODE, errorCode);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_CODE, errorDetailCode);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_SYSTEM,
                StringUtils.isEmpty(errorDetailSystem) ? HealthcareUtils.generateCodeSystem("operation-outcome") :
                        errorDetailSystem);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_DISPLAY, errorDetailDisplay);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DIAGNOSTICS, errorDiagnostic);
        throw new SynapseException(errorDetailDisplay);
    }

    public String getErrorSeverity() {

        return errorSeverity;
    }

    public void setErrorSeverity(String errorSeverity) {

        this.errorSeverity = errorSeverity;
    }

    public String getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(String errorCode) {

        this.errorCode = errorCode;
    }

    public String getErrorDetailCode() {

        return errorDetailCode;
    }

    public void setErrorDetailCode(String errorDetailCode) {

        this.errorDetailCode = errorDetailCode;
    }

    public String getErrorDetailDisplay() {

        return errorDetailDisplay;
    }

    public void setErrorDetailDisplay(String errorDetailDisplay) {

        this.errorDetailDisplay = errorDetailDisplay;
    }

    public String getErrorDiagnostic() {

        return errorDiagnostic;
    }

    public void setErrorDiagnostic(String errorDiagnostic) {

        this.errorDiagnostic = errorDiagnostic;
    }

    public String getErrorDetailSystem() {

        return errorDetailSystem;
    }

    public void setErrorDetailSystem(String errorDetailSystem) {

        this.errorDetailSystem = errorDetailSystem;
    }
}
