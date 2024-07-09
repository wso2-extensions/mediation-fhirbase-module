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

package org.wso2.healthcare.integration.common.utils;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;

/**
 * Utility class containing Open Healthcare specific utilities
 */
public class HealthcareUtils {

    /**
     * Utility function to generate name of internal synapse property name
     *
     * @param propertyName Functionality specific name given to the property (without internal prefix and suffix)
     * @return complete property name with internal suffix and prefix
     */
    public static String createInternalSynapsePropertyName (String propertyName) {
        return Constants.OH_INTERNAL_PROPERTY_PREFIX + propertyName + Constants.OH_INTERNAL_PROPERTY_SUFFIX;
    }

    /**
     * Utility function to generate name of internal synapse property name
     *
     * @param propertyName Functionality specific name given to the property (without internal prefix and suffix)
     * @return complete property name with internal suffix and prefix
     */
    public static String createOHSynapsePropertyName (String propertyName) {
        return Constants.ON_PROPERTY_PREFIX + propertyName;
    }

    /**
     * Utility function to retrieve processed FHIR resource
     *
     * @param messageContext synapse message context
     * @return currently processed FHIR resource
     */
    public static String getProcessedFHIRResource(MessageContext messageContext) {
        return (String) messageContext.getProperty(Constants.OH_INTERNAL_FHIR_RESOURCE);
    }

    /**
     * Utility function to generated code system URI for given suffix
     *
     * @param suffix
     * @return
     */
    public static String generateCodeSystem(String suffix) {
        String uriPrefix =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getHealthcareIntegratorConfig().
                        getFHIRServerConfig().getTerminologyConfig().getCodeSystemURIPrefix();
        return uriPrefix + "CodeSystem/" + suffix;
    }
}
