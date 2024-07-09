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
package org.wso2.healthcare.integration.fhir.internal;

import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.template.model.ConditionsMappingModel;
import org.wso2.healthcare.integration.fhir.template.model.KeyPairMappingModel;
import org.wso2.healthcare.integration.fhir.template.model.PropertyPayloadModel;
import org.wso2.healthcare.integration.fhir.template.model.ResourceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to store the runtime data objects needed for OH integration components.
 */
public class FHIRServerDataHolder {

    private final Map<String, ResourceModel> resourceModelMapForReadOp = new HashMap<>();
    private final Map<String, ResourceModel> resourceModelMapForWriteOp = new HashMap<>();
    private final Map<String, KeyPairMappingModel> mappingModelMap = new HashMap<>();
    private final ConditionsMappingModel conditionsMappingModel = new ConditionsMappingModel();
    private final List<String> templateFunctionSignatures = new ArrayList<>();
    private final Map<String, PropertyPayloadModel> propertyPayloadModelMap = new HashMap<>();

    private static FHIRServerDataHolder dataHolder = new FHIRServerDataHolder();

    public static FHIRServerDataHolder getInstance() {
        return dataHolder;
    }

    public Map<String, ResourceModel> getResourceModelMapForReadOp() {
        return resourceModelMapForReadOp;
    }

    public Map<String, ResourceModel> getResourceModelMapForWriteOp() {
        return resourceModelMapForWriteOp;
    }

    public Map<String, KeyPairMappingModel> getMappingModelMap() {
        return mappingModelMap;
    }

    public ConditionsMappingModel getConditionsMappingModel() {
        return conditionsMappingModel;
    }

    public List<String> getTemplateFunctionSignatures() {
        return templateFunctionSignatures;
    }

    public void addPropertyPayloadModel(String propertyName, PropertyPayloadModel propertyPayloadModel) {
        propertyPayloadModelMap.put(propertyName, propertyPayloadModel);
    }

    public Map<String, PropertyPayloadModel> getPropertyPayloadModelMap() {
        return propertyPayloadModelMap;
    }

    public Map<String, PropertyPayloadModel> getClonedPropertyPayloadModelMap() throws FHIRConnectException {
        Map<String, PropertyPayloadModel> propertyPayloadModelMap = new HashMap<>();
        for (String propertyModelName : this.propertyPayloadModelMap.keySet()) {
            try {
                propertyPayloadModelMap.put(propertyModelName,
                        (PropertyPayloadModel) this.propertyPayloadModelMap.get(propertyModelName).clone());
            } catch (CloneNotSupportedException e) {
                throw new FHIRConnectException("Error occurred while cloning write payload model", e);
            }
        }
        return propertyPayloadModelMap;
    }
}
