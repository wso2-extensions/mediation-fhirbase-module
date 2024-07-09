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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import javax.xml.namespace.QName;

/**
 * Parent class of all {@link org.wso2.healthcare.integration.common.fhir.server.model.SerializableInfo} implementations
 */
public abstract class AbstractSerializableInfo implements SerializableInfo {

    protected static final OMFactory omFactory = OMAbstractFactory.getOMFactory();

    protected abstract String getRootElementName();

    protected OMElement getRootOMElement() {
        return getOMFactory().createOMElement(new QName(getRootElementName()));
    }

    protected static OMFactory getOMFactory() {
        return omFactory;
    }

    protected static OMElement createSimpleElement (String tagName, String value) {
        OMElement element = getOMFactory().createOMElement(new QName(tagName));
        element.addChild(getOMFactory().createOMText(value));
        return element;
    }
}
