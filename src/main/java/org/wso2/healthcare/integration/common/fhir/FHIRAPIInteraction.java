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

package org.wso2.healthcare.integration.common.fhir;

import org.wso2.healthcare.integration.common.Constants;

/**
 * Contains FHIR Rest API interaction types
 * http://hl7.org/fhir/R4/http.html
 */
public enum FHIRAPIInteraction {

    READ("read", Constants.FHIR_REST_INT_LEVEL_INSTANCE, "Read the current state of the resource"),
    UPDATE("update", Constants.FHIR_REST_INT_LEVEL_INSTANCE, "Update an existing resource by its id (or create it if it is new)"),
    PATCH("update", Constants.FHIR_REST_INT_LEVEL_INSTANCE, "Update an existing resource by posting a set of changes to it"),
    DELETE("delete", Constants.FHIR_REST_INT_LEVEL_INSTANCE, "Delete a resource"),
    CREATE("create", Constants.FHIR_REST_INT_LEVEL_TYPE, "Create a new resource with a server assigned id"),
    SEARCH("search", Constants.FHIR_REST_INT_LEVEL_TYPE, "Search the resource type based on some filter criteria"),
    HISTORY("history", Constants.FHIR_REST_INT_LEVEL_TYPE, "Retrieve the change history for a particular resource type");

    private final String name;
    private final String level;
    private final String description;

    FHIRAPIInteraction(String name, String level , String description) {
        this.name = name;
        this.level = level;
        this.description = description;
    }

    public static FHIRAPIInteraction valueOfInteraction(String interactionStr) {
        for (FHIRAPIInteraction interaction : FHIRAPIInteraction.values()) {
            if (interaction.getName().equals(interactionStr)) {
                return interaction;
            }
        }
        return null;
    }

    public String getName() {

        return name;
    }

    public String getLevel() {

        return level;
    }

    public String getDescription() {

        return description;
    }

    @Override
    public String toString() {

        return "FHIRInteraction{" +
                "name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
