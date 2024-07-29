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
import org.wso2.healthcare.integration.common.config.model.FHIRPaginationConfig;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
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
 * _count search parameter implementation
 */
public class CountSearchParameter extends AbstractSearchControlParameter {

    public CountSearchParameter() {

        super(Constants.FHIR_SEARCH_PARAM_COUNT);
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI,
                                                     FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {
        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();
        // Validation and set default
        QueryParamInfo countParam = requestInfo.getHttpInfo().findQueryParam(getName());
        FHIRPaginationConfig paginationConfig =
                HealthcareIntegratorConfig.getInstance().getFHIRServerConfig().getPaginationConfig();
        SimpleSearchParameterInfo countParamInfo;
        if (countParam != null) {
            try {
                // validation
                int count = Integer.parseInt(countParam.getValue());
                if (count <= paginationConfig.getMaxPageSize()) {
                    countParamInfo = new SimpleSearchParameterInfo(getName(), countParam.getValue());

                } else {
                    // if requested count is greater than the maximum page size, we override the parameter with
                    // configured max page size
                    countParamInfo = new SimpleSearchParameterInfo(getName(),
                            String.valueOf(paginationConfig.getMaxPageSize()));
                }

            } catch (NumberFormatException e) {
                String msg = "Invalid \"_count\" value: " + countParam.getValue();
                throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                        OpenHealthcareFHIRException.IssueType.PROCESSING,
                        OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
            }
        } else {
            // populate default
            countParamInfo = new SimpleSearchParameterInfo(getName(), String.valueOf(paginationConfig.getDefaultPageSize()));
        }
        searchParameterInfoList.add(countParamInfo);
        // populate the synapse parameter
        messageContext.setProperty(Constants.FHIR_SYNAPSE_PROP_COUNT, countParamInfo.getValue());
        return searchParameterInfoList;
    }

    @Override
    public void postProcess(SearchParameterInfo searchParameterInfo,
                            AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException {

    }

    @Override
    public boolean canPreProcess(HTTPInfo httpInfo) {
        // Always we need to set process count search parameter to set default value
        return true;
    }
}
