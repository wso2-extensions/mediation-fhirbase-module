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

package org.wso2.healthcare.integration.common.fhir.server.model;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.rest.RESTConstants;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.FHIRAPIInteraction;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represent derived FHIR information
 */
public class FHIRInfo extends AbstractSerializableInfo {

    public static final String FHIR_INFO_ROOT_NAME = "fhir";
    public static final HashMap<String, HashMap<String, FHIRAPIInteraction>> interactionMap = new HashMap<>();
    private String resource;
    private FHIRAPIInteraction interaction;
    private String profile;
    private final ArrayList<org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo> searchParameters = new ArrayList<>();
    private final ArrayList<org.wso2.healthcare.integration.common.fhir.server.model.OperationInfo> operations = new ArrayList<>();
    private org.wso2.healthcare.integration.common.fhir.server.model.DataServiceInfo dataService;

    static {
        HashMap<String, FHIRAPIInteraction> getInteractions = new HashMap<>(3);
        getInteractions.put("/", FHIRAPIInteraction.SEARCH);
        getInteractions.put("/{id}", FHIRAPIInteraction.READ);
        getInteractions.put("/{id}/_history", FHIRAPIInteraction.HISTORY);
        interactionMap.put("GET", getInteractions);

        HashMap<String, FHIRAPIInteraction> postInteractions = new HashMap<>(3);
        postInteractions.put("/", FHIRAPIInteraction.CREATE);
        postInteractions.put("/_search", FHIRAPIInteraction.SEARCH);
        postInteractions.put("/{id}/_history", FHIRAPIInteraction.HISTORY);
        interactionMap.put("POST", postInteractions);

        HashMap<String, FHIRAPIInteraction> putInteractions = new HashMap<>(1);
        putInteractions.put("/{id}", FHIRAPIInteraction.UPDATE);
        interactionMap.put("PUT", putInteractions);

        HashMap<String, FHIRAPIInteraction> deleteInteractions = new HashMap<>(1);
        deleteInteractions.put("/{id}", FHIRAPIInteraction.DELETE);
        interactionMap.put("DELETE", deleteInteractions);

        HashMap<String, FHIRAPIInteraction> patchInteractions = new HashMap<>(1);
        patchInteractions.put("/{id}", FHIRAPIInteraction.PATCH);
        interactionMap.put("PATCH", patchInteractions);

    }

    @Override
    protected String getRootElementName() {
        return FHIR_INFO_ROOT_NAME;
    }

    @Override
    public OMElement serialize() {
        OMElement element = getRootOMElement();
        element.addChild(createSimpleElement("resource", this.resource));
        element.addChild(createSimpleElement("profile", this.profile));
        element.addChild(createSimpleElement("interaction", this.interaction.getName()));

        OMElement searchParametersOM = getOMFactory().createOMElement(new QName("search_parameters"));
        for (org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo searchParameterInfo : this.searchParameters) {
            searchParametersOM.addChild(searchParameterInfo.serialize());
        }
        element.addChild(searchParametersOM);

        OMElement operationsOM = getOMFactory().createOMElement(new QName("operations"));
        for (org.wso2.healthcare.integration.common.fhir.server.model.OperationInfo operationInfo : this.operations) {
            operationsOM.addChild(operationInfo.serialize());
        }
        element.addChild(operationsOM);

        if (dataService != null) {
            OMElement dsOM = getOMFactory().createOMElement(new QName("data_sources"));
            dsOM.addChild(dataService.serialize());
            element.addChild(dsOM);
        }

        return element;
    }

    public static FHIRInfo build(ResourceAPI resourceAPI, MessageContext msgCtx, org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo httpInfo) throws OpenHealthcareException {
        FHIRInfo fhirInfo = new FHIRInfo();

        // populate resource
        fhirInfo.setResource(resourceAPI.getResourceName());

        // populate profile
        fhirInfo.setProfile(getRequestedProfile(resourceAPI, httpInfo));

        // populate REST interaction
        fhirInfo.setInteraction(resolveInteraction(msgCtx));

        return fhirInfo;
    }

    public void addSearchParameterInfo(org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo searchParameterInfo) {
        this.searchParameters.add(searchParameterInfo);
    }

    public void addSearchParameterInfo(ArrayList<org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo> searchParameterInfoList) {
        this.searchParameters.addAll(searchParameterInfoList);
    }

    public Iterator<SearchParameterInfo> getAllSearchParameterInfo () {
        return searchParameters.iterator();
    }

    public void addOperationInfo(ArrayList<org.wso2.healthcare.integration.common.fhir.server.model.OperationInfo> operationInfoList) {
        this.operations.addAll(operationInfoList);
    }

    public Iterator<OperationInfo> getAlOperationInfo () {
        return operations.iterator();
    }

    public String getResource() {

        return resource;
    }

    private void setResource(String resource) {

        this.resource = resource;
    }

    public FHIRAPIInteraction getInteraction() {

        return interaction;
    }

    public void setInteraction(FHIRAPIInteraction interaction) {

        this.interaction = interaction;
    }

    public String getProfile() {

        return profile;
    }

    private void setProfile(String profile) {

        this.profile = profile;
    }

    public org.wso2.healthcare.integration.common.fhir.server.model.DataServiceInfo getDataService() {

        return dataService;
    }

    public void setDataService(DataServiceInfo dataService) {

        this.dataService = dataService;
    }

    private static String getRequestedProfile(ResourceAPI resourceAPI, HTTPInfo httpInfo) throws OpenHealthcareFHIRException {

        QueryParamInfo profileParam = httpInfo.findQueryParam(Constants.FHIR_SEARCH_PARAM_PROFILE);
        if (profileParam == null) {
            return resourceAPI.getDefaultProfile().getUri();
        } else if (resourceAPI.getProfile(profileParam.getValue()) == null) {
            throw new OpenHealthcareFHIRException("Unknown profile : " + profileParam.getValue(),
                    OpenHealthcareFHIRException.Severity.ERROR, OpenHealthcareFHIRException.IssueType.INVALID,
                    OpenHealthcareFHIRException.Details.INVALID_FHIR_PROFILE, null);
        }
        return profileParam.getValue();
    }

    private static FHIRAPIInteraction resolveInteraction(MessageContext messageContext) {

        String httpMethod = (String) messageContext.getProperty(RESTConstants.REST_METHOD);
        String restUrlPattern = (String) messageContext.getProperty(RESTConstants.REST_URL_PATTERN);

        // Cleanup operation part if exists
        restUrlPattern = restUrlPattern.split("\\$")[0];

        return interactionMap.get(httpMethod).get(restUrlPattern);
    }
}
