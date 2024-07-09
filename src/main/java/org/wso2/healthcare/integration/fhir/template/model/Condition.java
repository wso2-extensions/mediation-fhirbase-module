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

import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.template.function.impl.ConditionsEvaluateFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data structure to hold single condition information.
 */
public class Condition {

    private String name;
    private String expression;
    private String value;
    private List<Condition> nestedConditions;
    private ConditionsEvaluateFunction.ExpressionType expressionType;
    //this holds info on results to be returned based on condition evaluation
    private Map<String, Source> returnValues;
    private Map<String, Source> defaultReturnValues;

    public Condition() {
        nestedConditions = new ArrayList<>();
        returnValues = new HashMap<>();
        defaultReturnValues = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        setExpressionType();
    }

    private void setExpressionType() {
        if (FHIRConstants.JSON_EVAL.equals(expression)){
            expressionType = ConditionsEvaluateFunction.ExpressionType.JSON;
        } else {
            expressionType = ConditionsEvaluateFunction.ExpressionType.XML;
        }
    }

    public ConditionsEvaluateFunction.ExpressionType getExpressionType() {
        return expressionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Condition> getNestedConditions() {
        return nestedConditions;
    }

    public void setNestedConditions(List<Condition> nestedConditions) {
        this.nestedConditions = nestedConditions;
    }

    public Map<String, Source> getReturnValues() {
        return returnValues;
    }

    public void addReturnValue(String key, Source returnValue) {
        returnValues.putIfAbsent(key, returnValue);
    }

    public void setReturnValues(Map<String, Source> returnValues) {
        this.returnValues = returnValues;
    }

    public Map<String, Source> getDefaultReturnValues() {
        return defaultReturnValues;
    }

    public void setDefaultReturnValues(Map<String, Source> defaultReturnValues) {
        this.defaultReturnValues = defaultReturnValues;
    }

    public void addDefaultReturnValue(String key, Source returnValue) {
        defaultReturnValues.putIfAbsent(key, returnValue);
    }
}
