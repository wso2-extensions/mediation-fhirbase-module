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

package org.wso2.healthcare.integration.fhir.template.util;

import org.apache.synapse.MessageContext;
import org.apache.synapse.util.xpath.SynapseJsonPath;
import org.jaxen.JaxenException;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateException;

import java.util.Collections;
import java.util.List;

/**
 * This class is used to evaluate jsonpaths for the given message context.
 */
public class JsonPathEvaluator implements Evaluator {

    private static final JsonPathEvaluator evaluator = new JsonPathEvaluator();

    public static JsonPathEvaluator getInstance() {
        return evaluator;
    }

    @Override
    public List<?> evaluate(MessageContext ctx, String path) {
        try {
            SynapseJsonPath jsonPath = new SynapseJsonPath(path);
            Object evaluatedResult = jsonPath.evaluate(ctx);
            if (evaluatedResult instanceof List) {
                return (List<?>) evaluatedResult;
            } else {
                return Collections.singletonList(evaluatedResult);
            }
        } catch (JaxenException e) {
            throw new TemplateException("Error occurred while evaluating the JSON Path for source element", e);
        }
    }
}
