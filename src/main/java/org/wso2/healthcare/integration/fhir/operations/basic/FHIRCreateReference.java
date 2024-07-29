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

import org.apache.synapse.MessageContext;
import org.hl7.fhir.r4.model.Reference;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateAndAddDataTypeOperation;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.SpecialPurposeDataType;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.HashMap;

/**
 * Operation to create FHIR Reference
 */
public class FHIRCreateReference extends FHIRCreateAndAddDataTypeOperation {

    @Override
    protected DataType executeAndReturn(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                               HashMap<String, String> configuredParams, String objectId) throws FHIRConnectException {

        Reference reference = FHIRDataTypeUtils.getReference("", configuredParams, fhirConnectorContext);
        return new SpecialPurposeDataType(objectId, reference);
    }

    @Override
    public String getOperationName() {
        return "createReference";
    }
}
