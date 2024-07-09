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

import org.apache.commons.lang3.StringUtils;
import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;
import org.wso2.healthcare.integration.fhir.template.function.TemplateFunction;
import org.wso2.healthcare.integration.fhir.template.function.TemplateFunctionLexer;
import org.wso2.healthcare.integration.fhir.template.function.impl.ArrayFunction;
import org.wso2.healthcare.integration.fhir.template.function.impl.TextFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Data structure to hold source payload element related information.
 */
public class Source {
    private String expression;
    private boolean isArrayElement;
    private boolean isConstant;
    private final List<String> arrayPaths = new ArrayList<>();
    private String leafNodePath;
    private Stack<TemplateFunction> functionStack;
    private OperationType type;

    public Source(String expression, String fhirPath, OperationType type,  Map<String, String> namespaces)
            throws TemplateFunctionException {
        this.type = type;
        this.setExpression(expression);
        this.resolveFunctions();
        if (OperationType.WRITE.equals(type) && StringUtils.isNotBlank(this.expression)) {
            String[] propertyExpressions = this.expression.split(",");
            for (String propertyExpression : propertyExpressions) {
                WriteDataField writeProp = new WriteDataField(propertyExpression, fhirPath, namespaces, arrayPaths);
                if (FHIRServerDataHolder.getInstance().getPropertyPayloadModelMap().containsKey(writeProp.getPropName())) {
                    FHIRServerDataHolder.getInstance().getPropertyPayloadModelMap().get(writeProp.getPropName())
                            .addProperty(writeProp);
                } else {
                    PropertyPayloadModel propertyPayloadModel = new PropertyPayloadModel(writeProp);
                    FHIRServerDataHolder.getInstance().addPropertyPayloadModel(writeProp.getPropName(),
                            propertyPayloadModel);
                }
            }
        }
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    private void resolveFunctions() throws TemplateFunctionException {
        TemplateFunctionLexer lexer = new TemplateFunctionLexer();
        lexer.traverse(expression);
        Stack<TemplateFunction> functionStack = lexer.getFunctionStack();
        if (functionStack.size() > 0) {
            TemplateFunction topFunction = functionStack.peek();
            if (topFunction != null) {
                this.setExpression(topFunction.getInput());
                if (topFunction instanceof TextFunction) {
                    isConstant = true;
                }
            }
        }
        List<String> tempArrayPaths = new ArrayList<>();
        Stack<TemplateFunction> clonedFunctionStack = (Stack<TemplateFunction>) functionStack.clone();
        while (clonedFunctionStack.size() > 0) {
            TemplateFunction function = clonedFunctionStack.peek();
            if (function instanceof ArrayFunction) {
                if (((ArrayFunction) function).getArrayPath() != null) {
                    this.isArrayElement = true;
                    tempArrayPaths.add(((ArrayFunction) function).getArrayPath());
                }
                if (((ArrayFunction) function).getLeafNodePath() != null) {
                    this.leafNodePath = ((ArrayFunction) function).getLeafNodePath();
                }
            }
            clonedFunctionStack.pop();
        }
        this.functionStack = functionStack;
        //resolving array paths
        for (int i = tempArrayPaths.size() - 1; i >= 0; i--) {
            this.arrayPaths.add(tempArrayPaths.get(i));
        }
    }

    public String getExpression() {
        return expression;
    }

    public boolean isArrayElement() {
        return isArrayElement;
    }

    public List<String> getArrayPaths() {
        return arrayPaths;
    }

    public String getLeafNodePath() {
        return leafNodePath;
    }

    public Stack<TemplateFunction> getFunctionStack() {
        return functionStack;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public enum OperationType {
        READ,
        WRITE
    }
}
