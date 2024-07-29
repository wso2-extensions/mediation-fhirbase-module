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

package org.wso2.healthcare.integration.fhir.template.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Data structure to hold conditions template file information.
 */
public class ConditionsMappingModel {

    private String type;
    private Map<String, Condition> mappings;
    private Map<String, ConditionMapping> conditionMappingMap;

    public ConditionsMappingModel() {
        mappings = new HashMap<>();
        conditionMappingMap = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Condition> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Condition> mappings) {
        this.mappings = mappings;
    }

    public Map<String, ConditionMapping> getConditionMappingMap() {
        return conditionMappingMap;
    }

    public void setConditionMappingMap(Map<String, ConditionMapping> conditionMappingMap) {
        this.conditionMappingMap = conditionMappingMap;
    }
}
