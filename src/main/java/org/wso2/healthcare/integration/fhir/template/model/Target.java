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

import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRConstants;

/**
 * Data structure to hold target FHIR attribute related information.
 */
public class Target {
    private String fhirPath;
    private String targetRoot;
    private String baseType;
    private String basePath;
    private String rootType;
    private String targetDataType;
    private String sliceName;
    private ExtensionData extensionData;

    public Target(String target, String targetRoot, String baseType, String basePath, String rootType,
                  String targetDataType, String sliceName) {
        this.fhirPath = target;
        this.targetRoot = targetRoot;
        this.baseType = baseType;
        this.basePath = basePath;
        this.rootType = rootType;
        this.sliceName = sliceName;
        this.targetDataType = targetDataType;
        if (FHIRConstants.EXTENSION.equals(baseType)) {
            extensionData = new ExtensionData(fhirPath);
        }
    }

    public void setFhirPath(String fhirPath) {
        this.fhirPath = fhirPath;
    }

    public String getFhirPath() {
        return fhirPath;
    }

    public void setValueAsProperty(MessageContext context, OMElementImpl value) {
        context.setProperty(fhirPath, value);
    }

    public void setTargetRoot(String targetRoot) {
        this.targetRoot = targetRoot;
    }

    public String getTargetRoot() {
        return targetRoot;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getRootType() {
        return rootType;
    }

    public void setRootType(String rootType) {
        this.rootType = rootType;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getTargetDataType() {
        return targetDataType;
    }

    public void setTargetDataType(String targetDataType) {
        this.targetDataType = targetDataType;
    }

    public String getSliceName() {

        return sliceName;
    }

    public void setSliceName(String sliceName) {

        this.sliceName = sliceName;
    }

    public ExtensionData getExtensionData() {
        return extensionData;
    }
}
