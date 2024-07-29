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

import java.util.List;

/**
 * This interface is used to implement template specific functions that can be run against on source inputs.
 */
public interface TemplateFunction {

    /**
     * Performs runtime evaluation of the function and returns results as list of object.
     *
     * @param messageContext {@link MessageContext} instance
     * @param arguments      Runtime arguments that is used to process results. i.e intermediate payload objects
     * @return List of modified results after processing
     * @throws TemplateFunctionException
     */
    List evaluate(MessageContext messageContext, List arguments) throws TemplateFunctionException;

    /**
     * Returns function concrete object when the function name is provided.
     *
     * @param functionText function name
     * @return returns function object
     */
    TemplateFunction resolve(String functionText);

    /**
     * Sets function input parameters within the function declaration. This happens at the template compile time.
     *
     * @param input string contains input parameters in comma separated manner
     */
    void setInput(String input);

    /**
     * Returns function input parameters string
     *
     * @return string contains input parameters in comma separated manner
     */
    String getInput();

    /**
     * Returns function's type based on the evaluation behaviour. PREPROCESS type function is evaluated before source
     * expression evaluation and POSTPROCESS type function is evaluated against already evaluated source expression.
     *
     * @return Function type
     */
    default AbstractTemplateFunction.Type getFunctionType() {
        return AbstractTemplateFunction.Type.PREPROCESS;
    }
}
