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

package org.wso2.healthcare.integration.fhir.operations.basic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Reference;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateAndAddDataTypeOperation;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.SpecialPurposeDataType;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.HashMap;

/**
 * Operation to create special purpose data types
 */
public class FHIRCreateSpecialPurposeDataType extends FHIRCreateAndAddDataTypeOperation {

    private static final Log LOG = LogFactory.getLog(FHIRCreateSpecialPurposeDataType.class);
    private String dataType;

    @Override
    protected DataType executeAndReturn(MessageContext messageContext,
            FHIRConnectorContext fhirConnectorContext, HashMap<String, String> configuredParams, String objectId)
            throws FHIRConnectException {
        DataType dataObject;
        switch (getDataType()) {
            case "Reference":
                Reference reference = FHIRDataTypeUtils.getReference("", configuredParams, fhirConnectorContext);
                dataObject = new SpecialPurposeDataType(objectId, reference);
                break;
            case "Meta":
                Meta meta = FHIRDataTypeUtils.getMeta("", configuredParams, fhirConnectorContext);
                dataObject = new SpecialPurposeDataType(objectId, meta);
                break;
            case "Dosage":
                Dosage dosage = FHIRDataTypeUtils.getDosage("", configuredParams, fhirConnectorContext);
                dataObject = new SpecialPurposeDataType(objectId, dosage);
                break;
            case "Narrative":
                Narrative narrative = FHIRDataTypeUtils.getNarrative("", configuredParams, fhirConnectorContext);
                dataObject = new SpecialPurposeDataType(objectId, narrative);
                break;
            case "Extension":
                Extension extension = FHIRDataTypeUtils.getExtension("", configuredParams, fhirConnectorContext);
                dataObject = new SpecialPurposeDataType(objectId, extension);
                break;
            case "ElementDefinition":
                ElementDefinition elementDefinition = FHIRDataTypeUtils.getElementDefinition("", configuredParams, fhirConnectorContext);
                dataObject = new SpecialPurposeDataType(objectId, elementDefinition);
                break;
            default:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unknown Type : " + getDataType());
                }
                throw new FHIRConnectException("Unknown Type : " + getDataType());
        }
        return dataObject;
    }

    @Override
    public String getOperationName() {
        return "createSpecialPurposeDataType";
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
