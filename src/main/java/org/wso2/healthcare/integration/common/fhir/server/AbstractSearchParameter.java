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
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represent search parameter settings
 */
public abstract class AbstractSearchParameter implements SearchParameter {

    private String name;
    private String type;
    private String expression;
    private boolean active = false;
    /**
     * profiles contains list profiles which this parameter is belongs to.
     * the search parameter is only available for linked profile.
     * If profiles are null, then the search parameter is not bounded to specific profiles
     */
    private ArrayList<String> profiles = null;
    private final String defaultValue;

    public AbstractSearchParameter(String type) {
        this.type = type;
        this.defaultValue =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().
                        getHealthcareIntegratorConfig().getFHIRServerConfig().getDataSourceConfig().getMatchAnyPattern();
    }

    @Override
    public boolean canPreProcess(HTTPInfo httpInfo) {

        return httpInfo.isQueryParamPresent(getName()) && this.active;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public String getExpression() {

        return expression;
    }

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
    public boolean isBoundedToProfile() {

        return this.profiles != null && this.profiles.size() > 0;
    }

    @Override
    public Iterator<String> getProfiles() {

        if (this.profiles != null) {
            return this.profiles.iterator();
        }
        return null;
    }

    @Override
    public String getDefaultValue() {

        return defaultValue;
    }

    @Override
    public void postProcess(SearchParameterInfo searchParameterInfo, AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException {
        // Nothing to do here
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setType(String type) {

        this.type = type;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public void setExpression(String expression) {

        this.expression = expression;
    }


    public void populateSettings(OMElement settings) throws OpenHealthcareException {

        // process search parameter active setting
        this.setActive(Boolean.parseBoolean(settings.getAttributeValue(new QName("active"))));

        // process search parameter name setting
        OMElement nameElement = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "name"));
        if (nameElement != null) {
            this.setName(nameElement.getText());
        } else {
            throw new OpenHealthcareException("\"name\" (mandatory) of the search parameter is missing");
        }

        // process search parameter expression
        OMElement expressionElement = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "expression"));
        if (expressionElement != null) {
            this.setExpression(expressionElement.getText());
        } else {
            throw new OpenHealthcareException("\"expression\" (mandatory) of the search parameter is missing");
        }

        // process search parameter linked profiles
        OMElement profilesElement = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "profiles"));
        if (profilesElement != null) {
            Iterator profilesIterator = profilesElement.getChildrenWithName(new QName(Constants.SYNAPSE_NS, "profile"));
            while (profilesIterator.hasNext()) {
                OMElement profileElement = (OMElement) profilesIterator.next();
                this.addProfile(profileElement.getText());
            }
        }
    }

    @Override
    public String toString() {

        return "SearchParameterSetting {" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", expression='" + expression + '\'' +
                ", active=" + active +
                '}';
    }

    private void addProfile(String uri) {
        if (this.profiles == null) {
            this.profiles = new ArrayList<>();
        }
        this.profiles.add(uri);
    }
}
