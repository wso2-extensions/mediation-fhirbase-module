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

package org.wso2.healthcare.integration.common.fhir.server.search.control;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.common.fhir.server.AbstractSearchControlParameter;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.QueryParamInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SimpleSearchParameterInfo;

import java.util.ArrayList;

/**
 * _offset (WSO2 specific) search parameter implementation
 */
public class OffsetSearchParameter extends AbstractSearchControlParameter {

    public OffsetSearchParameter() {

        super(Constants.FHIR_SEARCH_PARAM_OFFSET);
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI,
                                                     FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {
        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();
        SimpleSearchParameterInfo offsetParamInfo;
        // Validation and set default
        QueryParamInfo offsetParam = requestInfo.getHttpInfo().findQueryParam(getName());
        if (offsetParam == null) {
            offsetParamInfo = new SimpleSearchParameterInfo(getName(), "0");
        } else {
            offsetParamInfo = new SimpleSearchParameterInfo(getName(), offsetParam.getValue());
        }
        searchParameterInfoList.add(offsetParamInfo);
        // populate the synapse parameter
        messageContext.setProperty(Constants.FHIR_SYNAPSE_PROP_OFFSET, offsetParamInfo.getValue());
        return searchParameterInfoList;
    }

    @Override
    public void postProcess(SearchParameterInfo searchParameterInfo,
                            AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException {

    }

    @Override
    public boolean canPreProcess(HTTPInfo httpInfo) {
        // Always we need to set process offset search parameter to set default value
        return true;
    }
}
