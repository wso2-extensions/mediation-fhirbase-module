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

package org.wso2.healthcare.integration.fhir.utils;

import org.hl7.fhir.r4.model.Extension;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.model.ExtendedProfile;
import org.wso2.healthcare.integration.fhir.model.Resource;

import java.util.Map;

/**
 * This is utility class containing utilities for custom profiles
 */
public class FHIRProfileUtils {

    /**
     * Function to initialize common parameters / elements
     *
     * @param profile
     * @param resource
     * @param paramPrefix
     * @param parameters
     * @throws FHIRConnectException
     */
    public static void initProfile(ExtendedProfile profile, Resource resource,
                                   String paramPrefix, Map<String, String> parameters) throws FHIRConnectException {
        if (!parameters.containsKey( paramPrefix + "meta.profile")) {
            //If user haven't provided profile url, we have to populate
            parameters.put(paramPrefix + "meta.profile", profile.getProfileURL());
            resource.unwrap().getMeta().addProfile(profile.getProfileURL());
        }
    }

    /**
     * Util function to create extension data object enclosing valu[x] with given url and given value data type.
     * The content of the value[x] will be loaded from parameters
     *
     * @param url
     * @param dataType
     * @param prefix
     * @param params
     * @param connectorContext
     * @return
     * @throws FHIRConnectException
     */
    public static Extension createExtensionObjByValueType(String url, String dataType, String prefix, Map<String, String> params,
                                                    FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Extension extension = new Extension();
        FHIRDataTypeUtils.populateElement(prefix, extension, params, connectorContext);

        TypeFactory typeFactory = FHIRDataTypeUtils.getTypeMap().get(dataType);
        if (typeFactory != null) {
            extension.setValue(typeFactory.createObject(prefix, params, connectorContext));
        } else {
            throw new FHIRConnectException("Unknown Data Type : " + dataType);
        }
        if (extension.isEmpty()) return null;
        // Setting URL at last, since the url is predefined. so unable to do empty check
        extension.setUrl(url);
        return extension;
    }

}
