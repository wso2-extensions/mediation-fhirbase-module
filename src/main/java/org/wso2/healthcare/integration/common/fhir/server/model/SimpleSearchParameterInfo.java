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
import org.wso2.healthcare.integration.common.Constants;

/**
 * Contains information about simple search parameter
 */
public class SimpleSearchParameterInfo extends SearchParameterInfo {

    private final String key;
    private final String value;

    public SimpleSearchParameterInfo(String key, String value) {
        super(Constants.FHIR_DATATYPE_STRING);
        this.key = key;
        this.value = value;
    }

    @Override
    public String getName() {

        return this.key;
    }

    @Override
    protected String getRootElementName() {
        return getName();
    }

    @Override
    public OMElement serialize() {
        OMElement element = getRootOMElement();
        element.addChild(getOMFactory().createOMText(value));
        return element;
    }

    public String getKey() {

        return key;
    }

    public String getValue() {

        return value;
    }
}
