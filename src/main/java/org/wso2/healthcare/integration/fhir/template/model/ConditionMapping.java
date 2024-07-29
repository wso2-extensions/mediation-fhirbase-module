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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionMapping {

    private String name;
    private List<Condition> conditions;
    private Map<String, Source> defaultReturnValues;

    public ConditionMapping() {
        this.conditions = new ArrayList<>();
        this.defaultReturnValues = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Map<String, Source> getDefaultReturnValues() {
        return defaultReturnValues;
    }

    public void setDefaultReturnValues(Map<String, Source> defaultReturnValues) {
        this.defaultReturnValues = defaultReturnValues;
    }

    public void addDefaultReturnValue(String key, Source returnValue) {
        this.defaultReturnValues.putIfAbsent(key, returnValue);
    }
}
