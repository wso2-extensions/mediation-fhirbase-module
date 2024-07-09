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

package org.wso2.healthcare.integration.common.fhir.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Holds information about deployed FHIR APIs
 */
public class FHIRAPIStore {

    private static final Log LOG = LogFactory.getLog(FHIRAPIStore.class);

    private final HashMap<String, org.wso2.healthcare.integration.common.fhir.server.ResourceAPI> fhirAPIs = new HashMap<>(4);
    private final HashMap<String, org.wso2.healthcare.integration.common.fhir.server.SearchParameter> commonSearchParameters = new HashMap<>(2); // Supported search parameters
    private final HashMap<String, org.wso2.healthcare.integration.common.fhir.server.SearchParameter> controlSearchParameters = new HashMap<>(2); // Search control parameters
    private final HashMap<String, Operation> operations = new HashMap<>(2); // Supported operations

    public org.wso2.healthcare.integration.common.fhir.server.ResourceAPI getAPI(String name) {
        return fhirAPIs.get(name);
    }

    public void addAPI(org.wso2.healthcare.integration.common.fhir.server.ResourceAPI resourceAPI) {
        fhirAPIs.put(resourceAPI.getResourceName(), resourceAPI);
    }

    public org.wso2.healthcare.integration.common.fhir.server.ResourceAPI removeAPI(String name) {
        return fhirAPIs.remove(name);
    }


    public void addOperation(Operation operation) {
        this.operations.put(operation.getName(), operation);
    }

    public Operation getOperationByName(String name) {
        return this.operations.get(name);
    }

    public void addCommonSearchParameter(org.wso2.healthcare.integration.common.fhir.server.SearchParameter searchParameter) {
        this.commonSearchParameters.put(searchParameter.getName(), searchParameter);
    }

    public org.wso2.healthcare.integration.common.fhir.server.SearchParameter getCommonSearchParameter(String name) {
        return this.commonSearchParameters.get(name);
    }

    public Iterator<org.wso2.healthcare.integration.common.fhir.server.SearchParameter> getAllCommonSearchParameters() {
        return this.commonSearchParameters.values().iterator();
    }

    public void addSearchControlParameter(AbstractSearchControlParameter parameter) {
        this.controlSearchParameters.put(parameter.getName(), parameter);
    }

    public org.wso2.healthcare.integration.common.fhir.server.SearchParameter getSearchControlParameter(String name) {
        return this.controlSearchParameters.get(name);
    }

    public Iterator<org.wso2.healthcare.integration.common.fhir.server.SearchParameter> getAllSearchControlParameters() {
        return this.controlSearchParameters.values().iterator();
    }

    /**
     * Find search parameter with given name
     * Depth of search depends on the parameters provided
     *
     * @param paramName (mandatory)
     * @param apiName (optional)
     * @param profile (optional)
     * @return
     * @throws OpenHealthcareFHIRException
     */
    public org.wso2.healthcare.integration.common.fhir.server.SearchParameter findSearchParameter(String paramName, String apiName, String profile) throws OpenHealthcareFHIRException {
        // check whether Search control parameters
        SearchParameter searchParameter = getSearchControlParameter(paramName);

        if (searchParameter == null) {
            // check whether common search parameters
            searchParameter = getCommonSearchParameter(paramName);

            if (searchParameter == null && apiName != null) {
                // check in API level search parameters if API name is given
                ResourceAPI fhirAPI = getAPI(apiName);
                if (fhirAPI != null) {
                    // check whether API level search parameters
                    searchParameter = fhirAPI.getSearchParameter(paramName);

                    if (searchParameter == null && profile != null) {
                        // check whether profile specific search parameters
                        searchParameter = fhirAPI.getProfile(profile).getSearchParameter(paramName);
                    }
                }
            }
        }
        if (searchParameter == null) {
            // unsupported/unknown search parameter
            LOG.warn("Failed to find search parameter : " + paramName);
        }
        return searchParameter;
    }
}
