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

/**
 * Holds evaluated condition and it's result.
 */
public class ConditionResult {

    private Condition evaluatedCondition;
    private boolean isTrue;

    public ConditionResult(Condition condition, boolean isTrue) {
        this.evaluatedCondition = condition;
        this.isTrue = isTrue;
    }

    public Condition getEvaluatedCondition() {
        return evaluatedCondition;
    }

    public void setEvaluatedCondition(Condition evaluatedCondition) {
        this.evaluatedCondition = evaluatedCondition;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        this.isTrue = aTrue;
    }

    public boolean hasReturnValues() {
        return evaluatedCondition.getReturnValues().size() > 0;
    }
}
