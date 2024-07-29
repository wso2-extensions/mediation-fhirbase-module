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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class to track and maintain resources in the request
 * Use case : Avoid repeating resources
 */
public class ResourceTracker {

    HashMap<String, ArrayList<String>> resourceMap = new HashMap<>(1);

    /**
     * Add resource record
     *
     * @param resource
     */
    public void addResource(Resource resource) {
        String resourceTypeName = resource.getResourceType().name();
        String resourceId = resource.getId();
        if (resourceId != null && !contains(resourceTypeName, resourceId)) {
            addRecord(resourceTypeName, resourceId);
        }
    }

    /**
     * Add resource record
     *
     * @param resourceType
     * @param id
     */
    public void addRecord(String resourceType, String id) {
        ArrayList<String> resourceList = resourceMap.computeIfAbsent(resourceType, k -> new ArrayList<>());
        resourceList.add(id);
    }

    /**
     * Check whether the resource record is available in the tracker
     *
     * @param resourceType
     * @param id
     * @return
     */
    public boolean contains(String resourceType, String id) {
        ArrayList<String> idList = resourceMap.get(resourceType);
        if (idList != null && id != null) {
            return idList.contains(id);
        }
        return false;
    }
}
