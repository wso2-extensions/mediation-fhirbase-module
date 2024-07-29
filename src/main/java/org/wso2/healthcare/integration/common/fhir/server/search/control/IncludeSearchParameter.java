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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.FHIRAPIInteraction;
import org.wso2.healthcare.integration.common.fhir.FHIRPath;
import org.wso2.healthcare.integration.common.fhir.FHIRUtils;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.common.fhir.server.AbstractSearchControlParameter;
import org.wso2.healthcare.integration.common.fhir.server.Profile;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.SearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.Sequence;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.IncludeSearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.QueryParamInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.search.type.ReferenceTypeSearchParameter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * pre processor implementation of _include search parameter
 */
public class IncludeSearchParameter extends AbstractSearchControlParameter {

    private static final Log LOG = LogFactory.getLog(IncludeSearchParameter.class);

    public IncludeSearchParameter() {

        super(Constants.FHIR_SEARCH_PARAM_INCLUDE);
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI, FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {
        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();
        ArrayList<QueryParamInfo> includeParams = requestInfo.getHttpInfo().findQueryParams(getName());
        for (QueryParamInfo queryParam : includeParams) {
            String paramValue = queryParam.getValue();
            String[] paramParts = paramValue.split(":");
            if (paramParts.length >= 2 && paramParts.length <= 3) {
                String sourceResource = paramParts[0];
                String searchParameter = paramParts[1];
                String targetResource = null;
                if (paramParts.length == 3) {
                    targetResource = paramParts[2];
                }

                if (!resourceAPI.getResourceName().equals(sourceResource)) {
                    String msg = "Source resource (requested under _include) : " + sourceResource + " does not match " +
                            "against requested search for resource API : " + resourceAPI.getResourceName();
                    throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                            OpenHealthcareFHIRException.IssueType.PROCESSING,
                            OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
                }

                SearchParameter parameter = resourceAPI.getSearchParameter(searchParameter);
                if (parameter == null) {
                    // Unsupported/Unknown _include search parameter part
                    String msg =
                            "Unknown search parameter : \"" + searchParameter + "\" for resource : \"" + sourceResource +
                                    " requested under _include";
                    throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                            OpenHealthcareFHIRException.IssueType.PROCESSING,
                            OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
                }

                if (parameter instanceof ReferenceTypeSearchParameter) {
                    ReferenceTypeSearchParameter refParamSetting = (ReferenceTypeSearchParameter) parameter;
                    if (StringUtils.isEmpty(targetResource)) {
                        if (refParamSetting.getTargetResources().size() == 1) {
                            targetResource = refParamSetting.getTargetResources().get(0);
                        }
                    } else if (!refParamSetting.getTargetResources().contains(targetResource)) {
                        // Unsupported/Unknown target resource
                        String msg =
                                "Unsupported/Unknown target resource : \"" + targetResource +
                                        "\" requested under _include search result parameter";
                        throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                                OpenHealthcareFHIRException.IssueType.PROCESSING,
                                OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
                    }

                    IncludeSearchParameterInfo parameterInfo = new IncludeSearchParameterInfo();
                    parameterInfo.setSourceResource(sourceResource);
                    parameterInfo.setSearchParameter(searchParameter);
                    parameterInfo.setTargetResource(targetResource);
                    parameterInfo.setSourceExpression(resourceAPI.getSearchParameter(searchParameter).getExpression());
                    searchParameterInfoList.add(parameterInfo);
                } else {
                    // Search parameter part should be of type reference
                    String msg = "Search parameter (requested under _include) : \"" + searchParameter +
                                    "\" is not reference type";
                    throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                            OpenHealthcareFHIRException.IssueType.PROCESSING,
                            OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
                }
            } else {
                String msg = "Invalid value/format for _include query parameter : " + queryParam.getValue();
                throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                        OpenHealthcareFHIRException.IssueType.PROCESSING,
                        OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER, null);
            }
        }
        return searchParameterInfoList;
    }

