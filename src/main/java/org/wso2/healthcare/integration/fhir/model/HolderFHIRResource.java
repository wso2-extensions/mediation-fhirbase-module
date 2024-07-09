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

package org.wso2.healthcare.integration.fhir.model;

import org.hl7.fhir.r4.model.ResourceFactory;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;

import java.util.Map;

/**
 * This class will hold any HAPI FHIR resource object.
 */
public class HolderFHIRResource extends Resource {

    private org.hl7.fhir.r4.model.Resource holderResource;

    public HolderFHIRResource(String parentPrefix, Map<String, String> connectorInputParameters) throws FHIRConnectException {

        super(parentPrefix, connectorInputParameters);
        if (connectorInputParameters.containsKey("resourceType")) {
            setHolderResource(connectorInputParameters.get("resourceType"));
        }
    }

    @Override
    public org.hl7.fhir.r4.model.Resource unwrap() {

        return holderResource;
    }

    public void setHolderResource(String type) {

        this.holderResource = ResourceFactory.createResource(type);
    }

    public void setHolderResource(org.hl7.fhir.r4.model.Resource holderResource) {
        this.holderResource = holderResource;
    }
}
