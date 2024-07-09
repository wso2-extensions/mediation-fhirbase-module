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
package org.wso2.healthcare.integration.fhir.model;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorConfig;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.Map;

public abstract class Resource {

    private static final Log LOG = LogFactory.getLog(Resource.class);
    protected String objectId;
    private String parentPrefix = "";

    public Resource(String parentPrefix, Map<String, String> connectorInputParameters) throws FHIRConnectException {
        this.objectId = connectorInputParameters.get(FHIRConstants.FHIR_PARAM_OBJECT_ID);
        if (objectId == null || objectId.trim().isEmpty()) {
            this.objectId = FHIRConnectorUtils.generateUUID();
        }
        if (parentPrefix != null) {
            this.parentPrefix = parentPrefix;
        }
    }

    abstract public org.hl7.fhir.r4.model.Resource unwrap();

    protected void init(Map<String, String> parameters, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        populateResource(parameters);
    }

    public String serializeToJSON() {
        FhirContext fhirContext = FHIRConnectorConfig.getInstance().getFhirContext();
        return fhirContext.newJsonParser().encodeResourceToString(unwrap());
    }

    public String serializeToXML() {
        FhirContext fhirContext = FHIRConnectorConfig.getInstance().getFhirContext();
        return fhirContext.newXmlParser().encodeResourceToString(unwrap());
    }

    public String getObjectId() {
        return objectId;
    }

    public String getParentPrefix() {
        return parentPrefix;
    }

    private void populateResource(Map<String, String> connectorInputParameters) throws FHIRConnectException {
        if (connectorInputParameters != null) {
            FHIRDataTypeUtils.populateBaseResource(this.unwrap(), connectorInputParameters);
        }
    }

    /**
     * Function to allow resource implementation to perform any remaining tasks before serialization
     */
    public void beforeSerialize(FHIRConnectorContext fhirConnectorContext) {
    }
}
