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

package org.wso2.healthcare.integration.fhir.template.function;

import org.apache.commons.lang3.StringUtils;
import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class is used to parse template functions.
 */
public class TemplateFunctionLexer {

    private String dataElement;
    private List<String> segments;
    private Stack<TemplateFunction> functionStack;

    /**
     * This method is used to traverse along the source input value to parse a function if available
     *
     * @param sourceInput Source input text
     */
    public void traverse(String sourceInput) throws TemplateFunctionException {

        segments = new ArrayList<>();
        functionStack = new Stack<>();
        States state = States.STARTING;
        StringBuilder sb = new StringBuilder("");
        if (sourceInput != null) {
            for (int i = 0; i < sourceInput.length(); i++) {
                char currentChar = sourceInput.charAt(i);
                switch (currentChar) {
                    case '(':
                        if (state == States.STARTING) {
                            state = States.CONTENT;
                        } else if (state == States.TOKEN) {
                            state = States.CONTENT;
                            String functionSegment = preprocessSegment(sb.toString());
                            TemplateFunctionFactory.getInstance().createFunction(
                                    getFunctionNameFromSegment(functionSegment));
                            if (containsInbuiltTemplateFunction(functionSegment)) {
                                segments.add(sb.toString().trim());
                                sb.delete(0, sb.length());
                            } else {
                                state = States.TOKEN;
                                sb.append(currentChar);
                            }
                        }
                        break;
                    case ')':
                        if (state == States.STARTING) {
                            throw new TemplateFunctionException("Erroneous source input syntax identified.");
                        } else if (state == States.TOKEN) {
                            String trimmedStr = sb.toString().trim();
                            if (StringUtils.countMatches(trimmedStr, ")") < StringUtils.countMatches(
                                    trimmedStr, "(")) {
                                sb.append(currentChar);
                                state = States.TOKEN;
                            } else {
                                segments.add(sb.toString().trim());
                                sb.delete(0, sb.length());
                                state = States.CONTENT;
                            }
                        }
                        break;
                    default:
                        state = States.TOKEN;
                        sb.append(currentChar);
                }
            }
        }
        resolveSegments();
    }

    private void resolveSegments() {
        if (segments != null) {
            for (String segment : segments) {
                String functionSegment = preprocessSegment(segment);
                TemplateFunction function = TemplateFunctionFactory.getInstance().createFunction(
                        getFunctionNameFromSegment(functionSegment));
                this.updateFunctionInputForNestedFunctions(functionSegment);
                if (function != null) {
                    functionStack.push(function);
                } else {
                    dataElement = segment;
                    if (functionStack.size() > 0) {
                        functionStack.peek().setInput(segment);
                    }
                }
            }
        }
    }

    private String preprocessSegment(String segment) {
        StringBuilder preprocessedSegment = new StringBuilder("");
        if (segment.contains(",")) {
            String[] splits = segment.split(",s*");
            for (String split : splits) {
                preprocessedSegment.append(split).append(",");
            }
            return preprocessedSegment.substring(0, preprocessedSegment.lastIndexOf(",")).trim();
        }
        return segment.trim();
    }

    private String getFunctionNameFromSegment(String segment) {
        if (segment.contains(",")) {
            return segment.substring(segment.lastIndexOf(",") + 1);
        }
        return segment;
    }

    private void updateFunctionInputForNestedFunctions(String functionSegment) {

        if (functionStack == null) return;
        if (functionSegment.endsWith(",map")) {
            functionStack.peek().setInput(functionSegment.substring(0, functionSegment.indexOf(",")));
        } else if (functionSegment.endsWith(",text")) {
            functionStack.peek().setInput(functionSegment.substring(0, functionSegment.indexOf(",")));
        } else if (functionSegment.endsWith(",array")) {
            functionStack.peek().setInput(functionSegment.substring(0, functionSegment.indexOf(",")));
        } else if (functionSegment.endsWith(",sequence")) {
            functionStack.peek().setInput(functionSegment.substring(0, functionSegment.indexOf(",")));
        } else if (functionSegment.endsWith(",condition")) {
            functionStack.peek().setInput(functionSegment.substring(0, functionSegment.indexOf(",")));
        }
    }

    public List<String> getSegments() {
        return segments;
    }

    public String getDataElement() {
        return dataElement;
    }

    public Stack<TemplateFunction> getFunctionStack() {
        return functionStack;
    }

    private boolean containsInbuiltTemplateFunction(String segment) {
        //check if native text() xpath function
        if (segment.endsWith("/text")) {
            return false;
        }
        List<String> templateFunctionSignatures = FHIRServerDataHolder.getInstance().getTemplateFunctionSignatures();
        for (String templateFunctionSignature : templateFunctionSignatures) {
            if (segment.endsWith(templateFunctionSignature)) {
                return true;
            }
        }
        return false;
    }

    private enum States {
        STARTING,
        TOKEN,
        CONTENT,
        ENDING,
    }
}
