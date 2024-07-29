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

import org.wso2.healthcare.integration.common.fhir.FHIRAPIInteraction;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents FHIR Profile
 */
public class Profile {

    private final String uri;
    private boolean defaultProfile = false;
    private final HashMap<FHIRAPIInteraction, org.wso2.healthcare.integration.common.fhir.server.Sequence> sequences = new HashMap<>(2);

    /**
     * searchParameters contains profile specific search parameters
     */
    private final HashMap<String, SearchParameter> searchParameters = new HashMap<>(1);

    public Profile(String uri) {
        this.uri = uri;
    }

    public String getUri() {

        return uri;
    }

    public boolean isDefaultProfile() {

        return defaultProfile;
    }

    public void setDefaultProfile(boolean defaultProfile) {

        this.defaultProfile = defaultProfile;
    }

    /**
     * Function to add search parameter setting specific to this profile
     *
     * @param searchParameterSetting
     */
    public void addSearchParameterSetting(SearchParameter searchParameterSetting) {

        searchParameters.put(searchParameterSetting.getName(), searchParameterSetting);
    }

    /**
     * Function to get profile related search parameter by name
     *
     * @param searchParamName
     * @return
     */
    public SearchParameter getSearchParameter(String searchParamName) {

        return searchParameters.get(searchParamName);
    }

    public Iterator<SearchParameter> getSearchParameters() {

        return this.searchParameters.values().iterator();
    }

    /**
     * Function to add interaction sequence
     *
     * @param sequence
     */
    public void addSequence(org.wso2.healthcare.integration.common.fhir.server.Sequence sequence) {

        sequences.put(sequence.getInteraction(), sequence);
    }

    /**
     * Function to get sequence by interaction
     *
     * @param interaction
     * @return
     */
    public Sequence getSequenceByInteraction(FHIRAPIInteraction interaction) {

        return sequences.get(interaction);
    }
}
