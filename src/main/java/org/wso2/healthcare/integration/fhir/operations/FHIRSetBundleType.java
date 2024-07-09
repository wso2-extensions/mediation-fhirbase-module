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

// Generated on 18-Thu, 06, 2020 18:09:12+0530

package org.wso2.healthcare.integration.fhir.operations;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRAddOperation;
import org.wso2.healthcare.integration.fhir.model.Bundle;
import org.wso2.healthcare.integration.fhir.model.Resource;

import java.util.HashMap;

/**
 * This class creates fhir patient resource.
 */
public class FHIRSetBundleType extends FHIRAddOperation {

    public FHIRSetBundleType() {
    }

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
            HashMap<String, String> configuredParams, Resource targetResource) throws FHIRConnectException {
        Bundle bundle = fhirConnectorContext.getContainerResource();
        if (bundle != null) {
            bundle.setType(configuredParams, fhirConnectorContext);
        } else {
            throw new FHIRConnectException("Bundle object not found in the FHIR context");
        }
    }

    @Override
    public String getOperationName() {
        return "setBundleType";
    }

    @Override
    protected String getParamPrefix() {
        return "";
    }
}