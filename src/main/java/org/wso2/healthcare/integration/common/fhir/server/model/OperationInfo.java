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
 * Implementations Contains different operation information
 */
public abstract class OperationInfo extends AbstractSerializableInfo {

    private final String name;
    private final boolean active;

    public OperationInfo(String name, boolean active) {

        this.name = name;
        this.active = active;
    }

    /**
     * Function to retrieve name of the operation
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    @Override
    public OMElement serialize() {

        OMElement element = getRootOMElement();
        element.addChild(createSimpleElement("active", Boolean.toString(this.isActive())));

        return element;
    }

    public boolean isActive() {

        return active;
    }

}
