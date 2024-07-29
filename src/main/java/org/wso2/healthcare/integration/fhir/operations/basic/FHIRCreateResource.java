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
package org.wso2.healthcare.integration.fhir.operations.basic;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateOperation;
import org.wso2.healthcare.integration.fhir.model.HolderFHIRResource;

import java.util.HashMap;

/**
 * Operation to create empty FHIR Resource instance for a given resource type.
 */
public class FHIRCreateResource extends FHIRCreateOperation {

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        if (configuredParams.containsKey("resourceType")) {
            HolderFHIRResource resource = new HolderFHIRResource(null, configuredParams);
            fhirConnectorContext.createResource(resource);
        } else {
            throw new FHIRConnectException("Resource type is not configured in the connector parameters");
        }
    }

    @Override
    public String getOperationName() {
        return "createResource";
    }
}
