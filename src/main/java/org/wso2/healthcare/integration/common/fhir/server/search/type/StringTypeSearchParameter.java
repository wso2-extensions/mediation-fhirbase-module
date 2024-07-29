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

package org.wso2.healthcare.integration.common.fhir.server.search.type;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.AbstractSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.QueryParamInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SimpleSearchParameterInfo;

import java.util.ArrayList;

/**
 * This implements simple search parameter (most of logic is implemented in the custom integration section)
 */
public class StringTypeSearchParameter extends AbstractSearchParameter {

    public StringTypeSearchParameter(String type) {
        super(type);
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI, FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {

        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();
        ArrayList<QueryParamInfo> queryParams = requestInfo.getHttpInfo().findQueryParams(this.getName());
        for (QueryParamInfo queryParam : queryParams) {
            SearchParameterInfo searchParameterInfo =
                    new SimpleSearchParameterInfo(queryParam.getName(), queryParam.getValue());
            searchParameterInfoList.add(searchParameterInfo);
        }
        return searchParameterInfoList;
    }
}
