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

import org.hl7.fhir.r4.model.Resource;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.fhir.model.Bundle;

/**
 * FHIR healthcare message context implementation
 */
public class FHIRHealthcareMessageContext extends AbstractFHIRMessageContext {

    private FHIRConnectorContext fhirConnectorContext;

    public FHIRHealthcareMessageContext() {

    }

    @Override
    public Resource getTargetResource() {

        org.wso2.healthcare.integration.fhir.model.Resource targetResource =
                this.fhirConnectorContext.getTargetResource();
        if (targetResource != null) {
            return targetResource.unwrap();
        }
        return null;
    }

    @Override
    public Resource getContainerResource() {

        Bundle bundle = this.fhirConnectorContext.getContainerResource();
        if (bundle != null) {
            return bundle.unwrap();
        }
        return null;
    }

    @Override
    public Resource getResourceById(String objectId) {

        org.wso2.healthcare.integration.fhir.model.Resource resource =
                this.fhirConnectorContext.getResource(objectId);
        if (resource != null) {
            return resource.unwrap();
        }
        return null;
    }

    public FHIRConnectorContext getFhirConnectorContext() {

        return fhirConnectorContext;
    }

    public void setFhirConnectorContext(FHIRConnectorContext fhirConnectorContext) {

        this.fhirConnectorContext = fhirConnectorContext;
    }
}
