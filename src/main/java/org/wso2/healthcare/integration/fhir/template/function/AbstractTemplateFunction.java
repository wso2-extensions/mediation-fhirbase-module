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

import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;

/**
 * Abstract implementation of {@link TemplateFunction} interface. This will hold common functionality across template
 * function implementations.
 */
public abstract class AbstractTemplateFunction implements TemplateFunction {

    private String funcName;
    private Type functionType;
    private String functionInput;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
        if (!FHIRServerDataHolder.getInstance().getTemplateFunctionSignatures().contains(funcName)) {
            FHIRServerDataHolder.getInstance().getTemplateFunctionSignatures().add(funcName);
        }
    }

    public Type getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Type functionType) {
        this.functionType = functionType;
    }

    public String getFunctionInput() {
        return functionInput;
    }

    public String getInput() {
        if (functionInput != null && functionInput.contains(",")) {
            return functionInput.substring(functionInput.indexOf(",") + 1);
        }
        return getFunctionInput();
    }

    public void setFunctionInput(String functionInput) {
        this.functionInput = functionInput;
    }

    @Override
    public TemplateFunction resolve(String functionText) {
        if (functionText.equals(this.getFuncName())) {
            return this;
        }
        return null;
    }

    public enum Type {
        PREPROCESS,
        POSTPROCESS
    }
}
