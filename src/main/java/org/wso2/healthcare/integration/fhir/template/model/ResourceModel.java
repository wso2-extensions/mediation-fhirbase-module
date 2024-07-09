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

import org.apache.commons.lang.StringUtils;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data structure to hold resource template information in yaml file.
 */
public class ResourceModel {

    private String resourceType;
    private String profile;
    private String sourcePayloadPath;
    private Map<String, String> namespaces;
    private List<Element> elements;

    public ResourceModel() {
        namespaces = new HashMap<>();
        elements = new ArrayList<>();
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getSourcePayloadPath() {
        return sourcePayloadPath;
    }

    public void setSourcePayloadPath(String sourcePayloadPath) {
        this.sourcePayloadPath = sourcePayloadPath;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<Element> getElements() {
        return elements;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public void addElement(String source, String target, String targetRoot,
                           String baseType, String basePath, String rootType, String targetDataType,
                           String sliceName, Source.OperationType type) throws TemplateFunctionException {
        Element element = new Element();
        if (StringUtils.isNotBlank(source)) {
            element.setSource(new Source(source, target, type, namespaces));
        }
        element.setTarget(new Target(target, targetRoot, baseType, basePath, rootType, targetDataType, sliceName));
        elements.add(element);
    }
}
