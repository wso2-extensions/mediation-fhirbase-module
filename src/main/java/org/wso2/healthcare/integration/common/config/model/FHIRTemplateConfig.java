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

package org.wso2.healthcare.integration.common.config.model;

/**
 * Holds FHIR templates related configs.
 */
public class FHIRTemplateConfig {

    public static final String DEFAULT_RESOURCE_TEMPLATES_PATH = "conf:/healthcare/templates/resources";
    public static final String DEFAULT_KEYMAPPING_TEMPLATE_PATH = "conf:/healthcare/templates/util/keymappings.yaml";
    public static final String DEFAULT_CONDITIONS_TEMPLATE_PATH = "conf:/healthcare/templates/util/conditions.yaml";

    private String resourceTemplatesPath;
    private String keyMappingTemplateFilePath;
    private String conditionsTemplateFilePath;

    public String getResourceTemplatesPath() {
        return resourceTemplatesPath;
    }

    public void setResourceTemplatesPath(String resourceTemplatesPath) {
        this.resourceTemplatesPath = resourceTemplatesPath;
    }

    public String getKeyMappingTemplateFilePath() {
        return keyMappingTemplateFilePath;
    }

    public void setKeyMappingTemplateFilePath(String keyMappingTemplateFilePath) {
        this.keyMappingTemplateFilePath = keyMappingTemplateFilePath;
    }

    public String getConditionsTemplateFilePath() {
        return conditionsTemplateFilePath;
    }

    public void setConditionsTemplateFilePath(String conditionsTemplateFilePath) {
        this.conditionsTemplateFilePath = conditionsTemplateFilePath;
    }
}
