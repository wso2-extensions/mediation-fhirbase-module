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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds extensions information for a target element referred to a fhir attribute.
 */
public class ExtensionData {

    private String parentExtensionUrl;
    private List<String> extensionUrls;
    //fhir path upto extension
    private String fhirPathToExtension;
    //relative fhir path from the extension
    private String fhirPathAfterExtension;

    public ExtensionData(String fhirPath) {
        if (StringUtils.isNotBlank(fhirPath)) {
            this.initialize(fhirPath);
        }
    }

    private void initialize(String fhirPath) {
        String[] extensionSegments = fhirPath.split(".extension\\(");
        extensionUrls = new ArrayList<>();
        int segmentPos = 0;
        for (String extensionSegment : extensionSegments) {
            if (segmentPos == 0) {
                fhirPathToExtension = extensionSegment;
            } else if (segmentPos == 1) {
                parentExtensionUrl = extensionSegment.substring(0, extensionSegment.indexOf(")"));
                extensionUrls.add(parentExtensionUrl);
                if (extensionSegments.length == 2) {
                    fhirPathAfterExtension = extensionSegment.substring(extensionSegment.indexOf(").") + 1);
                }
            } else if (segmentPos == extensionSegments.length - 1) {
                extensionUrls.add(extensionSegment.substring(0, extensionSegment.indexOf(")")));
                fhirPathAfterExtension = extensionSegment.substring(extensionSegment.indexOf(").") + 1);
            } else {
                extensionUrls.add(extensionSegment.substring(0, extensionSegment.indexOf(")")));
            }
            segmentPos++;
        }
    }

    public String getParentExtensionUrl() {
        return parentExtensionUrl;
    }

    public void setParentExtensionUrl(String parentExtensionUrl) {
        this.parentExtensionUrl = parentExtensionUrl;
    }

    public List<String> getExtensionUrls() {
        return extensionUrls;
    }

    public void setExtensionUrls(List<String> extensionUrls) {
        this.extensionUrls = extensionUrls;
    }

    public String getFhirPathToExtension() {
        return fhirPathToExtension;
    }

    public void setFhirPathToExtension(String fhirPathToExtension) {
        this.fhirPathToExtension = fhirPathToExtension;
    }

    public String getFhirPathAfterExtension() {
        return fhirPathAfterExtension;
    }

    public void setFhirPathAfterExtension(String fhirPathAfterExtension) {
        this.fhirPathAfterExtension = fhirPathAfterExtension;
    }
}
