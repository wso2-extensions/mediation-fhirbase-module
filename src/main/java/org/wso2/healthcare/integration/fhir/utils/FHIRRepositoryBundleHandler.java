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

package org.wso2.healthcare.integration.fhir.utils;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.hl7.fhir.r4.model.Bundle;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;

import java.util.List;

public class FHIRRepositoryBundleHandler extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext messageContext) {
        FHIRConnectorContext fhirConnectorContext = FHIRConnectorUtils.getFHIRConnectorContext(messageContext);
        Bundle bundle = fhirConnectorContext.getContainerResource().getFhirBundle();
        List<Bundle.BundleEntryComponent> entryComponentList = getEntries(bundle);
        for (Bundle.BundleEntryComponent entry : entryComponentList) {
            String entryResource = String.valueOf(entry.getResource());
            if (entryResource.contains((String) messageContext.getProperty("_OH_resourceType"))) {
                Bundle.BundleEntryRequestComponent request = entry.getRequest();
                request.setMethod(Bundle.HTTPVerb.POST);
                request.setUrl((String) messageContext.getProperty("_OH_resourceType"));
            }
        }
        return true;
    }

    public List<Bundle.BundleEntryComponent> getEntries(Bundle bundle) {
        return bundle.getEntry();
    }
}
