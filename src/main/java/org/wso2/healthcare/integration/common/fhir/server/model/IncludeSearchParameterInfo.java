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

package org.wso2.healthcare.integration.common.fhir.server.model;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.wso2.healthcare.integration.common.Constants;

/**
 * Holds information related to _include search parameter
 */
public class IncludeSearchParameterInfo extends SearchParameterInfo {

    private String sourceResource;
    private String sourceExpression;
    private String searchParameter;
    private String targetResource;

    public IncludeSearchParameterInfo() {
        super("SearchControl");
    }

    @Override
    public OMElement serialize() {

        OMElement element = getRootOMElement();
        element.addChild(createSimpleElement("resource", this.sourceResource));
        element.addChild(createSimpleElement("search_parameter", this.searchParameter));
        element.addChild(createSimpleElement("source_expression", this.sourceExpression));
        if (StringUtils.isNotEmpty(this.targetResource)) {
            element.addChild(createSimpleElement("target_resource", this.targetResource));
        }
        return element;
    }

    @Override
    protected String getRootElementName() {
        return Constants.FHIR_SEARCH_PARAM_INCLUDE;
    }

    @Override
    public String getName() {
        return Constants.FHIR_SEARCH_PARAM_INCLUDE;
    }

    public String getSourceResource() {
        return sourceResource;
    }

    public void setSourceResource(String sourceResource) {
        this.sourceResource = sourceResource;
    }

    public String getSearchParameter() {
        return searchParameter;
    }

    public void setSearchParameter(String searchParameter) {
        this.searchParameter = searchParameter;
    }

    public String getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(String targetResource) {
        this.targetResource = targetResource;
    }

    public String getSourceExpression() {
        return sourceExpression;
    }

    public void setSourceExpression(String sourceExpression) {
        this.sourceExpression = sourceExpression;
    }

    @Override
    public String toString() {

        return "IncludeSearchParameterInfo{" +
                "sourceResource='" + sourceResource + '\'' +
                ", sourceExpression='" + sourceExpression + '\'' +
                ", searchParameter='" + searchParameter + '\'' +
                ", targetResource='" + targetResource + '\'' +
                '}';
    }
}
