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

package org.wso2.healthcare.integration.common.context;

/**
 * Runtime message context holding FHIR related runtime information
 */
public interface HealthcareMessageContext {

    /**
     * Get type of the context based on the focus area used
     * types: refer {@link org.wso2.healthcare.integration.common.context.MessageContextType}
     *
     * @return
     */
    MessageContextType getType();

    /**
     * Get the name of the property which this {@link HealthcareMessageContext} to be stored
     * @return
     */
    String getStoredPropertyName();

}
