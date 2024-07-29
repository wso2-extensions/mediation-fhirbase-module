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
package org.wso2.healthcare.integration.fhir.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.model.type.BaseType;
import org.wso2.healthcare.integration.fhir.model.type.ElementType;
import org.wso2.healthcare.integration.fhir.utils.QueryUtils;

import java.util.HashMap;

/**
 * This abstract class should be implemented by the operations that perform resource "Create" Operations
 */
public abstract class FHIRCreateAndAddElementTypeOperation extends FHIRCreateDataTypeOperation {

    private static final Log LOG = LogFactory.getLog(FHIRCreateAndAddElementTypeOperation.class);

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
            HashMap<String, String> configuredParams, String objectId) throws FHIRConnectException {
        ElementType returnType = executeAndReturn(messageContext, fhirConnectorContext, configuredParams, objectId);
        if (returnType != null) {
            //evaluate FHIRPath and inject the element object
            String fhirPath = configuredParams.get(FHIRConstants.FHIR_PARAM_FHIR_PATH);
            if (fhirPath != null) {
                // If user have provided FHIRPath directly add to the object model
                String targetId = configuredParams.get(FHIRConstants.FHIR_PARAM_TARGET_OBJECT_ID);
                Resource targetResource;
                if (targetId == null) {
                    targetResource = fhirConnectorContext.getTargetResource();
                } else {
                    targetResource = fhirConnectorContext.getResource(targetId);
                }
                Query query = new Query();
                query.setFhirPath(fhirPath);
                query.setSrcResource(targetResource);
                QueryUtils.evaluateQueryAndPlace(query, new BaseType<ElementType>(returnType));
            } else {
                // If user haven't provided the FHIRPath add it to the object Map against objectId
                //add to the element type map
                fhirConnectorContext.addElementObject(returnType);
            }
        } else {
            LOG.warn("Failed to create element object");
        }
    }

    protected abstract ElementType executeAndReturn(MessageContext messageContext,
            FHIRConnectorContext fhirConnectorContext,
            HashMap<String, String> configuredParams, String objectId) throws FHIRConnectException;
}
