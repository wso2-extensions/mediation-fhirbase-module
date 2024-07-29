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
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.Contributor;
import org.hl7.fhir.r4.model.DataRequirement;
import org.hl7.fhir.r4.model.Expression;
import org.hl7.fhir.r4.model.ParameterDefinition;
import org.hl7.fhir.r4.model.RelatedArtifact;
import org.hl7.fhir.r4.model.TriggerDefinition;
import org.hl7.fhir.r4.model.UsageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateAndAddDataTypeOperation;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.MetaDataType;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.HashMap;

public class FHIRCreateMetaDataType extends FHIRCreateAndAddDataTypeOperation {

    private static Log log = LogFactory.getLog(FHIRCreateMetaDataType.class);
    private String dataType;

    @Override
    protected DataType executeAndReturn(MessageContext messageContext,
            FHIRConnectorContext fhirConnectorContext, HashMap<String, String> configuredParams, String objectId)
            throws FHIRConnectException {
        DataType dataObject;
        switch (getDataType()) {
            case "ContactDetail":
                ContactDetail contactDetail = FHIRDataTypeUtils.getContactDetail("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, contactDetail);
                break;
            case "UsageContext":
                UsageContext usageContext = FHIRDataTypeUtils.getUsageContext("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, usageContext);
                break;
            case "RelatedArtifact":
                RelatedArtifact relatedArtifact = FHIRDataTypeUtils.getRelatedArtifact("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, relatedArtifact);
                break;
            case "Contributor":
                Contributor contributor = FHIRDataTypeUtils.getContributor("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, contributor);
                break;
            case "DataRequirement":
                DataRequirement dataRequirement = FHIRDataTypeUtils.getDataRequirement("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, dataRequirement);
                break;
            case "ParameterDefinition":
                ParameterDefinition parameterDefinition = FHIRDataTypeUtils.getParameterDefinition("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, parameterDefinition);
                break;
            case "Expression":
                Expression expression = FHIRDataTypeUtils.getExpression("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, expression);
                break;
            case "TriggerDefinition":
                TriggerDefinition triggerDefinition = FHIRDataTypeUtils.getTriggerDefinition("", configuredParams, fhirConnectorContext);
                dataObject = new MetaDataType(objectId, triggerDefinition);
                break;
            default:
                throw new FHIRConnectException("Unknown Type : " + getDataType());
        }
        return dataObject;
    }

    @Override
    public String getOperationName() {
        return "createMetaDataType";
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
