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

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Search parameter interface
 */
public interface SearchParameter {

    /**
     * Function to retrieve name of the search parameter
     * this should be same name given in the query parameter
     */
    String getName();

    /**
     * Function to retrieve the type of the search parameter
     *
     * @return
     */
    String getType();

    /**
     * Function to retrieve expression bound with the search parameter
     *
     * @return
     */
    String getExpression();

    /**
     * Function to check whether the search parameter is active or not
     *
     * @return
     */
    boolean isActive();

    /**
     * Function to check whether the search parameter is bounded to a specific profile
     *
     * @return
     */
    boolean isBoundedToProfile();

    /**
     * Function to retrieve bounded profiles
     *
     * @return
     */
    Iterator<String> getProfiles();

    /**
     * Function to retrieve default value to assign if parameter not present
     *
     * @return
     */
    String getDefaultValue();

    /**
     * Check whether the search parameter can be engaged based on the http request
     *
     * @param httpInfo HTTP request information
     * @return returns true if required parameters available in the HTTP request
     */
    boolean canPreProcess(HTTPInfo httpInfo);

    /**
     * Function to perform preprocess
     *
     * @param resourceAPI
     * @param requestInfo
     * @param messageContext
     * @return
     */
    ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI, FHIRRequestInfo requestInfo,
                                              MessageContext messageContext) throws OpenHealthcareFHIRException;

    /**
     * Function to perform post process
     *
     * @param searchParameterInfo
     * @param fhirMessageContext
     * @param requestInfo
     * @param messageContext
     * @throws OpenHealthcareFHIRException
     */
    void postProcess(SearchParameterInfo searchParameterInfo, AbstractFHIRMessageContext fhirMessageContext,
                     FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException;
}
