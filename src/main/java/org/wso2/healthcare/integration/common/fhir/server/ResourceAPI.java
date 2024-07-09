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

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.fhir.FHIRAPIInteraction;
import org.wso2.healthcare.integration.common.fhir.server.operations.SimpleOperation;
import org.wso2.healthcare.integration.common.fhir.server.search.type.ReferenceTypeSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.type.StringTypeSearchParameter;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represent FHIR API deployed in Healthcare Integration layer
 */
public class ResourceAPI {

    private String resourceName; // Resource name is same as API name
    private final HashMap<String, org.wso2.healthcare.integration.common.fhir.server.Profile> profiles = new HashMap<>(1);
    private final HashMap<String, Operation> operations = new HashMap<>(4);
    private final HashMap<String, org.wso2.healthcare.integration.common.fhir.server.SearchParameter> searchParameters = new HashMap<>(4);
    private org.wso2.healthcare.integration.common.fhir.server.Profile defaultProfile;

    private ResourceAPI() {
    }

    public String getResourceName() {

        return resourceName;
    }

    protected void setResourceName(String resourceName) {

        this.resourceName = resourceName;
    }

    protected void addProfile (org.wso2.healthcare.integration.common.fhir.server.Profile profile) {

        this.profiles.put(profile.getUri(), profile);
        if (profile.isDefaultProfile()) {
            this.defaultProfile = profile;
        }
    }

    public HashMap<String, org.wso2.healthcare.integration.common.fhir.server.Profile> getProfiles() {

        return profiles;
    }

    public org.wso2.healthcare.integration.common.fhir.server.Profile getProfile(String uri) {

        return this.profiles.get(uri);
    }

    public org.wso2.healthcare.integration.common.fhir.server.Profile getDefaultProfile() {

        return this.defaultProfile;
    }

    protected void addSearchParameter(org.wso2.healthcare.integration.common.fhir.server.SearchParameter searchParameter) {

        this.searchParameters.put(searchParameter.getName(), searchParameter);
    }

    protected void addOperation(AbstractOperation operation) {

        this.operations.put(operation.getName(), operation);
    }

    public Operation getOperation(String name) {

        return this.operations.get(name);
    }

    public HashMap<String, Operation> getOperations() {

        return operations;
    }

    /**
     * Function to retrieve search parameter setting by name
     *
     * @param name
     * @return
     */
    public org.wso2.healthcare.integration.common.fhir.server.SearchParameter getSearchParameter(String name) {
        return this.searchParameters.get(name);
    }

    /**
     * Function to return iterator to iterate through search parameter list
     *
     * @return
     */
    public Iterator<org.wso2.healthcare.integration.common.fhir.server.SearchParameter> getSearchParametersIterator() {

        return this.searchParameters.values().iterator();
    }

    public HashMap<String, SearchParameter> getSearchParameters() {

        return searchParameters;
    }

