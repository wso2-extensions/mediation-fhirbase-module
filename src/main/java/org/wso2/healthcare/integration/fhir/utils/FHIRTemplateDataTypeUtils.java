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

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.*;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.codesystems.CodeSystemEnumGenFactory;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.model.Bundle;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.GeneralPurposeDataType;
import org.wso2.healthcare.integration.fhir.model.type.MetaDataType;
import org.wso2.healthcare.integration.fhir.model.type.PrimitiveDataType;
import org.wso2.healthcare.integration.fhir.model.type.SpecialPurposeDataType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class FHIRTemplateDataTypeUtils {

    public static DataType getDataTypeObject(String type, String fhirpath, String parentPath, String value,
                                             Object data, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (type == null) {
            // if the type is not given, it's assumed as string type
            type = "string";
        }
        switch (type) {
            case "string":
                return new PrimitiveDataType(null, getStringType(value));
            case "id":
                return new PrimitiveDataType(null, getIdType(value));
            case "integer":
                return new PrimitiveDataType(null, getIntegerType(value));
            case "unsignedInt":
                return new PrimitiveDataType(null, getUnsignedIntType(value));
            case "positiveInt":
                return new PrimitiveDataType(null, getPositiveIntType(value));
            case "decimal":
                return new PrimitiveDataType(null, getDecimalType(value));
            case "instant":
                return new PrimitiveDataType(null, getInstantType(value));
            case "code":
                return new PrimitiveDataType(null, getCodeType(value));
            case "boolean":
                return new PrimitiveDataType(null, getBooleanType(value));
            case "CodeableConcept":
                return new GeneralPurposeDataType(null, populateCodeableConcept(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Coding":
                return new GeneralPurposeDataType(null, populateCoding(fhirpath, parentPath, value, data,
                        connectorContext));
            case "date":
                return new PrimitiveDataType(null, getDateType(value));
            case "Count":
                return new GeneralPurposeDataType(null, populateCount(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Reference":
                return new SpecialPurposeDataType(null, populateReference(fhirpath, parentPath, value, data,
                        connectorContext));
            case "HumanName":
                return new GeneralPurposeDataType(null, populateHumanName(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Address":
                return new GeneralPurposeDataType(null, populateAddress(fhirpath, parentPath, value, data,
                        connectorContext));
            case "dateTime":
                return new PrimitiveDataType(null, getDateTimeType(value));
            case "markdown":
                return new PrimitiveDataType(null, getMarkdownType(value));
            case "oid":
                return new PrimitiveDataType(null, getOidType(value));
            case "time":
                return new PrimitiveDataType(null, getTimeType(value));
            case "uri":
                return new PrimitiveDataType(null, getUriType(value));
            case "url":
                return new PrimitiveDataType(null, getUrlType(value));
            case "uuid":
                return new PrimitiveDataType(null, getUuidType(value));
            case "Age":
                return new GeneralPurposeDataType(null, populateAge(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Annotation":
                return new GeneralPurposeDataType(null, populateAnnotation(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Attachment":
                return new GeneralPurposeDataType(null, populateAttachment(fhirpath, parentPath, value, data,
                        connectorContext));
            case "ContactPoint":
                return new GeneralPurposeDataType(null, populateContactPoint(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Distance":
                return new GeneralPurposeDataType(null, populateDistance(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Duration":
                return new GeneralPurposeDataType(null, populateDuration(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Identifier":
                return new GeneralPurposeDataType(null, populateIdentifier(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Money":
                return new GeneralPurposeDataType(null, populateMoney(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Period":
                return new GeneralPurposeDataType(null, populatePeriod(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Quantity":
                return new GeneralPurposeDataType(null, populateQuantity(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Range":
                return new GeneralPurposeDataType(null, populateRange(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Ratio":
                return new GeneralPurposeDataType(null, populateRatio(fhirpath, parentPath, value, data,
                        connectorContext));
            case "SampledData":
                return new GeneralPurposeDataType(null, populateSampledData(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Signature":
                return new GeneralPurposeDataType(null, populateSignature(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Timing":
                return new GeneralPurposeDataType(null, populateTiming(fhirpath, parentPath, value, data,
                        connectorContext));
            case "ContactDetail":
                return new MetaDataType(null, populateContactDetail(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Contributor":
                return new MetaDataType(null, populateContributor(fhirpath, parentPath, value, data,
                        connectorContext));
            case "Meta":
                return new SpecialPurposeDataType(null, populateMeta(fhirpath, parentPath, value, data,
                        connectorContext));
            case "base64Binary":
                return new PrimitiveDataType(null, getBase64BinaryType(value));
            case "canonical":
                return new PrimitiveDataType(null, getCanonicalType(value));
            default:
                return null;
        }
    }

    public static Identifier populateIdentifier(String fhirpath, String parentPath, String value, Object identifier,
                                                FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (identifier == null) {
            identifier = new Identifier();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Identifier)identifier, connectorContext);

        switch (leafFieldName) {
            case "use":
                try {
                    ((Identifier)identifier).setUse(Identifier.IdentifierUse.fromCode(value));
                } catch (FHIRException e) {
                    String msg = "Error occurred while setting the identifier.use field";
                    FHIRConnectorUtils.handleException(msg, e);
                }
                break;
            case "type":
                CodeableConcept codeableConcept = populateCodeableConcept(
                        "http://hl7.org/fhir/ValueSet/identifier-type", fhirpath, value, null, connectorContext);
                ((Identifier) identifier).setType(codeableConcept);
                break;
            case "system":
                ((Identifier)identifier).setSystem(value);
                break;
            case "value":
                ((Identifier)identifier).setValue(value);
                break;
            case "assigner.reference":
                Reference reference = ((Identifier)identifier).getAssigner();
                if (((Identifier)identifier).getAssigner() == null) {
                    reference = new Reference();
                }
                reference.setReference(value);
                ((Identifier)identifier).setAssigner(reference);
                break;
            case "assigner.display":
                reference = ((Identifier)identifier).getAssigner();
                if (((Identifier)identifier).getAssigner() == null) {
                    reference = new Reference();
                }
                reference.setDisplay(value);
                break;
            case "assigner.type":
                reference = ((Identifier)identifier).getAssigner();
                if (((Identifier)identifier).getAssigner() == null) {
                    reference = new Reference();
                }
                reference.setType(value);
                break;
            case "assigner.identifier.use":
                reference = ((Identifier)identifier).getAssigner();
                Identifier referenceIdentifier = reference.getIdentifier();
                if (((Identifier)identifier).getAssigner() == null) {
                    reference = new Reference();
                }
                if (reference.getIdentifier() == null) {
                    referenceIdentifier = new Identifier();
                }
                try {
                    referenceIdentifier.setUse(Identifier.IdentifierUse
                            .fromCode(value));
                    reference.setIdentifier(referenceIdentifier);
                } catch (FHIRException e) {
                    String msg = "Error occurred while setting the identifier.use field";
                    FHIRConnectorUtils.handleException(msg, e);
                }
                break;
            case "assigner.identifier.type":
                reference = ((Identifier)identifier).getAssigner();
                referenceIdentifier = reference.getIdentifier();
                if (((Identifier)identifier).getAssigner() == null) {
                    reference = new Reference();
                }
                if (reference.getIdentifier() == null) {
                    referenceIdentifier = new Identifier();
                }
                CodeableConcept concept = populateCodeableConcept(
                        "http://hl7.org/fhir/ValueSet/identifier-type", fhirpath, value, null, connectorContext);
                referenceIdentifier.setType(concept);
                break;
            default:
                ((Identifier)identifier).setPeriod(populatePeriod(fhirpath, parentPath, value, null, connectorContext));
                if (leafFieldName.contains("type.")) {
                    codeableConcept = populateCodeableConcept(fhirpath, parentPath + ".type", value,
                            ((Identifier) identifier).getType(), connectorContext);
                    ((Identifier) identifier).setType(codeableConcept);
                }
        }

        if (((Identifier)identifier).isEmpty()) return null;
        return ((Identifier)identifier);
    }

    public static HumanName populateHumanName(String fhirpath, String parentPath, String value, Object name,
                                              FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (name == null) {
            name = new HumanName();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (HumanName)name, connectorContext);
        switch (leafFieldName) {
            case "use":
                try {
                    ((HumanName)name).setUse(HumanName.NameUse.fromCode(value));
                } catch (FHIRException e) {
                    String msg = "Error occurred while setting the name.use field";
                    FHIRConnectorUtils.handleException(msg, e);
                }
                break;
            case "text":
                ((HumanName)name).setText(value);
                break;
            case "family":
                ((HumanName)name).setFamily(value);
                break;
            case "given":
                ((HumanName)name).addGiven(value);
                break;
            case "prefix":
                ((HumanName)name).addPrefix(value);
                break;
            case "suffix":
                ((HumanName)name).addSuffix(value);
                break;
            default:
                ((HumanName)name).setPeriod(populatePeriod(fhirpath, parentPath, value, null, connectorContext));
        }

        if (((HumanName)name).isEmpty()) return null;
        return ((HumanName)name);
    }

    public static Period populatePeriod(String fhirpath, String parentPath, String value, Object period,
                                        FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (period == null) {
            period = new Period();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) period, connectorContext);
        switch (leafFieldName) {
            case "start":
                if (StringUtils.isNotBlank(value)) {
                    ((Period)period).setStart(FHIRDataTypeUtils.parseToDate(value));
                }
                break;
            case "end":
                if (StringUtils.isNotBlank(value)) {
                    ((Period)period).setEnd(FHIRDataTypeUtils.parseToDate(value));
                }
                break;
            default:
                //do nothing
        }
        if (((Period)period).isEmpty()) return null;
        return (Period) period;
    }

    public static CodeableConcept populateCodeableConcept(String fhirpath, String parentPath, String valueSetUrl,
                                                          String value,  Object codeableConcept,
                                                          FHIRConnectorContext connectorContext) throws FHIRConnectException {
        CodeSystemEnumGenFactory codeSystemEnumGenFactory = new CodeSystemEnumGenFactory();
        if (codeableConcept == null) {
            codeableConcept = new CodeableConcept();
        }
        populateElement(fhirpath, value, (Element) codeableConcept, connectorContext);

        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        if ("text".equals(leafFieldName)) {
            ((CodeableConcept) codeableConcept).setText(value);
        } else {
            if (value != null) {
                ((CodeableConcept) codeableConcept).addCoding(codeSystemEnumGenFactory.createCoding(valueSetUrl, value));
            } else {
                ((CodeableConcept) codeableConcept).addCoding(populateCoding(fhirpath, parentPath, value, null,
                        connectorContext));
            }
        }

        if (((CodeableConcept) codeableConcept).isEmpty()) return null;
        return ((CodeableConcept) codeableConcept);
    }

    public static CodeableConcept populateCodeableConcept(String fhirpath, String parentPath, String value,
                                                          Object codeableConcept,
                                                          FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (codeableConcept == null) {
            codeableConcept = new CodeableConcept();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) codeableConcept, connectorContext);

        if ("text".equals(leafFieldName)) {
            ((CodeableConcept) codeableConcept).setText(value);
        } else {
            if (fhirpath.contains("coding.")) {
                if (((CodeableConcept) codeableConcept).getCoding() != null
                        && ((CodeableConcept) codeableConcept).getCoding().size() > 0) {
                    Coding codingVal =
                            ((CodeableConcept) codeableConcept).getCoding().get(((CodeableConcept) codeableConcept).getCoding().size() - 1);
                    if (!codingVal.isEmpty()) {
                        populateCoding(fhirpath, parentPath + ".coding", value, codingVal, connectorContext);
                    } else {
                        ((CodeableConcept) codeableConcept).addCoding(populateCoding(fhirpath, parentPath + ".coding", value, codingVal, connectorContext));
                    }
                } else {
                    ((CodeableConcept) codeableConcept).addCoding(populateCoding(fhirpath, parentPath + ".coding",
                            value, null, connectorContext));
                }
            }
        }

        if (((CodeableConcept)codeableConcept).isEmpty()) return null;
        return ((CodeableConcept)codeableConcept);
    }

    public static ContactPoint populateContactPoint(String fhirpath, String parentPath, String value,
                                                    Object contactPoint, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (contactPoint == null) {
            contactPoint = new ContactPoint();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) contactPoint, connectorContext);

        switch (leafFieldName) {
            case "system":
                ((ContactPoint)contactPoint).setSystem(ContactPoint.ContactPointSystem.fromCode(value));
                break;
            case "use":
                ((ContactPoint)contactPoint).setUse(ContactPoint.ContactPointUse.fromCode(value));
                break;
            case "value":
                ((ContactPoint)contactPoint).setValue(value);
                break;
            case "rank":
                ((ContactPoint)contactPoint).setRank(Integer.parseInt(value));
                break;
            default:
                ((ContactPoint)contactPoint).setPeriod(populatePeriod(fhirpath, parentPath, value, null,
                        connectorContext));
        }
        if (((ContactPoint)contactPoint).isEmpty()) return null;
        return ((ContactPoint)contactPoint);
    }

    public static Coding populateCoding(String fhirpath, String parentPath, String value, Object coding,
                                        FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (coding == null) {
            coding = new Coding();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) coding, connectorContext);
        switch (leafFieldName) {
            case "code":
                ((Coding)coding).setCode(value);
                break;
            case "system":
                ((Coding)coding).setSystem(value);
                break;
            case "display":
                ((Coding)coding).setDisplay(value);
                break;
            case "version":
                ((Coding)coding).setVersion(value);
                break;
            case "userSelected":
                ((Coding)coding).setUserSelected(Boolean.parseBoolean(value));
                break;
            default:
                //do nothing
        }

        if (((Coding)coding).isEmpty()) return null;
        return ((Coding)coding);
    }

    public static Address populateAddress(String fhirpath, String parentPath, String value, Object address,
                                          FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (address == null) {
            address = new Address();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) address, connectorContext);
        switch (leafFieldName) {
            case "use":
                try {
                    ((Address) address).setUse(Address.AddressUse.fromCode(value));
                } catch (FHIRException e) {
                    String msg = "Error occurred while setting the address.use field(s).";
                    FHIRConnectorUtils.handleException(msg, e);
                }
                break;
            case "type":
                try {
                    ((Address) address).setType(Address.AddressType.fromCode(value));
                } catch (FHIRException e) {
                    String msg = "Error occurred while setting the address.use field(s).";
                    FHIRConnectorUtils.handleException(msg, e);
                }
                break;
            case "text":
                ((Address) address).setText(value);
                break;
            case "line":
                ((Address) address).addLine(value);
                break;
            case "city":
                ((Address) address).setCity(value);
                break;
            case "district":
                ((Address) address).setDistrict(value);
                break;
            case "state":
                ((Address) address).setState(value);
                break;
            case "postalCode":
                ((Address) address).setPostalCode(value);
                break;
            case "country":
                ((Address) address).setCountry(value);
                break;
            default:
                ((Address) address).setPeriod(populatePeriod(fhirpath, parentPath, value, null, connectorContext));
        }
        if (((Address) address).isEmpty()) return null;
        return ((Address) address);
    }

    public static Attachment populateAttachment(String fhirpath, String parentPath, String value, Object attachment,
                                                FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (attachment == null) {
            attachment = new Attachment();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) attachment, connectorContext);
        switch (leafFieldName) {
            case "contentType":
                ((Attachment)attachment).setContentType(value);
                break;
            case "language":
                ((Attachment)attachment).setLanguage(value);
                break;
            case "data":
                ((Attachment)attachment).setData(value.getBytes());
                break;
            case "url":
                ((Attachment)attachment).setUrl(value);
                break;
            case "size":
                ((Attachment)attachment).setSize(Integer.parseInt(value));
                break;
            case "hash":
                ((Attachment)attachment).setHash(value.getBytes());
                break;
            case "title":
                ((Attachment)attachment).setTitle(value);
                break;
            case "creation":
                ((Attachment)attachment).setCreation(FHIRDataTypeUtils.parseToDate(value));
                break;
            default:
                //do nothing
        }
        if (((Attachment)attachment).isEmpty()) return null;
        return ((Attachment)attachment);
    }


    public static Quantity populateQuantity(String fhirpath, String parentPath, String value, Object quantity,
                                            FHIRConnectorContext connectorContext) throws FHIRConnectException {

        if (quantity == null) {
            quantity = new Quantity();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) quantity, connectorContext);
        switch (leafFieldName) {
            case "value":
                ((Quantity)quantity).setValueElement(getDecimalType(value));
                break;
            case "comparator":
                ((Quantity)quantity).setComparator(Quantity.QuantityComparator.fromCode(value));
                break;
            case "unit":
                ((Quantity)quantity).setUnitElement(getStringType(value));
                break;
            case "system":
                ((Quantity)quantity).setSystemElement(getUriType(value));
                break;
            case "code":
                ((Quantity)quantity).setCodeElement(getCodeType(value));
                break;
        }
        return ((Quantity)quantity);
    }

    public static Duration populateDuration(String fhirpath, String parentPath, String value, Object duration,
                                            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (duration == null) {
            duration = new Duration();
        }
        populateQuantity(fhirpath, parentPath, value, duration, connectorContext);
        if (((Duration)duration).isEmpty()) return null;
        return (Duration) duration;
    }

    public static Distance populateDistance(String fhirpath, String parentPath, String value, Object distance,
                                            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (distance == null) {
            distance = new Distance();
        }
        populateQuantity(fhirpath, parentPath, value, distance, connectorContext);
        if (((Distance)distance).isEmpty()) return null;
        return (Distance) distance;
    }

    public static Count populateCount(String fhirpath, String parentPath, String value, Object count,
                                      FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (count == null) {
            count = new Count();
        }
        populateQuantity(fhirpath, parentPath, value, count, connectorContext);
        if (((Count)count).isEmpty()) return null;
        return (Count) count;
    }

    public static Age populateAge(String fhirpath, String parentPath, String value, Object age,
                                  FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (age == null) {
            age = new Age();
        }
        populateQuantity(fhirpath, parentPath, value, age, connectorContext);
        if (((Age)age).isEmpty()) return null;
        return (Age) age;
    }

    public static MoneyQuantity populateMoneyQuantity(String fhirpath, String parentPath, String value,
                                                      Object moneyQuantity, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (moneyQuantity == null) {
            moneyQuantity = new MoneyQuantity();
        }
        populateQuantity(fhirpath, parentPath, value, moneyQuantity, connectorContext);
        if (((MoneyQuantity)moneyQuantity).isEmpty()) return null;
        return (MoneyQuantity) moneyQuantity;
    }

    public static SimpleQuantity populateSimpleQuantity(String fhirpath, String parentPath, String value,
                                                        Object simpleQuantity, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (simpleQuantity == null) {
            simpleQuantity = new SimpleQuantity();
        }
        populateQuantity(fhirpath, parentPath, value, simpleQuantity, connectorContext);
        if (((SimpleQuantity)simpleQuantity).isEmpty()) return null;
        return (SimpleQuantity) simpleQuantity;
    }

    public static Range populateRange(String fhirpath, String parentPath, String value, Object range,
                                      FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (range == null) {
            range = new Range();
        }
        populateElement(fhirpath, value, (Element) range, connectorContext);
        if (fhirpath.contains("low.")) {
            ((Range)range).setLow(populateSimpleQuantity(fhirpath, parentPath + ".low", value, null, connectorContext));
        } else if (fhirpath.contains("high.")) {
            ((Range)range).setHigh(populateSimpleQuantity(fhirpath, parentPath + ".high", value, null,
                    connectorContext));
        }
        if (((Range)range).isEmpty()) return null;
        return ((Range)range);
    }

    public static Timing populateTiming(String fhirpath, String parentPath, String value, Object timing,
                                        FHIRConnectorContext connectorContext) throws FHIRConnectException {

        if (timing == null) {
            timing = new Timing();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) timing, connectorContext);
        Timing.TimingRepeatComponent repeatComponent = ((Timing)timing).getRepeat();
        if (repeatComponent == null) {
            repeatComponent = new Timing.TimingRepeatComponent();
            ((Timing)timing).setRepeat(repeatComponent);
        }

        switch (leafFieldName) {
            case "event":
                ((Timing)timing).addEvent(getDateTime(value));
                break;
            case "repeat.count":
                repeatComponent.setCountElement(getPositiveIntType(value));
                break;
            case "repeat.countMax":
                repeatComponent.setCountMaxElement(getPositiveIntType(value));
                break;
            case "repeat.duration":
                repeatComponent.setDurationElement(getDecimalType(value));
                break;
            case "repeat.durationMax":
                repeatComponent.setDurationMaxElement(getDecimalType(value));
                break;
            case "repeat.durationUnit":
                repeatComponent.setDurationUnit(Timing.UnitsOfTime.fromCode(value));
                break;
            case "repeat.frequency":
                repeatComponent.setFrequencyElement(getPositiveIntType(value));
                break;
            case "repeat.frequencyMax":
                repeatComponent.setFrequencyMaxElement(getPositiveIntType(value));
                break;
            case "repeat.period":
                repeatComponent.setPeriodElement(getDecimalType(value));
                break;
            case "repeat.periodMax":
                repeatComponent.setPeriodMaxElement(getDecimalType(value));
                break;
            case "repeat.periodUnit":
                repeatComponent.setPeriodUnit(Timing.UnitsOfTime.fromCode(value));
                break;
            case "repeat.dayOfWeek":
                repeatComponent.addDayOfWeek(Timing.DayOfWeek.fromCode(value));
                break;
            case "repeat.timeOfDay":
                repeatComponent.addTimeOfDay(value);
                break;
            case "repeat.when":
                repeatComponent.addWhen(Timing.EventTiming.fromCode(value));
                break;
            case "repeat.offset":
                repeatComponent.setOffsetElement(getUnsignedIntType(value));
                break;
            case "code":
                ((Timing)timing).setCode(populateCodeableConcept(fhirpath,parentPath,
                        "http://hl7.org/fhir/ValueSet/timing-abbreviation",value,null, connectorContext));
                break;
            default:
                repeatComponent.setBounds(populateDuration(fhirpath, parentPath, value, null, connectorContext));
                repeatComponent.setBounds(populateRange(fhirpath, parentPath, value, null, connectorContext));
                repeatComponent.setBounds(populatePeriod(fhirpath, parentPath, value, null, connectorContext));
        }
        if (((Timing)timing).isEmpty()) return null;
        return (Timing) timing;
    }

    public static Ratio populateRatio(String fhirpath, String parentPath, String value, Object ratio,
                                      FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (ratio == null) {
            ratio = new Ratio();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) ratio, connectorContext);
        if (leafFieldName.contains("numerator.")) {
            ((Ratio)ratio).setNumerator(populateQuantity(fhirpath, parentPath + ".numerator", value, null,
                    connectorContext));
        } else if (leafFieldName.contains("denominator.")) {
            ((Ratio)ratio).setDenominator(populateQuantity(fhirpath, parentPath + ".denominator", value, null, connectorContext));
        }

        if (((Ratio)ratio).isEmpty()) return null;
        return (Ratio) ratio;
    }

    public static SampledData populateSampledData(String fhirpath, String parentPath, String value,
                                                  Object sampledData, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (sampledData == null) {
            sampledData = new SampledData();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) sampledData, connectorContext);
        if (leafFieldName.contains("origin.")) {
            ((SampledData)sampledData).setOrigin(populateSimpleQuantity(fhirpath, parentPath, value, null,
                    connectorContext));
        }
        switch (leafFieldName) {
            case "period":
                ((SampledData)sampledData).setPeriodElement(getDecimalType(value));
                break;
            case "factor":
                ((SampledData)sampledData).setFactorElement(getDecimalType(value));
                break;
            case "lowerLimit":
                ((SampledData)sampledData).setLowerLimitElement(getDecimalType(value));
                break;
            case "upperLimit":
                ((SampledData)sampledData).setUpperLimitElement(getDecimalType(value));
                break;
            case "dimensions":
                ((SampledData)sampledData).setDimensionsElement(getPositiveIntType(value));
                break;
            case "data":
                ((SampledData)sampledData).setDataElement(getStringType(value));
                break;
        }
        if (((SampledData)sampledData).isEmpty()) return null;
        return ((SampledData)sampledData);
    }

    public static Money populateMoney(String fhirpath, String parentPath, String value, Object money,
                                      FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (money == null) {
            money = new Money();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) money, connectorContext);
        if ("value".equals(leafFieldName)) {
            ((Money)money).setValueElement(getDecimalType(value));
        } else if ("currency".equals(leafFieldName)) {
            ((Money)money).setCurrencyElement(getCodeType(value));
        }
        if (((Money)money).isEmpty()) return null;
        return ((Money)money);
    }

    public static Annotation populateAnnotation(String fhirpath, String parentPath, String value, Object annotation,
                                                FHIRConnectorContext connectorContext) throws FHIRConnectException {

        if (annotation == null) {
            annotation = new Annotation();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) annotation, connectorContext);

        if ("time".equals(leafFieldName)) {
            ((Annotation) annotation).setTimeElement(getDateTimeType(value));
        } else if ("text".equals(leafFieldName)) {
            ((Annotation) annotation).setTextElement(getMarkdownType(value));
        } else if (leafFieldName.contains("authorString")) {
            ((Annotation) annotation).setAuthor(getStringType(value));
        } else if (leafFieldName.contains("authorReference")) {
            ((Annotation)annotation).setAuthor(populateReference(fhirpath, parentPath, value, null, connectorContext));
        }
        if (((Annotation) annotation).isEmpty()) return null;
        return ((Annotation)annotation);
    }

    public static Signature populateSignature(String fhirpath, String parentPath, String value, Object signature,
                                              FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (signature == null) {
            signature = new Signature();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) signature, connectorContext);

        switch (leafFieldName) {
            case "when":
                ((Signature)signature).setWhenElement(getInstantType(value));
                break;
            case "targetFormat":
                ((Signature)signature).setTargetFormatElement(getCodeType(value));
                break;
            case "sigFormat" :
                ((Signature)signature).setSigFormatElement(getCodeType(value));
                break;
            case "data":
                ((Signature)signature).setDataElement(getBase64BinaryType(value));
                break;
            default:
                ((Signature)signature).addType(populateCoding(fhirpath, parentPath, value, null, connectorContext));
                ((Signature)signature).setWho(populateReference(fhirpath, parentPath, value, null, connectorContext));
                ((Signature)signature).setOnBehalfOf(populateReference(fhirpath, parentPath, value, null,
                        connectorContext));
        }
        if (((Signature)signature).isEmpty()) return null;
        return ((Signature)signature);
    }

    public static ContactDetail populateContactDetail(String fhirpath, String parentPath, String value,
                                                      Object contactDetail, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (contactDetail == null) {
            contactDetail = new ContactDetail();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) contactDetail, connectorContext);

        if ("name".equals(leafFieldName)) {
            ((ContactDetail)contactDetail).setNameElement(getStringType(value));
        } else if (leafFieldName.contains("telecom.")) {
            ((ContactDetail)contactDetail).addTelecom(populateContactPoint(fhirpath, parentPath + ".telecom", value,
                    null,
                    connectorContext));
        }

        if (((ContactDetail)contactDetail).isEmpty()) return null;
        return ((ContactDetail)contactDetail);
    }

    public static Contributor populateContributor(String fhirpath, String parentPath, String value,
                                                  Object contributor, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (contributor == null) {
            contributor = new Contributor();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) contributor, connectorContext);

        if ("type".equals(leafFieldName)) {
            ((Contributor)contributor).setType(Contributor.ContributorType.fromCode(value));
        } else if ("name".equals(leafFieldName)) {
            ((Contributor)contributor).setNameElement(getStringType(value));
        } else if (leafFieldName.contains("contact.")) {
            ((Contributor)contributor).addContact(populateContactDetail(fhirpath, parentPath, value, null,
                    connectorContext));
        }
        if (((Contributor)contributor).isEmpty()) return null;
        return ((Contributor)contributor);
    }

    public static Reference populateReference(String fhirpath, String parentPath, String value, Object reference,
                                              FHIRConnectorContext connectorContext) throws FHIRConnectException{
        if (reference == null) {
            reference = new Reference();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) reference, connectorContext);

        switch (leafFieldName) {
            case "reference":
                ((Reference)reference).setReference(value);
                break;
            case "type":
                ((Reference)reference).setType(value);
                break;
            case "display":
                ((Reference)reference).setDisplay(value);
                break;
            default:
                if (leafFieldName.contains("identifier.")) {
                    ((Reference)reference).setIdentifier(populateIdentifier(fhirpath, parentPath + ".identifier", value,
                            ((Reference)reference).getIdentifier(),
                            connectorContext));
                }
        }
        if (((Reference)reference).isEmpty()) return null;
        return ((Reference)reference);
    }

    public static Meta populateMeta(String fhirpath, String parentPath, String value, Object meta,
                                    FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (meta == null) {
            meta = new Meta();
        }
        String leafFieldName = getLeafFieldName(fhirpath, parentPath);
        populateElement(fhirpath, value, (Element) meta, connectorContext);

        switch (leafFieldName) {
            case "versionId":
                ((Meta)meta).setVersionIdElement(getIdType(value));
                break;
            case "lastUpdated":
                ((Meta)meta).setLastUpdatedElement(getInstantType(value));
                break;
            case "source":
                ((Meta)meta).setSourceElement(getUriType(value));
                break;
            case "profile":
                ((Meta)meta).addProfile(value);
                break;
            default:
                if (leafFieldName.contains("security.")) {
                    ((Meta)meta).addSecurity(populateCoding(fhirpath, parentPath, value, null, connectorContext));
                } else if (leafFieldName.contains("tag.")) {
                    ((Meta)meta).addSecurity(populateCoding(fhirpath, parentPath, value, null, connectorContext));
                }
        }
        if (((Meta)meta).isEmpty()) return null;
        return ((Meta)meta);
    }

    private static String getDataObjectNotFoundErrorMessage(String path, String value) {

        return "DataType object not found in the data map. [path : " + path + " " + "| value:" + value + "]";
    }

    private static DataType getDataObject(String path, Map<String, String> params, FHIRConnectorContext context) throws FHIRConnectException {
        if (path != null && !path.isEmpty()) {
            String refinedPath = path;
            if (path.endsWith(".")) {
                refinedPath = path.substring(0, path.length() - 1);
            }
            String objId = params.get(refinedPath);
            if (objId != null) {
                DataType dataType = context.getDataObject(objId);
                if (dataType != null) {
                    return  dataType;
                } else {
                    throw new FHIRConnectException(getDataObjectNotFoundErrorMessage(refinedPath, objId));
                }
            }
        }
        return null;
    }

    public static void populateElement(String fhirpath, String value, Element parent,
                                       FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String leafFieldName = fhirpath.substring(fhirpath.lastIndexOf(".") + 1);
        switch (leafFieldName) {
            case "id":
                parent.setId(value);
                break;
            case "extension":
                if (value != null) {
                    String[] extentions = value.split(",");
                    for (String objId : extentions) {
                        DataType dataObj = connectorContext.getDataObject(objId);
                        if (dataObj != null && dataObj.unwrap() instanceof Extension) {
                            parent.addExtension((Extension) dataObj.unwrap());
                        }
                    }
                }
                break;
            default:
                //do nothing
        }
    }

    public static Extension getExtensionByValueType(List<String> urls, String dataType, String fhirpath,
                                                    String parentPath, String value,
                                                    Object extension, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        if (extension == null) {
            extension = new Extension();
        }
        List<Extension> extensionList = new ArrayList<>();
        extensionList.add((Extension) extension);
        if (urls.size() > 1) {
            ((Extension)extension).setUrl(urls.get(0));
            ListIterator<String> stringListIterator = urls.listIterator(1);
            populateNestedExtension((Extension) extension, stringListIterator, extensionList);
        } else if (urls.size() == 1) {
            ((Extension)extension).setUrl(urls.get(0));
        }
        Extension targetExtension = extensionList.get(extensionList.size() - 1);
        DataType dataTypeObject = getDataTypeObject(dataType, fhirpath, parentPath, value,
                targetExtension.getValue(), connectorContext);
        if (dataTypeObject != null) {
            targetExtension.setValue(dataTypeObject.unwrap());
        }

        if (((Extension)extension).isEmpty()) return null;
        return ((Extension)extension);
    }

    private static void populateNestedExtension(Extension extension, ListIterator<String> stringListIterator,
                                                List<Extension> extensionList) {
        if (stringListIterator.hasNext()) {
            String next = stringListIterator.next();
            Extension childExtension = extension.getExtensionByUrl(next);
            if (childExtension == null) {
                childExtension = new Extension();
                childExtension.setUrl(next);
                extension.addExtension(childExtension);
            }
            extensionList.add(childExtension);
            populateNestedExtension(childExtension, stringListIterator, extensionList);
        }
    }

    public static SpecialPurposeDataType getExtensionDataType(Object extension) throws FHIRConnectException {
        if (extension instanceof Extension) {
            return new SpecialPurposeDataType(null, (Extension)extension);
        }
        return null;
    }

    /*****************************************************************************************************************
     *                  Simple / primitive types, which are single elements with a primitive value
     *****************************************************************************************************************/
    public static BooleanType getBooleanType(String value) throws FHIRConnectException {
        if (value != null) {
            return new BooleanType(value);
        }
        return null;
    }

    public static boolean getBoolean(String value) throws FHIRConnectException {
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return false;
    }

    //TODO check type conversions integer<->string
    public static IntegerType getIntegerType(String value) throws FHIRConnectException {
        if (value != null) {
            return new IntegerType(value);
        }
        return null;
    }

    public static int getInteger(String value) throws FHIRConnectException {
        if (value != null) {
            return Integer.parseInt(value);
        }
        return -1;
    }

    public static StringType getStringType(String value) throws FHIRConnectException {
        if (value != null) {
            return new StringType(value);
        }
        return null;
    }

    public static String getString(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static DecimalType getDecimalType(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            try {
                return new DecimalType(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                String msg = "The input for the field is not a valid decimal number for value: " + value;
                FHIRConnectorUtils.handleException(msg, e);
            }
        }
        return null;
    }

    public static BigDecimal getDecimal(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            try {
                return BigDecimal.valueOf(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                String msg = "The input for the field is not a valid decimal number for value: " + value;
                FHIRConnectorUtils.handleException(msg, e);
            }
        }
        return null;
    }

    public static UriType getUriType(String value) throws FHIRConnectException {
        if (value != null) {
            return new UriType(value);
        }
        return null;
    }

    public static String getUri(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static UrlType getUrlType(String value) throws FHIRConnectException {
        if (value != null) {
            return new UrlType(value);
        }
        return null;
    }

    public static String getUrl(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static CanonicalType getCanonicalType(String value) throws FHIRConnectException {
        if (value != null) {
            return new CanonicalType(value);
        }
        return null;
    }

    public static String getCanonical(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static Base64BinaryType getBase64BinaryType(String value) throws FHIRConnectException {
        if (value != null) {
            return new Base64BinaryType(value);
        }
        return null;
    }

    public static byte[] getBase64Binary(String value) throws FHIRConnectException {
        if (value != null) {
            return value.getBytes();
        }
        return null;
    }

    public static InstantType getInstantType(String value) throws FHIRConnectException {
        if (value != null) {
            return new InstantType(FHIRDataTypeUtils.parseToDate(value));
        }
        return null;
    }

    public static Date getInstant(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            return FHIRDataTypeUtils.parseToDate(value);
        }
        return null;
    }

    public static DateType getDateType(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            return new DateType(FHIRDataTypeUtils.parseToDate(value));
        }
        return null;
    }

    public static Date getDate(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            return FHIRDataTypeUtils.parseToDate(value);
        }
        return null;
    }

    public static DateTimeType getDateTimeType(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            return new DateTimeType(FHIRDataTypeUtils.parseToDate(value));
        }
        return null;
    }

    public static Date getDateTime(String value) throws FHIRConnectException {
        if (StringUtils.isNotBlank(value)) {
            return FHIRDataTypeUtils.parseToDate(value);
        }
        return null;
    }


    public static TimeType getTimeType(String value) throws FHIRConnectException {
        if (value != null) {
            return new TimeType(value);
        }
        return null;
    }

    //TODO check for additional business logic for type conversion
    public static String getTime(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static CodeType getCodeType(String value) throws FHIRConnectException {
        if (value != null) {
            return new CodeType(value);
        }
        return null;
    }

    public static String getCode(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static OidType getOidType(String value) throws FHIRConnectException {
        if (value != null) {
            return new OidType(value);
        }
        return null;
    }

    public static String getOid(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static IdType getIdType(String value) throws FHIRConnectException {
        if (value != null) {
            return new IdType(value);
        }
        return null;
    }

    public static String getId(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static MarkdownType getMarkdownType(String value) throws FHIRConnectException {
        if (value != null) {
            return new MarkdownType(value);
        }
        return null;
    }

    public static String getMarkdown(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static UnsignedIntType getUnsignedIntType(String value) throws FHIRConnectException {
        if (value != null) {
            return new UnsignedIntType(value);
        }
        return null;
    }

    public static Integer getUnsignedInt(String value) throws FHIRConnectException {
        if (value != null) {
            return Integer.parseUnsignedInt(value);
        }
        return null;
    }

    public static PositiveIntType getPositiveIntType(String value) throws FHIRConnectException {
        if (value != null) {
            return new PositiveIntType(value);
        }
        return null;
    }

    public static Integer getPositiveInt(String value) throws FHIRConnectException {
        if (value != null) {
            return Integer.parseUnsignedInt(value);
        }
        return null;
    }

    public static UuidType getUuidType(String value) throws FHIRConnectException {
        if (value != null) {
            return new UuidType(value);
        }
        return null;
    }

    public static String getUuid(String value) throws FHIRConnectException {
        if (value != null) {
            return value;
        }
        return null;
    }

    public static void addBundleEntry(FHIRConnectorContext connectorContext, String resourceObjId) {
        Bundle containerBundle = connectorContext.getContainerResource();
        if (containerBundle != null && containerBundle.unwrap() != null) {
            org.wso2.healthcare.integration.fhir.model.Resource targetResource =
                    (StringUtils.isEmpty(resourceObjId)) ? connectorContext.getTargetResource() : connectorContext.getResource(resourceObjId);
            containerBundle.addResourceAsBundleEntry(targetResource, connectorContext);
        }
    }

    private static String getLeafFieldName(String fhirpath, String parentPath) {
        if (StringUtils.isNotBlank(parentPath) && fhirpath.startsWith(parentPath)) {
            return fhirpath.substring(parentPath.length() + 1);
        } else {
            return fhirpath.substring(fhirpath.lastIndexOf(".") + 1);
        }
    }
}
