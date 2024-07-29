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

package org.wso2.healthcare.integration.common.fhir.server;

import org.hl7.fhir.r4.model.Resource;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.context.HealthcareMessageContext;
import org.wso2.healthcare.integration.common.context.MessageContextType;

/**
 * Abstract class containing functions for FHIR message context
 */
public abstract class AbstractFHIRMessageContext implements HealthcareMessageContext {

    ResourceTracker resourceTracker = new ResourceTracker();

    @Override
    public MessageContextType getType() {

        return MessageContextType.FHIR;
    }

    @Override
    public String getStoredPropertyName() {

        return Constants.OH_INTERNAL_FHIR_MESSAGE_CONTEXT;
    }

    public ResourceTracker getResourceTracker() {

        return resourceTracker;
    }

    /**
     * Get target FHR resource
     *
     * @return
     */
    public abstract Resource getTargetResource();

    /**
     * Get container resource
     *
     * @return container resource if exists, otherwise null
     */
    public abstract Resource getContainerResource();

    /**
     * Get FHIR resource by name
     *
     * @param objectId
     * @return fhir resource linked with the given id if available, otherwise null
     */
    public abstract Resource getResourceById(String objectId);
}
