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
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.context.AbstractMessageContextCreator;
import org.wso2.healthcare.integration.common.context.HealthcareMessageContext;
import org.wso2.healthcare.integration.common.context.MessageContextType;
import org.wso2.healthcare.integration.common.fhir.FHIRUtils;
import org.wso2.healthcare.integration.common.fhir.server.model.DataServiceInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.OperationInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.QueryParamInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.search.control.IncludeSearchParameter;
import org.wso2.healthcare.integration.common.utils.SynapseUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is responsible to preprocess the incoming FHIR API requests and pre process and create meta information
 * to be used by the API implementations
 */
public class FHIRAPIPreprocessor {

    private static final Log LOG = LogFactory.getLog(FHIRAPIPreprocessor.class);

    /**
     * Function which execute the main processing task
     *
     * @param synapseMsgCtx
     */
    public void process(MessageContext synapseMsgCtx) throws OpenHealthcareException {
        HealthcareIntegratorEnvironment integratorEnvironment =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        HealthcareIntegratorConfig config = integratorEnvironment.getHealthcareIntegratorConfig();

        // check whether preprocessor is enable or not
        if (!config.getFHIRServerConfig().getFhirPreprocessorConfig().isEnable()) return;

        String fhirResourceName = (String) synapseMsgCtx.getProperty(Constants.OH_INTERNAL_FHIR_RESOURCE);
        if (fhirResourceName != null) {
            ResourceAPI resourceAPI = integratorEnvironment.getFHIRAPIStore().getAPI(fhirResourceName);

            // Create request info
            FHIRRequestInfo requestInfoObj = createBaseRequestInfo(resourceAPI, synapseMsgCtx);

            // process search parameters
            ArrayList<SearchParameterInfo> searchParameterInfoList =
                    processSearchParameters(resourceAPI, requestInfoObj, synapseMsgCtx);
            requestInfoObj.getFhirInfo().addSearchParameterInfo(searchParameterInfoList);

            // process operations
            ArrayList<OperationInfo> operationInfoList = processOperations(resourceAPI, requestInfoObj, synapseMsgCtx);
            requestInfoObj.getFhirInfo().addOperationInfo(operationInfoList);

            // Process data services
            if (integratorEnvironment.getHealthcareIntegratorConfig().getFHIRServerConfig().
                    getFhirPreprocessorConfig().isEnableDataServiceQueryGen()) {
                DataServiceInfo dsInfo = processDataServices(resourceAPI, requestInfoObj);
                requestInfoObj.getFhirInfo().setDataService(dsInfo);
                synapseMsgCtx.setProperty(Constants.OH_PROP_FHIR_BE_DS_QUERY, dsInfo.getQueryString());
            }

            // Set request info to the message context
            FHIRUtils.setFHIRRequestInfo(requestInfoObj, synapseMsgCtx);
            FHIRUtils.setFHIRRequestInfoXML(requestInfoObj, synapseMsgCtx);

            // populate fhir healthcare message contexts
            populateHealthcareMessageContexts(integratorEnvironment, synapseMsgCtx);
        } else {
            // FHIR resource is not available means the handler is not engaged or disabled
            if (LOG.isDebugEnabled()) {
                LOG.debug("FHIR resource is not set to " + Constants.OH_INTERNAL_FHIR_RESOURCE + " property, hence " +
                        "consider as this feature is disabled");
            }
        }
    }

    private FHIRRequestInfo createBaseRequestInfo(ResourceAPI fhirAPI, MessageContext msgCtx) throws OpenHealthcareException {
        return FHIRRequestInfo.build(msgCtx, fhirAPI);
    }

    private ArrayList<SearchParameterInfo> processSearchParameters(ResourceAPI fhirAPI,
                                                                   FHIRRequestInfo requestInfo,
                                                                   MessageContext msgCtx) throws OpenHealthcareException {
        ArrayList<SearchParameterInfo> searchParamInfoList = new ArrayList<>();
        FHIRAPIStore fhirapiStore =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getFHIRAPIStore();
        Iterator<QueryParamInfo> queryParameters = requestInfo.getHttpInfo().getQueryParameters();
        ArrayList<String> processedQueryParams = new ArrayList<>();
        while (queryParameters.hasNext()) {
            QueryParamInfo queryParam = queryParameters.next();

            // One query parameter is only processed once if repeating. Relevant implementation must handle multiple
            // occurrences of the search parameter
            if (processedQueryParams.contains(queryParam.getName())) {
                continue;
            } else {
                processedQueryParams.add(queryParam.getName());
            }

            // check whether Search control parameters
            SearchParameter searchParameter = fhirapiStore.getSearchControlParameter(queryParam.getName());
            if (searchParameter == null) {

                // check whether common search parameters
                searchParameter = fhirapiStore.getCommonSearchParameter(queryParam.getName());
                if (searchParameter == null) {

                    // check whether API level search parameters
                    searchParameter = fhirAPI.getSearchParameter(queryParam.getName());
                    if (searchParameter == null) {

                        // check whether profile specific search parameters
                        String profileUri = requestInfo.getFhirInfo().getProfile();
                        searchParameter = fhirAPI.getProfile(profileUri).getSearchParameter(queryParam.getName());

                        if (searchParameter == null) {
                            // unsupported/unknown search parameter
                            String msg = "Unknown search parameter : \"" + queryParam.getName() +
                                    "\" for resource type : \"" + fhirAPI.getResourceName() +
                                    "\" for profile : " + profileUri;
                            throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                                    OpenHealthcareFHIRException.IssueType.PROCESSING,
                                    OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
                        }
                    }
                }
            }

            if (searchParameter.canPreProcess(requestInfo.getHttpInfo())) {
                searchParamInfoList.addAll(searchParameter.preProcess(fhirAPI, requestInfo, msgCtx));
            }
        }
        return searchParamInfoList;
    }