    @Override
    public void postProcess(SearchParameterInfo searchParameterInfo, AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException {
        Resource resource = fhirMessageContext.getContainerResource();
        if (resource instanceof Bundle) {
            Bundle bundleResource = (Bundle) resource;
            if (searchParameterInfo instanceof IncludeSearchParameterInfo) {
                IncludeSearchParameterInfo includeInfo = (IncludeSearchParameterInfo) searchParameterInfo;
                String expression = "Bundle.entry.resource." +
                        includeInfo.getSourceExpression().substring(includeInfo.getSourceResource().length() + 1);
                FHIRPath fhirPath = new FHIRPath(expression, bundleResource);
                List<Base> results = fhirPath.evaluate();

                // TODO Execute relevant sequences parallel to improve performance
                //  GIT Issue: https://github.com/wso2-enterprise/open-healthcare/issues/587
                for (Base resultElement : results) {
                    // should point a reference
                    if (resultElement instanceof Reference) {
                        // process reference and identify final resource type amd ID
                        String resourceType = null;
                        String resourceId = null;

                        Reference reference = (Reference) resultElement;
                        String refStr = reference.getReference();

                        // We only handle literal references
                        if (StringUtils.isNotBlank(refStr)) {
                            String[] splitResult = refStr.split("/");
                            if (splitResult.length == 2) {
                                // Relative URL
                                resourceId = splitResult[1].trim();
                                resourceType = splitResult[0].trim();
                            } else if (splitResult.length > 2) {
                                // May be absolute url
                                try {
                                    URL url = new URL(refStr);
                                    // check validity against RFC 2396
                                    url.toURI();

                                    resourceId = splitResult[splitResult.length - 1].trim();
                                    resourceType = splitResult[splitResult.length - 2].trim();
                                } catch (MalformedURLException | URISyntaxException e) {
                                    // URL is invalid
                                    // TODO check whether we need to add some operation outcome to the bundle if not,
                                    //  just log warning and move on
                                    //  GIT Issue : https://github.com/wso2-enterprise/open-healthcare/issues/587
                                    LOG.warn("Reference URL is invalid, hence not processed. Details : { Reference :" +
                                            refStr + " Search parameter Info :" + searchParameterInfo + " }", e);
                                }
                            } else {
                                // TODO handle invalid
                                //  GIT Issue : https://github.com/wso2-enterprise/open-healthcare/issues/587
                                LOG.warn("Failed to decode reference format : " + refStr + ". Related search param : " + searchParameterInfo);
                                continue;
                            }
                            if (resourceType != null && resourceId != null) {

                                // found something as resource type, need to check whether it is a valid or
                                // acceptable one
                                if (includeInfo.getTargetResource() != null) {
                                    if (!resourceType.equals(includeInfo.getTargetResource())) {
                                        String msg = "User has requested invalid target resource " +
                                                " for \"_indlude\" search parameter : " + searchParameterInfo;
                                        LOG.warn(msg);
                                        continue;
                                    }
                                } else {
                                    // Since pre processor haven't identified exact target resource, we need to
                                    // check whether identified resource type is accepted according to profile
                                    // definition
                                    ResourceAPI api =
                                            FHIRUtils.getResourceAPIByName(requestInfo.getFhirInfo().getResource());
                                    SearchParameter sourceSearchParam =
                                            api.getSearchParameter(includeInfo.getSearchParameter());
                                    if (!(sourceSearchParam instanceof ReferenceTypeSearchParameter) ||
                                            ((ReferenceTypeSearchParameter) sourceSearchParam).getTargetResources().contains(resourceType)) {
                                        String msg = "User has requested invalid target resource " +
                                                " for \"_indlude\" search parameter : " + searchParameterInfo;
                                        LOG.warn(msg);
                                        continue;
                                    }
                                }

                                // Check whether that target resource is already retrieved and added to the container
                                if (!(fhirMessageContext.getResourceTracker().contains(resourceType, resourceId))) {

                                    // Find target FHIR resource API
                                    ResourceAPI api = FHIRUtils.getResourceAPIByName(resourceType);

                                    if (api == null) {
                                        String msg = "FHIR resource : " + resourceType + " is not supported. hence " +
                                                "unable to serve search parameter: " + searchParameterInfo;
                                        throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                                                OpenHealthcareFHIRException.IssueType.NOT_SUPPORTED,
                                                OpenHealthcareFHIRException.Details.RESOURCE_NOT_SUPPORTED, null);
                                    }

                                    Profile profile = api.getDefaultProfile();
                                    Sequence sequence = profile.getSequenceByInteraction(FHIRAPIInteraction.READ);

                                    // Backup ID property
                                    String uriVarIdProp = (String) messageContext.getProperty(Constants.REST_SYNAPSE_PROP_ID);
                                    messageContext.setProperty(Constants.REST_SYNAPSE_PROP_ID, resourceId);

                                    Mediator mediator = messageContext.getSequence(sequence.getName());
                                    if (mediator != null) {
                                        mediator.mediate(messageContext);
                                        // TODO ATM we assume that the integration logic will add the created
                                        //  resource to Bundle. Need to check template implementation and handle it
                                        //  here.
                                        //  GIT Issue:https://github.com/wso2-enterprise/open-healthcare/issues/587
                                    } else {
                                        LOG.warn("Unable to locate relevant sequence : " + sequence.getName());
                                    }

                                    // Restore original ID property
                                    messageContext.setProperty(Constants.REST_SYNAPSE_PROP_ID, uriVarIdProp);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDefaultValue() {

        return null;
    }
}
