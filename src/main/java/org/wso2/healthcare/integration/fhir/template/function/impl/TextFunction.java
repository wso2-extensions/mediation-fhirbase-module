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

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.template.function.AbstractTemplateFunction;

import java.util.ArrayList;
import java.util.List;


/**
 * This function is used to insert a hardcoded value to the template resource. syntax: text(<value>)
 */
public class TextFunction extends AbstractTemplateFunction {

    public TextFunction() {
        this.setFuncName("text");
        this.setFunctionType(Type.POSTPROCESS);
    }

    @Override
    public List evaluate(MessageContext messageContext, List arguments) {
        List<String> results = new ArrayList<>();
        results.add(getFunctionInput());
        return results;
    }

    @Override
    public void setInput(String input) {
        this.setFunctionInput(input);
    }

    @Override
    public String getInput() {
        return null;
    }
}
