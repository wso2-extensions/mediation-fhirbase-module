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
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.OperationInfo;

public interface Operation {

    /**
     * Function to retrieve name of the operation
     * this should be same name given in the operation
     */
    String getName();

    /**
     * Function to check whether the operation is active or not
     * @return
     */
    boolean isActive();

    /**
     * Check whether the operation can be engaged based on the http request
     *
     * @param httpInfo HTTP request information
     * @return returns true if required parameters available in the HTTP request
     */
    boolean canProcess(HTTPInfo httpInfo);

    /**
     * Function to perform preprocess
     *
     * @param resourceAPI
     * @param httpInfo
     * @param messageContext
     * @return
     */
    OperationInfo preProcess(ResourceAPI resourceAPI, HTTPInfo httpInfo,
                             MessageContext messageContext);

    /**
     * Function to perform post process
     *
     * @param operationInfo
     * @param fhirMessageContext
     * @param requestInfo
     * @param messageContext
     */
    void postProcess(OperationInfo operationInfo, AbstractFHIRMessageContext fhirMessageContext,
                     FHIRRequestInfo requestInfo, MessageContext messageContext);
}