    /**
     * Function to create {@link ResourceAPI} object from settings OMElement
     *
     * @param settings settings
     * @return created FHIR resource API
     * @throws OpenHealthcareException
     */
    public static ResourceAPI createAPIFromSettings(OMElement settings) throws OpenHealthcareException {

        ResourceAPI resourceAPI = new ResourceAPI();

        OMElement resourceElement = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "resource"));
        if (resourceElement != null && StringUtils.isNotBlank(resourceElement.getText())) {
            resourceAPI.setResourceName(resourceElement.getText());
        } else {
            throw new OpenHealthcareException("\"resource\" (mandatory) is missing from the FHIR API settings");
        }

        OMElement profiles = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "profiles"));
        if (profiles != null) {
            Iterator profileIterator = profiles.getChildrenWithName(new QName(Constants.SYNAPSE_NS, "profile"));
            while (profileIterator.hasNext()) {
                OMElement profileElement = (OMElement) profileIterator.next();
                resourceAPI.addProfile(createProfile(profileElement));
            }
        }
        // At least one profile should be specified
        if (resourceAPI.getProfiles().isEmpty()) {
            throw new OpenHealthcareException("FHIR API must be bounded to at least one profile");
        }

        // Specifying default profile is MUST
        if (resourceAPI.getDefaultProfile() == null) {
            throw new OpenHealthcareException("Default profile is not found for FHIR API : " + resourceAPI.getResourceName());
        }

        OMElement searchParams = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "search"));
        if (searchParams != null) {
            Iterator searchParamsIterator = searchParams.getChildrenWithName(new QName(Constants.SYNAPSE_NS, "parameter"));
            while (searchParamsIterator.hasNext()) {
                OMElement parameter = (OMElement) searchParamsIterator.next();

                // process search parameter type setting
                OMElement typeElement = parameter.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "type"));
                if (typeElement == null || StringUtils.isBlank(typeElement.getText())) {
                    throw new OpenHealthcareException("\"type\" (mandatory) of the search parameter is missing");
                }
                String parameterType = typeElement.getText();

                AbstractSearchParameter searchParameter;
                if (Constants.FHIR_DATATYPE_REFERENCE.equals(parameterType)) {
                    searchParameter = new ReferenceTypeSearchParameter();
                } else if (Constants.FHIR_DATATYPE_STRING.equals(parameterType) ||
                        Constants.FHIR_DATATYPE_TOKEN.equals(parameterType)) {
                    searchParameter = new StringTypeSearchParameter(parameterType);
                } else {
                    throw new OpenHealthcareException("Unknown search parameter type : " + parameterType);
                }
                searchParameter.populateSettings(parameter);

                // Check whether the search parameter is bounded to specific profiles.
                if (searchParameter.isBoundedToProfile()) {
                    // If the search parameter is bound to specific profiles, it will be added to those profiles
                    Iterator<String> profileIterator = searchParameter.getProfiles();
                    while (profileIterator.hasNext()) {
                        String profileUri = profileIterator.next();
                        org.wso2.healthcare.integration.common.fhir.server.Profile profile = resourceAPI.getProfile(profileUri);
                        if (profile != null) {
                            profile.addSearchParameterSetting(searchParameter);
                        } else {
                            throw new OpenHealthcareException(
                                    "Search parameter : \"" + searchParameter.getName() +
                                            "\" is bounded to unknown profile : " + profileUri);
                        }
                    }
                } else {
                    // If not bounded, it will be registered as a resource level search parameter
                    resourceAPI.addSearchParameter(searchParameter);
                }
            }
        }

        // load resource level operations
        OMElement operations = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "operations"));
        if (operations != null) {
            Iterator operationIterator = operations.getChildrenWithName(new QName(Constants.SYNAPSE_NS, "operation"));
            while (operationIterator.hasNext()) {
                OMElement operationElement = (OMElement) operationIterator.next();
                // TODO when there are multiple implementations need to handle and instantiate correct implementation
                String operationStr = operationElement.getText();
                if (StringUtils.isBlank(operationStr)) {
                    throw new OpenHealthcareException("Operation element is empty");
                }
                SimpleOperation operation = new SimpleOperation(operationStr);
                operation.setActive(Boolean.parseBoolean(operationElement.getAttributeValue(new QName("active"))));
                resourceAPI.addOperation(operation);
            }
        }
        return resourceAPI;
    }


    private static org.wso2.healthcare.integration.common.fhir.server.Profile createProfile(OMElement profileElement) throws OpenHealthcareException {
        String profileUri = profileElement.getAttributeValue(new QName("uri"));
        if (StringUtils.isBlank(profileUri)) {
            throw new OpenHealthcareException("Empty profile element in the FHIR API settings");
        }
        org.wso2.healthcare.integration.common.fhir.server.Profile profile = new Profile(profileUri);
        boolean isDefault =
                Boolean.parseBoolean(profileElement.getAttributeValue(new QName("default")));
        profile.setDefaultProfile(isDefault);

        OMElement sequencesOM = profileElement.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "sequences"));
        if (sequencesOM != null) {
            Iterator sequencesIterator = sequencesOM.getChildrenWithName(new QName(Constants.SYNAPSE_NS, "sequence"));

            while (sequencesIterator.hasNext()) {
                OMElement sequenceOM = (OMElement) sequencesIterator.next();
                String interaction = sequenceOM.getAttributeValue(new QName("interaction"));
                String targetSequence = sequenceOM.getText();
                if (StringUtils.isNotBlank(interaction) && StringUtils.isNotBlank(targetSequence)) {
                    profile.addSequence(new Sequence(targetSequence, FHIRAPIInteraction.valueOfInteraction(interaction)));
                } else {
                    throw new OpenHealthcareException("Interaction or name of sequence is missing in the profile " +
                            "configuration of : " + profileUri);
                }
            }
        } else {
            throw new OpenHealthcareException("No sequences (mandatory) defined under profiles : " + profileUri);
        }
        return profile;
    }
}
