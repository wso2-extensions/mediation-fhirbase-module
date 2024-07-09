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
import org.wso2.healthcare.integration.fhir.model.Resource;

import java.util.HashMap;

/**
 * This abstract class should be implemented by the operations that perform resource "Add" Operations
 */
public abstract class FHIRAddOperation extends FHIRConnectorBase {

    protected abstract String getParamPrefix();

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        HashMap<String, String> updatedConfiguredParams = configuredParams;
        // TODO following is commented for experimentation. Let's cleanup later
        /*if (!getParamPrefix().isEmpty()) {
            for (String key : configuredParams.keySet()) {
                if (!key.equals(FHIRConstants.FHIR_PARAM_OBJECT_ID) &&
                        !key.equals(FHIRConstants.FHIR_PARAM_READ_FROM_MESSAGE)) {
                    updatedConfiguredParams.put(getParamPrefix() + key, configuredParams.get(key));
                } else {
                    updatedConfiguredParams.put(key, configuredParams.get(key));
                }
            }
        } else {
            updatedConfiguredParams = configuredParams;
        }*/

        String objectId = updatedConfiguredParams.get(FHIRConstants.FHIR_PARAM_OBJECT_ID);
        Resource targetResource;
        if (objectId != null && !objectId.isEmpty()) {
            targetResource = fhirConnectorContext.getResource(objectId);
            if(targetResource == null) {
                throw new FHIRConnectException("Usage Error : Unable to find resource with id: " +
                        objectId + ". When referring resource via id resource must be created resource with relevant " +
                        "id, before executing add operations");
            }
        } else {
            targetResource = fhirConnectorContext.getTargetResource();
            if (targetResource == null && fhirConnectorContext.getContainerResource() != null) {
                targetResource = fhirConnectorContext.getContainerResource();
            }
        }
        execute(messageContext, fhirConnectorContext, updatedConfiguredParams, targetResource);
    }

    protected abstract void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                                    HashMap<String, String> configuredParams, Resource targetResource) throws FHIRConnectException;

}
