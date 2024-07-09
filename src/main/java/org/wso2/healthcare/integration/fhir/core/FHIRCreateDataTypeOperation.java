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
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;

import java.util.HashMap;

/**
 * This abstract class should be implemented by the operations that perform resource "Create" Operations
 */
public abstract class FHIRCreateDataTypeOperation extends FHIRConnectorBase {

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {

        //Retrieve target object id if provided
        String objectId = configuredParams.get(FHIRConstants.FHIR_PARAM_OBJECT_ID);
        String fhirPath = configuredParams.get(FHIRConstants.FHIR_PARAM_FHIR_PATH);
        if (objectId == null) {
            if (fhirPath == null) {
                throw new FHIRConnectException(FHIRConstants.FHIR_PARAM_OBJECT_ID +
                        " is a mandatory parameter for DataType creation operations or at least should provide " +
                        FHIRConstants.FHIR_PARAM_FHIR_PATH);
            } else {
                // If user has given FHIRPath, we generate UUID for the object since it is directly attached to the
                // target FHIR resource object
                objectId = FHIRConnectorUtils.generateUUID();
            }
        }
        execute(messageContext, fhirConnectorContext, configuredParams, objectId);

    }

    /**
     * Note: For operation that require object id resolved for resource/data type creation
     * @param messageContext
     * @param fhirConnectorContext
     * @param configuredParams
     * @param objectId
     * @throws FHIRConnectException
     */
    protected abstract void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                                    HashMap<String, String> configuredParams, String objectId) throws FHIRConnectException;
}
