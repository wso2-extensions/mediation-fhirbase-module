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

import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;
import org.wso2.healthcare.integration.fhir.template.function.AbstractTemplateFunction;
import org.wso2.healthcare.integration.fhir.template.model.KeyPairMappingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This function is used to map a value on source payload element. syntax: map(<mapping_name>,<element_path>)
 */
public class MapFunction extends AbstractTemplateFunction {

    private String mappingKey;

    public MapFunction() {
        this.setFuncName("map");
        this.setFunctionType(Type.POSTPROCESS);
    }

    @Override
    public List evaluate(MessageContext messageContext, List arguments) {
        Map<String, KeyPairMappingModel> mappingModelMap = FHIRServerDataHolder.getInstance().getMappingModelMap();
        KeyPairMappingModel mappingModel = mappingModelMap.get(mappingKey);
        if (arguments.size() == 0 || mappingModel == null) {
            return null;
        }
        List results = (List) arguments.get(0);
        List<String> modifiedResults = new ArrayList<>();
        for (Object evaluatedResult : results) {
            if (evaluatedResult instanceof List) {
                for (Object resultElement : (List<?>) evaluatedResult) {
                    String result = null;
                    if (resultElement instanceof OMTextImpl) {
                        result = ((OMTextImpl) resultElement).getText();
                    } else if (resultElement instanceof String) {
                        result = (String) resultElement;
                    } else if (resultElement instanceof OMElementImpl) {
                        result = ((OMElementImpl) resultElement).getText();
                    }
                    String mappedResult = mappingModel.getKeyPairMapping().getMappings().get(result);
                    if (StringUtils.isBlank(mappedResult)) {
                        mappedResult = StringUtils.isNotBlank(mappingModel.getKeyPairMapping().getDefaultMapping()) ?
                                mappingModel.getKeyPairMapping().getDefaultMapping() : result;
                    }
                    modifiedResults.add(mappedResult);
                }
            } else if (evaluatedResult instanceof String) {
                String mappedResult = mappingModel.getKeyPairMapping().getMappings().get((String) evaluatedResult);
                if (StringUtils.isBlank(mappedResult)) {
                    mappedResult = mappingModel.getKeyPairMapping().getDefaultMapping();
                }
                modifiedResults.add(mappedResult);
            }
        }
        return modifiedResults;
    }

    @Override
    public void setInput(String input) {
        this.setFunctionInput(input);
        setMappingKey(input);
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public void setMappingKey(String input) {
        if (input.contains(",")) {
            this.mappingKey = input.substring(0, input.indexOf(","));
        } else {
            this.mappingKey = input;
        }
    }
}
