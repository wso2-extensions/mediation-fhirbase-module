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
package org.wso2.healthcare.integration.fhir.model.type;

/**
 * Wrapper for FHIR base types(Type, Element)
 * @param <T> base type
 */
public class BaseType<T> {

    private T baseType;

    public BaseType(T baseType) {
        this.baseType = baseType;
    }

    public T getBaseType() {
        return baseType;
    }

    public void setBaseType(T baseType) {
        this.baseType = baseType;
    }
}
