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

/**
 * Represents HTTP query param information
 */
public class QueryParamInfo extends AbstractSerializableInfo {

    private final String name;
    private final String value;

    public QueryParamInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    protected String getRootElementName() {
        return "param";
    }

    @Override
    public OMElement serialize() {
        OMElement queryParamElement = getRootOMElement();
        queryParamElement.addAttribute(getOMFactory().createOMAttribute("name", null, this.name));
        queryParamElement.addChild(getOMFactory().createOMText(this.value));
        return queryParamElement;
    }

    public String getName() {

        return name;
    }

    public String getValue() {

        return value;
    }

    @Override
    public String toString() {
        return "QueryParamInfo {" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