    private ArrayList<OperationInfo> processOperations (ResourceAPI fhirAPI,
                                                        FHIRRequestInfo requestInfo,
                                                        MessageContext msgCtx) throws OpenHealthcareException {
        ArrayList<OperationInfo> operationInfoList = new ArrayList<>();

        String restPath = SynapseUtils.getRestFullRequestPath(msgCtx);
        String[] pathParts = restPath.split("\\$");
        if (pathParts.length == 2) {
            // reach here means, there is an operation

            String[] operationAndQueryParts = pathParts[1].split("\\?");
            String operation = operationAndQueryParts[0];

            // find operation
            Operation operationImpl = findOperationImpl(fhirAPI, operation);
            if (operationImpl != null) {
                if (operationImpl.canProcess(requestInfo.getHttpInfo())) {
                    OperationInfo operationInfo = operationImpl.preProcess(fhirAPI, requestInfo.getHttpInfo(), msgCtx);
                    if (operationInfo != null) {
                        operationInfoList.add(operationInfo);
                    }
                }
            } else {
                // Unsupported operation by the API
                String message = "Unsupported Operation \"" + operation + "\" on resource " + fhirAPI.getResourceName();
                throw new OpenHealthcareFHIRException(message, OpenHealthcareFHIRException.Severity.ERROR,
                        OpenHealthcareFHIRException.IssueType.INVALID,
                        OpenHealthcareFHIRException.Details.INVALID_OPERATION, null);
            }
        }
        return operationInfoList;
    }

    private DataServiceInfo processDataServices(ResourceAPI fhirAPI,
                                                FHIRRequestInfo requestInfo) {
        DataServiceInfo dataServiceInfo = new DataServiceInfo();
        HTTPInfo httpInfo = requestInfo.getHttpInfo();
        FHIRAPIStore apiStore =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getFHIRAPIStore();

        StringBuilder queryBuilder = new StringBuilder();

        // handle common search parameters
        appendDSQueryParam(queryBuilder, apiStore.getAllCommonSearchParameters(), httpInfo);

        // handle control search parameters
        appendDSQueryParam(queryBuilder, apiStore.getAllSearchControlParameters(), httpInfo);

        // handle API level search parameters
        appendDSQueryParam(queryBuilder, fhirAPI.getSearchParametersIterator(), httpInfo);

        // handle profile level search parameters
        Profile profile = fhirAPI.getProfile(requestInfo.getFhirInfo().getProfile());
        appendDSQueryParam(queryBuilder, profile.getSearchParameters(), httpInfo);

        dataServiceInfo.setQueryString(queryBuilder.toString());

        return dataServiceInfo;
    }

    private void appendDSQueryParam(StringBuilder dsQueryParamBuilder, Iterator<SearchParameter> searchParamIterator,
                                    HTTPInfo httpInfo) {
        while (searchParamIterator.hasNext()) {
            SearchParameter parameter = searchParamIterator.next();

            // TODO _include search parameter will not be added to the DS query
            if (parameter instanceof IncludeSearchParameter) {
                continue;
            }
            QueryParamInfo requestQueryParam = httpInfo.findQueryParam(parameter.getName());
            if (dsQueryParamBuilder.length() != 0) {
                dsQueryParamBuilder.append('&');
            }
            if (requestQueryParam != null) {
                dsQueryParamBuilder.append(requestQueryParam.getName()).append('=').append(requestQueryParam.getValue());
            } else if (parameter.getDefaultValue() != null){
                // Query param does not exists, populate default
                dsQueryParamBuilder.append(parameter.getName()).append('=').append(parameter.getDefaultValue());
            }
        }
    }

    private Operation findOperationImpl(ResourceAPI api, String operationName) {
        // check global operations
        FHIRAPIStore fhirapiStore =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getFHIRAPIStore();
        // Check for global operation
        Operation operation = fhirapiStore.getOperationByName(operationName);
        if (operation == null) {
            // check resource API level operations
            operation = api.getOperation(operationName);
        }
        return operation;
    }

    private void populateHealthcareMessageContexts(HealthcareIntegratorEnvironment environment,
                                                   MessageContext msgCtx) throws OpenHealthcareException {
        ArrayList<AbstractMessageContextCreator> creators =
                environment.getMessageContextCreators(MessageContextType.FHIR);
        if (creators != null) {
            for (AbstractMessageContextCreator creator : creators) {
                HealthcareMessageContext ctx = creator.create(msgCtx);
                msgCtx.setProperty(ctx.getStoredPropertyName(), ctx);
            }
        }
    }
}
