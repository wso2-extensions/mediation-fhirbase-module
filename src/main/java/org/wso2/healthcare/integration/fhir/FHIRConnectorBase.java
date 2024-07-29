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

package org.wso2.healthcare.integration.fhir;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorConfig;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;
import org.wso2.healthcare.integration.common.HealthcareIntegratorInitializer;
import org.wso2.healthcare.integration.common.OpenHealthcareException;

import java.util.HashMap;

/**
 * This is base class of the connector operations which all the connector operations extend
 */
public abstract class FHIRConnectorBase extends AbstractConnector {

    private static Log LOG = LogFactory.getLog(FHIRConnectorBase.class);

    public FHIRConnectorBase() {
        //TODO need to add initialization logic
        HealthcareIntegratorInitializer healthcareIntegratorInitializer = new HealthcareIntegratorInitializer();
        try {
            healthcareIntegratorInitializer.initialize();
        } catch (OpenHealthcareException e) {
            LOG.error("Error occurred while initializing the Healthcare Integrator", e);
        }
        FHIRConnectorConfig.getInstance().initialize();
    }

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        // getting fhir configuration
        FHIRConnectorContext fhirConnectorContext = FHIRConnectorUtils.getFHIRConnectorContext(messageContext);

        HashMap<String, String> configuredParams = FHIRConnectorUtils.getConfiguredParams(messageContext);
        if (configuredParams.get("dataMapperEnabled") != null && configuredParams.get("dataMapperEnabled")
                .equals("true")) {
            configuredParams = extractParametersFromPayload(messageContext);
            //Generate objectID
            configuredParams.put(FHIRConstants.FHIR_PARAM_OBJECT_ID, FHIRConnectorUtils.generateUUID());
        }
        try {
            execute(messageContext, fhirConnectorContext, configuredParams);
        } catch (FHIRConnectException e) {
            // This is the exit point of the connector if there is an exception occurred, hence log it
            FHIRConnectorUtils.logConnectorError("Error occurred while executing the connector", e, fhirConnectorContext,
                    messageContext, log);
            throw e;
        } catch (RuntimeException e) {
            // Here we log runtime exceptions otherwise not logged it in upstream. So in case user haven't logged
            // those information in fault handler unable to track information about errors originate from the connector
            FHIRConnectorUtils.logConnectorError("Error occurred within the FHIR connector", e, fhirConnectorContext,
                    messageContext, log);
            throw e;
        }
    }

    protected abstract void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException;

    /**
     * Function to retrieve the name of the operation
     *
     * @return name of the operation
     */
    public abstract String getOperationName();

    public HashMap<String, String> extractParametersFromPayload(MessageContext msgCtx) {
        return FHIRConnectorUtils.getOperationParamMapFromMessage(getOperationName(), msgCtx);
    }

}
