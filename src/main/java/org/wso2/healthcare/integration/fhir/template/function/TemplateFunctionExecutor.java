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

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;
import org.wso2.healthcare.integration.fhir.template.model.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class is used to execute template functions for a source element.
 */
public class TemplateFunctionExecutor {

    /**
     * This method will execute template functions for the source field input
     *
     * @param messageContext {@link MessageContext} instance
     * @param source         Source element information
     * @param results        Evaluated results for source element expression
     * @return Modified results after execution of functions
     * @throws TemplateFunctionException
     */
    public List execute(MessageContext messageContext, Source source, List results) throws TemplateFunctionException {
        Stack<TemplateFunction> functionStack = (Stack<TemplateFunction>) source.getFunctionStack().clone();
        List evaluatedResults = null;
        List runtimeArgs = new ArrayList();
        if (results.size() > 0) {
            runtimeArgs.add(results);
        }
        while (functionStack.size() > 0) {
            TemplateFunction function = functionStack.pop();
            if (evaluatedResults != null) {
                runtimeArgs.add(evaluatedResults);
            }
            if (!AbstractTemplateFunction.Type.PREPROCESS.equals(function.getFunctionType())) {
                evaluatedResults = function.evaluate(messageContext, runtimeArgs);
            }
        }
        if (evaluatedResults == null) {
            return results;
        }
        List modifiedResults = new ArrayList<>();
        modifiedResults.add(evaluatedResults);
        return modifiedResults;
    }
}
