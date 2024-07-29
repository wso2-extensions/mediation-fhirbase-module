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

package org.wso2.healthcare.integration.common.fhir.server.api.handlers;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.rest.AbstractHandler;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.fhir.server.FHIRAPIStore;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;

import javax.xml.namespace.QName;

/**
 * This handler is responsible to load settings of the API to the
 * {@link FHIRAPIStore}
 */
public class FHIRAPISettingsHandler extends AbstractHandler implements ManagedLifecycle {

    private static final Log LOG = LogFactory.getLog(FHIRAPISettingsHandler.class);
    private OMElement apiSettings;
    private String resourceName;

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        messageContext.setProperty(Constants.OH_INTERNAL_FHIR_RESOURCE, resourceName);
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        return false;
    }

    @Override
    public void init(SynapseEnvironment synapseEnvironment) {

        ResourceAPI fhirAPI;
        try {
            fhirAPI = ResourceAPI.createAPIFromSettings(apiSettings);
        } catch (OpenHealthcareException e) {
            throw new SynapseException("Error occurred while creating FHIR resource API model from api settings", e);
        }
        FHIRAPIStore apiStore =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getFHIRAPIStore();
        apiStore.addAPI(fhirAPI);
        LOG.info("Successfully added FHIR API to the FHIR API Store : " + resourceName);
    }

    @Override
    public void destroy() {
        FHIRAPIStore apiStore =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getFHIRAPIStore();
        apiStore.removeAPI(resourceName);
        LOG.info("Successfully removed FHIR API from the FHIR API Store : " + resourceName);
    }

    public OMElement getApiSettings() {
        return apiSettings;
    }

    public void setApiSettings(OMElement apiSettings) {
        OMElement resourceElement = apiSettings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "resource"));
        if (resourceElement != null) {
            resourceName = resourceElement.getText();
        }
        this.apiSettings = apiSettings;
    }

    public String getResourceName() {
        return resourceName;
    }
}
