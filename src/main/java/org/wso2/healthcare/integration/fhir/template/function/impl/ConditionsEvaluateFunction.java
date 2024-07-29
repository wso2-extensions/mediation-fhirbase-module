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

package org.wso2.healthcare.integration.fhir.template.function.impl;

import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;
import org.wso2.healthcare.integration.fhir.template.function.AbstractTemplateFunction;
import org.wso2.healthcare.integration.fhir.template.function.TemplateFunctionExecutor;
import org.wso2.healthcare.integration.fhir.template.model.Condition;
import org.wso2.healthcare.integration.fhir.template.model.ConditionMapping;
import org.wso2.healthcare.integration.fhir.template.model.ConditionResult;
import org.wso2.healthcare.integration.fhir.template.model.ConditionsMappingModel;
import org.wso2.healthcare.integration.fhir.template.model.Source;
import org.wso2.healthcare.integration.fhir.template.util.JsonPathEvaluator;
import org.wso2.healthcare.integration.fhir.template.util.MsgCtxUtil;
import org.wso2.healthcare.integration.fhir.template.util.XPathEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This function is used to execute a condition on source payload element. syntax: condition(<condition>,<element_path>)
 */
public class ConditionsEvaluateFunction extends AbstractTemplateFunction {

    private final TemplateFunctionExecutor templateFunctionExecutor = new TemplateFunctionExecutor();

    private String conditionName;
    private String returnValueKey;
    private String expression;
    private List<Condition> conditions;
    private Map<String, Source> defaultReturnValues;

    public ConditionsEvaluateFunction() {
        this.setFuncName("condition");
        this.setFunctionType(Type.POSTPROCESS);
        conditions = new ArrayList<>();
    }

    @Override
    public List evaluate(MessageContext messageContext, List arguments) throws TemplateFunctionException {
        ConditionResult conditionResult = resolveConditions(messageContext, conditions);
        if (arguments.size() > 0) {
            List<?> evaluatedResults = (ArrayList<?>) arguments.get(0);
            List<?> modifiedResults = new ArrayList<>();
            if (conditionResult.isTrue()) {
                if (conditionResult.hasReturnValues()) {
                    Source source = conditionResult.getEvaluatedCondition().getReturnValues().get(returnValueKey);
                    evaluatedResults = templateFunctionExecutor.execute(messageContext, source,
                            MsgCtxUtil.evaluateExpression(messageContext, source));
                }
            } else if (conditionResult.getEvaluatedCondition() != null) {
                if (conditionResult.getEvaluatedCondition().getDefaultReturnValues().containsKey(returnValueKey)) {
                    Source source = conditionResult.getEvaluatedCondition().getDefaultReturnValues().get(returnValueKey);
                    evaluatedResults = templateFunctionExecutor.execute(messageContext, source,
                            MsgCtxUtil.evaluateExpression(messageContext, source));
                }
            } else if (defaultReturnValues.containsKey(returnValueKey)) {
                Source source = defaultReturnValues.get(returnValueKey);
                evaluatedResults = templateFunctionExecutor.execute(messageContext, source,
                        MsgCtxUtil.evaluateExpression(messageContext, source));
            }
            for (Object evaluatedResult : evaluatedResults) {
                modifiedResults.addAll((ArrayList) evaluatedResult);
            }
            return modifiedResults;
        }
        return null;
    }

    @Override
    public void setInput(String input) {
        this.setFunctionInput(input);
        if (input.contains(",")) {
            this.conditionName = input.substring(0, input.indexOf(","));
            this.returnValueKey = input.substring(input.indexOf(",") + 1);
            ConditionsMappingModel conditionsMappingModel = FHIRServerDataHolder.getInstance().getConditionsMappingModel();
            ConditionMapping conditionMapping = conditionsMappingModel.getConditionMappingMap().get(conditionName);
            if (conditionMapping != null) {
                conditions = conditionMapping.getConditions();
                defaultReturnValues = conditionMapping.getDefaultReturnValues();
            }
        }
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    private ConditionResult resolveConditions(MessageContext messageContext, List<Condition> conditions) {

        List evaluatedResult;
        Condition lastEvaluatedCondition = null;
        for (Condition condition : conditions) {
            String expression = condition.getExpression();
            List<Condition> nestedConditions = condition.getNestedConditions();
            if (expression != null) {
                if (ExpressionType.JSON.equals(condition.getExpressionType())) {
                    evaluatedResult = JsonPathEvaluator.getInstance().evaluate(messageContext, expression);
                } else {
                    evaluatedResult = XPathEvaluator.getInstance().evaluate(messageContext, expression);
                }
                if (evaluatedResult != null) {
                    Object evaluatedResultObj = evaluatedResult.get(0);
                    String result;
                    if (evaluatedResultObj instanceof OMTextImpl) {
                        result = ((OMTextImpl) evaluatedResultObj).getText();
                    } else {
                        result = String.valueOf(evaluatedResult.get(0));
                    }
                    if (result.equals(condition.getValue())) {
                        if (nestedConditions.size() > 0) {
                            return resolveConditions(messageContext, nestedConditions);
                        } else {
                            return new ConditionResult(condition, true);
                        }
                    } else {
                        lastEvaluatedCondition = condition;
                    }
                }
            }
        }
        return new ConditionResult(lastEvaluatedCondition, false);
    }

    public enum ExpressionType {
        JSON,
        XML
    }
}
