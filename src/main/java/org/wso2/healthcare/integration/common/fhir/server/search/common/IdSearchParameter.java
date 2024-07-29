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

package org.wso2.healthcare.integration.common.fhir.server.search.common;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.AbstractCommonSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SimpleSearchParameterInfo;

import java.util.ArrayList;

/**
 * _id search parameter
 */
public class IdSearchParameter extends AbstractCommonSearchParameter {

    public IdSearchParameter() {

        super(Constants.FHIR_SEARCH_PARAM_ID);
    }

    @Override
    public String getDefaultValue() {

        return null;
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI,
                                                     FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {
        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();
        String id = requestInfo.getHttpInfo().findQueryParam(Constants.FHIR_SEARCH_PARAM_ID).getValue();
        searchParameterInfoList.add(new SimpleSearchParameterInfo(Constants.FHIR_SEARCH_PARAM_ID, id));

        // populate synapse properties for backward compatibility
        messageContext.setProperty(Constants.REST_SYNAPSE_PROP_ID, id);
        messageContext.setProperty(Constants.FHIR_SYNAPSE_PROP_ID, id);

        return searchParameterInfoList;
    }

    @Override
    public void postProcess(SearchParameterInfo searchParameterInfo,
                            AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException {
    }
}
