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
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Count;
import org.hl7.fhir.r4.model.Distance;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.MoneyQuantity;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.SampledData;
import org.hl7.fhir.r4.model.Signature;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.Timing;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateAndAddDataTypeOperation;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.GeneralPurposeDataType;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.HashMap;

/**
 * Operation to create FHIR General Purpose Data types
 */
public class FHIRCreateGeneralPurposeDataType extends FHIRCreateAndAddDataTypeOperation {

    private static Log log = LogFactory.getLog(FHIRCreateGeneralPurposeDataType.class);
    private String dataType;

    @Override
    protected DataType executeAndReturn(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams, String objectId) throws FHIRConnectException {
        DataType dataObject;
        switch (getDataType()) {
            case "Coding":
                Coding coding = FHIRDataTypeUtils.getCoding("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, coding);
                break;
            case "CodeableConcept":
                String code = configuredParams.get("code");
                String valueSet = configuredParams.get("valueSet");
                if (code != null && valueSet != null) {
                    // populating the code and valueSet that will be read by the FHIRDataTypeUtils.getCoding
                    configuredParams.put("coding.code", code);
                    configuredParams.put("coding.valueSet", valueSet);
                }
                CodeableConcept codeableConcept = FHIRDataTypeUtils.getCodeableConcept("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, codeableConcept);
                break;
            case "Identifier":
                Identifier identifier = FHIRDataTypeUtils.getIdentifier("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, identifier);
                break;
            case "HumanName":
                HumanName humanName = FHIRDataTypeUtils.getHumanName("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, humanName);
                break;
            case "Address":
                Address address = FHIRDataTypeUtils.getAddress("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, address);
                break;
            case "ContactPoint":
                ContactPoint contactPoint = FHIRDataTypeUtils.getContactPoint("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, contactPoint);
                break;
            case "Attachment":
                Attachment attachment = FHIRDataTypeUtils.getAttachment("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, attachment);
                break;
            case "Period":
                Period period = FHIRDataTypeUtils.getPeriod("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, period);
                break;
            case "Quantity":
                Quantity quantity = FHIRDataTypeUtils.getQuantity("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, quantity);
                break;
            case "SimpleQuantity":
                SimpleQuantity simpleQuantity = FHIRDataTypeUtils.getSimpleQuantity("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, simpleQuantity);
                break;
            case "Range":
                Range range = FHIRDataTypeUtils.getRange("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, range);
                break;
            case "Ratio":
                Ratio ratio = FHIRDataTypeUtils.getRatio("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, ratio);
                break;
            case "Timing":
                Timing timing = FHIRDataTypeUtils.getTiming("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, timing);
                break;
            case "Money":
                Money money = FHIRDataTypeUtils.getMoney("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, money);
                break;
            case "Signature":
                Signature signature = FHIRDataTypeUtils.getSignature("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, signature);
                break;
            case "Annotation":
                Annotation annotation = FHIRDataTypeUtils.getAnnotation("",configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, annotation);
                break;
            case "SampledData":
                SampledData sampledData = FHIRDataTypeUtils.getSampledData("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, sampledData);
                break;
            case "Age":
                Age age = FHIRDataTypeUtils.getAge("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, age);
                break;
            case "Distance":
                Distance distance = FHIRDataTypeUtils.getDistance("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, distance);
                break;
            case "Duration":
                Duration duration = FHIRDataTypeUtils.getDuration("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, duration);
                break;
            case "Count":
                Count count = FHIRDataTypeUtils.getCount("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, count);
                break;
            case "MoneyQuantity":
                MoneyQuantity moneyQuantity = FHIRDataTypeUtils.getMoneyQuantity("", configuredParams, fhirConnectorContext);
                dataObject = new GeneralPurposeDataType(objectId, moneyQuantity);
                break;
            default:
                throw new FHIRConnectException("Unknown Type : " + getDataType());
        }
        return dataObject;
    }

    @Override
    public String getOperationName() {
        return "createGeneralPurposeDataType";
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
