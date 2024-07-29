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

package org.wso2.healthcare.integration.fhir.core;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConnectorBase;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;

import java.util.HashMap;

/**
 * This abstract class should be implemented by the operations that perform resource "Query" Operations
 */
public abstract class FHIRQueryOperation extends FHIRConnectorBase {

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        Query query = new Query();

        String fhirPath = configuredParams.get(FHIRConstants.FHIR_PARAM_FHIR_PATH);
        if (fhirPath == null || fhirPath.trim().isEmpty()) {
            throw new FHIRConnectException(FHIRConstants.FHIR_PARAM_FHIR_PATH +
                    " is a mandatory parameter for a Query operation");
        }
        query.setFhirPath(fhirPath);

        String targetId = configuredParams.get(FHIRConstants.FHIR_PARAM_OBJECT_ID);
        if (targetId == null || targetId.isEmpty()) {
            query.setSrcResource(fhirConnectorContext.getTargetResource());
        } else {
            query.setSrcResource(fhirConnectorContext.getResource(targetId));
        }
        execute(messageContext, fhirConnectorContext, configuredParams, query);
    }

    protected abstract void execute(MessageContext messageContext, FHIRConnectorContext context,
                                    HashMap<String, String> configuredParams , Query query) throws FHIRConnectException ;

}
