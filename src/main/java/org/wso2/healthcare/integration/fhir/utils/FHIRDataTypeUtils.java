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

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.codesystems.CodeSystemEnumGenFactory;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorConfig;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.utils.type.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class FHIRDataTypeUtils {

    private static Log LOG = LogFactory.getLog(FHIRDataTypeUtils.class);
    private static HashMap<String, TypeFactory> typeMap = new HashMap<>(4);

    static {
        //Populate FHIR Data Types
        populateTypeMap();
    }

    public static HashMap<String, TypeFactory> getTypeMap() {
        return typeMap;
    }

    public static void populateBaseResource(Resource resource, Map<String, String> baseResourceFields) throws FHIRConnectException {
        if (resource == null || baseResourceFields == null) {
            throw new FHIRConnectException("Resource and baseResourceFields must not null");
        }
        if (baseResourceFields.get("id") != null) {
            resource.setId(baseResourceFields.get("id"));
        }
        Meta metadata = new Meta();
        if (baseResourceFields.get("meta.versionId") != null) {
            metadata.setVersionId(baseResourceFields.get("meta.versionId"));
        }
        if (baseResourceFields.get("meta.lastUpdated") != null) {
            metadata.setLastUpdated(parseToDate(baseResourceFields.get("meta.lastUpdated")));
        }
        if (baseResourceFields.get("meta.source") != null) {
            metadata.setSource(baseResourceFields.get("meta.source"));
        }
        //TODO look for a way to add more profiles at once
        if (baseResourceFields.get("meta.profile") != null) {
            metadata.addProfile(baseResourceFields.get("meta.profile"));
        }
        //TODO look for a way to add more security at once
        if (baseResourceFields.get("meta.security") != null) {
            // if the coding is provided in single string using '|' as separator
            if (baseResourceFields.get("meta.security").contains("\\|")) {
                Coding coding = Factory.makeCoding(baseResourceFields.get("meta.security"));
                metadata.addSecurity(coding);
            }
        } else if (baseResourceFields.get("meta.security.system") != null
                && baseResourceFields.get("meta.security.code") != null) {
            metadata.addSecurity(baseResourceFields.get("meta.security.system"),
                    baseResourceFields.get("meta.security.code"), baseResourceFields.get("meta.security.display"));
        }
        if (baseResourceFields.get("meta.tag") != null) {
            // if the coding is provided in single string using '|' as separator
            if (baseResourceFields.get("meta.tag").contains("\\|")) {
                Coding coding = Factory.makeCoding(baseResourceFields.get("meta.tag"));
                metadata.addTag(coding);
            }
        } else if (baseResourceFields.get("meta.tag") != null
                && baseResourceFields.get("meta.tag") != null) {
            metadata.addTag(baseResourceFields.get("meta.tag"),
                    baseResourceFields.get("meta.tag"), baseResourceFields.get("meta.tag"));
        }
        resource.setMeta(metadata);
        if (baseResourceFields.get("implicitRules") != null) {
            resource.setImplicitRules(baseResourceFields.get("implicitRules"));
        }
        if (baseResourceFields.get("language") != null) {
            resource.setLanguage(baseResourceFields.get("language"));
        }
        if (baseResourceFields.get("text.status") != null && baseResourceFields.get("text.div") != null) {
            try {
                //text.div content will be wrapped by a <div></div> tag
                Narrative textNarrative = Factory
                        .newNarrative(Narrative.NarrativeStatus.fromCode(baseResourceFields.get("text.status")),
                                baseResourceFields.get("text.div"));
                ((DomainResource) resource).setText(textNarrative);
            } catch (IOException | FHIRException e) {
                String msg = "Error occurred while setting the narrative field";
                handleException(msg, e);
            }
        }
    }

    /**
     * @param resourceAsString JSON string of a FHIR resource
     * @return FHIR Resource object(HAPI-FHIR) (According to resourceType of the input string)
     */
    public static IBaseResource parseResource(String resourceAsString, String type) {

        FhirContext fhirContext = FHIRConnectorConfig.getInstance().getFhirContext();
        if (FHIRConstants.JSON.equals(type)) {
            return fhirContext.newJsonParser().parseResource(resourceAsString);
        } else {
            return fhirContext.newXmlParser().parseResource(resourceAsString);
        }
    }

    public static Resource getResource(String prefix, Map<String, String> resourceFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {

        org.wso2.healthcare.integration.fhir.model.Resource resource = null;
        if (resourceFields.get(prefix) != null) {
            resource = connectorContext.getResource(resourceFields.get(prefix));
        } else {
            resource = connectorContext.getTargetResource();
        }
        //TODO consider constructing resources from connector parameters
        return resource != null ? resource.unwrap() : null;
    }

    public static Identifier getIdentifier(String prefix, Map<String, String> identifierFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Identifier identifier = new Identifier();
        populateElement(prefix,identifier, identifierFields, connectorContext);

        if (identifierFields.get(prefix + "use") != null) {
            try {
                identifier.setUse(Identifier.IdentifierUse.fromCode(identifierFields.get(prefix + "use")));
            } catch (FHIRException e) {
                String msg = "Error occurred while setting the identifier.use field";
                handleException(msg, e);
            }
        }
        if (identifierFields.get(prefix + "system") != null) {
            identifier.setSystem(identifierFields.get(prefix + "system"));
        }

        CodeableConcept codeableConcept = getCodeableConcept(
                "http://hl7.org/fhir/ValueSet/identifier-type", prefix + "type", identifierFields, connectorContext);
        identifier.setType(codeableConcept);

        if (identifierFields.get(prefix + "system") != null) {
            identifier.setSystem(identifierFields.get(prefix + "system"));
        }

        if (identifierFields.get(prefix + "value") != null) {
            identifier.setValue(identifierFields.get(prefix + "value"));
        }

        identifier.setPeriod(getPeriod(prefix + "period.", identifierFields, connectorContext));

        Reference reference = new Reference();
        if (identifierFields.get(prefix + "assigner.reference") != null) {
            reference.setReference(identifierFields.get(prefix + "assigner.reference"));
        }

        if (identifierFields.get(prefix + "assigner.display") != null) {
            reference.setDisplay(identifierFields.get(prefix + "assigner.display"));
        }

        if (identifierFields.get(prefix + "assigner.type") != null) {
            reference.setType(identifierFields.get(prefix + "assigner.type"));
        }

        Identifier referenceIdentifier = new Identifier();
        if (identifierFields.get(prefix + "assigner.identifier.use") != null) {
            try {
                referenceIdentifier.setUse(Identifier.IdentifierUse
                        .fromCode(identifierFields.get(prefix + "assigner.identifier.use")));
            } catch (FHIRException e) {
                String msg = "Error occurred while setting the identifier.use field";
                handleException(msg, e);
            }
        }

        if (identifierFields.get(prefix + "assigner.identifier.type") != null) {
            CodeableConcept concept = getCodeableConcept(
                    "http://hl7.org/fhir/ValueSet/identifier-type", prefix + "assigner.identifier.type",
                    identifierFields, connectorContext);
            referenceIdentifier.setType(concept);
        }

        if (!referenceIdentifier.isEmpty()) {
            reference.setIdentifier(referenceIdentifier);
        }

        if (!reference.isEmpty()) {
            identifier.setAssigner(reference);
        }

        // TODO This implementation is being restricted intentionally
        /*if (identifierFields.get(prefix + "assigner.identifier.period") != null) {
            reference.setIdentifier(
                    referenceIdentifier.setPeriod(getPeriod(prefix + "assigner.identifier.period.", identifierFields,
                            connectorContext)));
        }*/

        if (identifier.isEmpty()) return null;
        return identifier;
    }

    public static HumanName getHumanName(String prefix, Map<String, String> nameFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        HumanName name = new HumanName();
        populateElement(prefix, name, nameFields, connectorContext);

        if (nameFields.get(prefix + "use") != null) {
            try {
                name.setUse(HumanName.NameUse.fromCode(nameFields.get(prefix + "use")));
            } catch (FHIRException e) {
                String msg = "Error occurred while setting the name.use field";
                handleException(msg, e);
            }
        }
        if (nameFields.get(prefix + "text") != null) {
            name.setText(nameFields.get(prefix + "text"));
        }
        if (nameFields.get(prefix + "family") != null) {
            name.setFamily(nameFields.get(prefix + "family"));
        }
        if (nameFields.get(prefix + "given") != null) {
            List<StringType> givenNameList = new ArrayList<>();
            String[] split = nameFields.get(prefix + "given").split("\\s*,\\s*");
            for (String s : split) {
                givenNameList.add(new StringType(s));
            }
            name.setGiven(givenNameList);
        }
        if (nameFields.get(prefix + "prefix") != null) {
            List<StringType> prefixList = new ArrayList<>();
            String[] split = nameFields.get(prefix + "prefix").split(",");
            for (String s : split) {
                prefixList.add(new StringType(s));
            }
            name.setPrefix(prefixList);
        }
        //TODO provide evaluating the string array from the xpath
        if (nameFields.get(prefix + "suffix") != null) {
            List<StringType> suffixList = new ArrayList<>();
            String[] split = nameFields.get(prefix + "suffix").split(",");
            for (String s : split) {
                suffixList.add(new StringType(s));
            }
            name.setSuffix(suffixList);
        }
        name.setPeriod(getPeriod(prefix + "period.", nameFields, connectorContext));

        if (name.isEmpty()) return null;
        return name;
    }

    //TODO review the getting common types from the given key value map
    public static ContactPoint getContactPoint(String prefix, Map<String, String> contactFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        ContactPoint contactPoint = new ContactPoint();
        populateElement(prefix, contactPoint, contactFields, connectorContext);

        if (StringUtils.isNotBlank(contactFields.get(prefix + "system"))) {
            contactPoint.setSystem(ContactPoint.ContactPointSystem.fromCode(contactFields.get(prefix + "system")));
        }
        if (StringUtils.isNotBlank(contactFields.get(prefix + "use"))) {
            contactPoint.setUse(ContactPoint.ContactPointUse.fromCode(contactFields.get(prefix + "use")));
        }
        if (StringUtils.isNotBlank(contactFields.get(prefix + "value"))) {
            contactPoint.setValue(contactFields.get(prefix + "value"));
        }
        if (StringUtils.isNotBlank(contactFields.get(prefix + "rank"))) {
            contactPoint.setRank(Integer.parseInt(contactFields.get(prefix + "rank")));
        }

        contactPoint.setPeriod(getPeriod(prefix + "period.", contactFields, connectorContext));

        if (contactPoint.isEmpty()) return null;
        return contactPoint;
    }

    public static Address getAddress(String prefix, Map<String, String> addressFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Address address = new Address();
        populateElement(prefix, address, addressFields, connectorContext);

        try {
            if (StringUtils.isNotBlank(addressFields.get(prefix + "use"))) {
                address.setUse(Address.AddressUse.fromCode(addressFields.get(prefix + "use")));
            }
            if (StringUtils.isNotBlank(addressFields.get(prefix + "type"))) {
                address.setType(Address.AddressType.fromCode(addressFields.get(prefix + "type")));
            }
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the address.use/type field(s).";
            handleException(msg, e);
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "text"))) {
            address.setText(addressFields.get(prefix + "text"));
        }
        //TODO 0.* field. look for a way to update address lines
        if (StringUtils.isNotBlank(addressFields.get(prefix + "line"))) {
            List<StringType> lines = new ArrayList<>();
            String[] split = addressFields.get(prefix + "line").split(",");
            for (String s : split) {
                lines.add(new StringType(s));
            }
            address.setLine(lines);
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "city"))) {
            address.setCity(addressFields.get(prefix + "city"));
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "district"))) {
            address.setDistrict(addressFields.get(prefix + "district"));
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "state"))) {
            address.setState(addressFields.get(prefix + "state"));
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "postalCode"))) {
            address.setPostalCode(addressFields.get(prefix + "postalCode"));
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "country"))) {
            address.setCountry(addressFields.get(prefix + "country"));
        }
        if (StringUtils.isNotBlank(addressFields.get(prefix + "period"))) {
            address.setPeriod(getPeriod(prefix, addressFields, connectorContext));
        }

        if (address.isEmpty()) return null;
        return address;
    }

    public static Attachment getAttachment(String prefix, Map<String, String> attachmentFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Attachment attachment = new Attachment();
        populateElement(prefix, attachment, attachmentFields, connectorContext);

        if (attachmentFields.get(prefix + "contentType") != null) {
            attachment.setContentType(attachmentFields.get(prefix + "contentType"));
        }
        if (attachmentFields.get(prefix + "language") != null) {
            attachment.setLanguage(attachmentFields.get(prefix + "language"));
        }
        if (attachmentFields.get(prefix + "data") != null) {
            attachment.setData(attachmentFields.get(prefix + "data").getBytes());
        }
        if (attachmentFields.get(prefix + "url") != null) {
            attachment.setUrl(attachmentFields.get(prefix + "url"));
        }
        if (attachmentFields.get(prefix + "size") != null) {
            attachment.setSize(Integer.valueOf(attachmentFields.get(prefix + "size")));
        }
        if (attachmentFields.get(prefix + "hash") != null) {
            attachment.setHash(attachmentFields.get(prefix + "hash").getBytes());
        }
        if (attachmentFields.get(prefix + "title") != null) {
            attachment.setTitle(attachmentFields.get(prefix + "title"));
        }
        if (attachmentFields.get(prefix + "creation") != null) {
            attachment.setCreation(parseToDate(attachmentFields.get(prefix + "creation")));
        }

        if (attachment.isEmpty()) return null;
        return attachment;
    }

    public static CodeableConcept getCodeableConcept(String valueSetUrl, String codeFPath, Map<String, String> codeableFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        CodeSystemEnumGenFactory codeSystemEnumGenFactory = new CodeSystemEnumGenFactory();
        CodeableConcept codeableConcept = new CodeableConcept();
        populateElement(codeFPath, codeableConcept, codeableFields, connectorContext);

        if (codeableFields.get(codeFPath) != null) {
            codeableConcept.addCoding(codeSystemEnumGenFactory.createCoding(valueSetUrl, codeableFields.get(codeFPath)));
        } else {
            // If reach here, it's a user defined codeableConcept, hence no need to resolve valueSet/codeSystem
            codeableConcept.addCoding(getCoding(codeFPath + ".coding.", codeableFields, connectorContext));
        }
        codeableConcept.setText(codeableFields.get(codeFPath + ".text"));
        if (codeableConcept.isEmpty()) return null;
        return codeableConcept;
    }

    public static CodeableConcept getCodeableConcept(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        CodeableConcept codeableConcept = new CodeableConcept();
        populateElement(prefix, codeableConcept, params, connectorContext);

        // TODO We do not need to support resolving codes for unknown codes.  Hence following code block is not needed. Clean this later
        /*if (prefix != null && !prefix.isEmpty()) {
            if (params.get(prefix + "coding.valueSet") != null) {
                // If valueSet is defined if could be a value set defined by the FHIR spec
                // remove ending '.' character
                String codeableConceptParamKey = prefix.substring(0, prefix.length() - 1);
                String code = params.get(codeableConceptParamKey);
                if (code != null) {
                    // Override with the value defined in the high level parameter
                    params.put(prefix + "coding.code", code);
                }
            } else {
                // TODO handle objectID scenario
            }
        }*/

        codeableConcept.addCoding(getCoding(prefix + "coding.", params, connectorContext));
        codeableConcept.setText(params.get(prefix + "text"));

        if (codeableConcept.isEmpty()) return null;
        return codeableConcept;
    }

    public static Coding getCoding(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Coding coding;
        String code = params.get(prefix + "code");
        if (code == null) {
            code = params.get(trimFHIRPrefixPath(prefix));
        }
        String valueSet = params.get(prefix + "valueSet");
        if (valueSet != null  && code != null) {
            if (LOG.isDebugEnabled()) {
                //TODO handle value set
                LOG.debug("Retrieving code system for code :" + code + " from valueSet:" + valueSet);
            }
            CodeSystemEnumGenFactory codeSystemEnumGenFactory = new CodeSystemEnumGenFactory();
            coding = codeSystemEnumGenFactory.createCoding(valueSet, code);
            if (coding == null) {
                throw new FHIRConnectException("Unknown system : " + valueSet + " or unknown code:" + code);
            }
        } else {
            LOG.debug("User has not given ValueSet. Hence consider as custom values");
            coding = new Coding();
            if (StringUtils.isNotBlank(code) && StringUtils
                    .isNotBlank(params.get(prefix + "system"))) {
                coding.setSystem(params.get(prefix + "system"));
                coding.setCode(code);
                coding.setDisplay(params.get(prefix + "display"));
            }
        }
        coding.setVersion(params.get(prefix + "version"));

        String userSelected = params.get(prefix + "userSelected");
        if (userSelected != null) coding.setUserSelected(Boolean.parseBoolean(userSelected));

        if (coding.isEmpty()) return null;
        return coding;
    }

    public static Period getPeriod(String prefix, Map<String, String> periodFields,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO add date conversion validation
        Period period = new Period();
        populateElement(prefix, period, periodFields, connectorContext);

        String start = periodFields.get(prefix + "start");
        if (StringUtils.isNotBlank(start)) {
            period.setStart(parseToDate(start));
        }

        String end = periodFields.get(prefix + "end");
        if (StringUtils.isNotBlank(end)) {
            period.setEnd(parseToDate(end));
        }

        if (period.isEmpty()) return null;
        return period;
    }

    /*****************************************************************************************************************
     *                  Simple / primitive types, which are single elements with a primitive value
     *****************************************************************************************************************/
    public static BooleanType getBooleanType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new BooleanType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static boolean getBoolean(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return Boolean.valueOf(connectorInputParameters.get(trimmedFHIRPath));
        }
        return false;
    }

    //TODO check type conversions integer<->string
    public static IntegerType getIntegerType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new IntegerType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static int getInteger(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            Integer integer = Integer.valueOf(connectorInputParameters.get(trimmedFHIRPath));
            return (int)integer;
        }
        return -1;
    }

    public static StringType getStringType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new StringType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getString(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static DecimalType getDecimalType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (StringUtils.isNotBlank(connectorInputParameters.get(trimmedFHIRPath))) {
            try {
                double value = Double.parseDouble(connectorInputParameters.get(trimmedFHIRPath));
                return new DecimalType(value);
            } catch (NumberFormatException e) {
                String msg = "The input for the field is not a valid decimal number for FHIR path: " + trimmedFHIRPath;
                handleException(msg, e);
            }
        }
        return null;
    }

    public static BigDecimal getDecimal(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (StringUtils.isNotBlank(connectorInputParameters.get(trimmedFHIRPath))) {
            try {
                double value = Double.parseDouble(connectorInputParameters.get(trimmedFHIRPath));
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                String msg = "The input for the field is not a valid decimal number for FHIR path: " + trimmedFHIRPath;
                handleException(msg, e);
            }
        }
        return null;
    }

    public static UriType getUriType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new UriType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getUri(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static UrlType getUrlType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new UrlType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getUrl(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static CanonicalType getCanonicalType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new CanonicalType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getCanonical(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static Base64BinaryType getBase64BinaryType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new Base64BinaryType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static byte[] getBase64Binary(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath).getBytes();
        }
        return null;
    }

    public static InstantType getInstantType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new InstantType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    //TODO validate date string
    public static Date getInstant(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return parseToDate(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static DateType getDateType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new DateType(parseToDate(connectorInputParameters.get(trimmedFHIRPath)));
        }
        return null;
    }

    public static Date getDate(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return parseToDate(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static DateTimeType getDateTimeType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new DateTimeType(parseToDate(connectorInputParameters.get(trimmedFHIRPath)));
        }
        return null;
    }

    public static Date getDateTime(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return parseToDate(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    /**
     * Function to create Date object from given date string
     *
     * @param dateStr
     * @return
     * @throws FHIRConnectException
     */
    public static Date parseToDate(String dateStr) throws FHIRConnectException {
        String pattern;
        switch (dateStr.length()) {
            case 4:
                //format: YYYY
                pattern = "yyyy";
                break;
            case 7:
                //format: YYYY-MM
                pattern = "yyyy-MM";
                break;
            case 10:
                //format: YYYY-MM-DD
                pattern = "yyyy-MM-dd";
                break;
            case 19:
                //format: YYYY-MM-DDThh:mm:ss
                pattern = "yyyy-MM-dd'T'HH:mm:ss";
                break;
            case 20:
            case 25:
                //format: YYYY-MM-DDThh:mm:ss+zz:zz
                pattern = "yyyy-MM-dd'T'HH:mm:ssX";
                break;
            case 24:
                //format: YYYY-MM-DDThh:mm:ss+zz:zz
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
                break;
            case 29:
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
                break;
            default:
                throw new FHIRConnectException("Unsupported Date format : " + dateStr);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new FHIRConnectException("Error occurred while parsing the date input : " + dateStr, e);
        }
    }

    public static TimeType getTimeType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new TimeType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    //TODO check for additional business logic for type conversion
    public static String getTime(String path, Map<String, String> connectorInputParameters,
                                 FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static CodeType getCodeType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new CodeType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getCode(String path, Map<String, String> connectorInputParameters,
                                       FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static OidType getOidType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new OidType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getOid(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static IdType getIdType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new IdType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getId(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static MarkdownType getMarkdownType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new MarkdownType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getMarkdown(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }

    public static UnsignedIntType getUnsignedIntType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new UnsignedIntType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static Integer getUnsignedInt(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return Integer.parseUnsignedInt(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static PositiveIntType getPositiveIntType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new PositiveIntType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static Integer getPositiveInt(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return Integer.parseUnsignedInt(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static UuidType getUuidType(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return new UuidType(connectorInputParameters.get(trimmedFHIRPath));
        }
        return null;
    }

    public static String getUuid(String path, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String trimmedFHIRPath = trimFHIRPrefixPath(path);
        if (connectorInputParameters.get(trimmedFHIRPath) != null) {
            return connectorInputParameters.get(trimmedFHIRPath);
        }
        return null;
    }


    /*****************************************************************************************************************
     *                  General-purpose complex types, which are re-usable clusters of elements
     *****************************************************************************************************************/

    public static void populateElement(String prefix, Element parent, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        String id = params.get(prefix + "id");
        if (id != null) {
            parent.setId(id);
        }

        String extension = params.get(prefix + "extension");
        if (extension != null) {
            String[] extentions = extension.split(",");
            for (String objId : extentions) {
                DataType dataObj = connectorContext.getDataObject(objId);
                if (dataObj != null && dataObj.unwrap() instanceof Extension) {
                    parent.addExtension((Extension) dataObj.unwrap());
                }
            }
        }
    }


    public static Quantity getQuantity(String prefix, Map<String, String> params,
                                                    FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Quantity quantity = populateQuantity(prefix, null, params, connectorContext);
        if (quantity.isEmpty()) return null;
        return quantity;
    }

    public static Quantity populateQuantity(String prefix, Quantity parent, Map<String, String> params,
                                                    FHIRConnectorContext connectorContext) throws FHIRConnectException {

        Quantity quantity;
        if (parent == null) {
            quantity = new Quantity();
        } else {
            quantity = parent;
        }

        populateElement(prefix, quantity, params, connectorContext);

        String tempParamValue;

        quantity.setValueElement(getDecimalType(prefix + "value", params, connectorContext));

        if ((tempParamValue = params.get(prefix + "comparator")) != null) {
            quantity.setComparator(Quantity.QuantityComparator.fromCode(tempParamValue));
        }
        quantity.setUnitElement(getStringType(prefix + "unit", params, connectorContext));

        //TODO check for rules get validated at validation phase
        quantity.setSystemElement(getUriType(prefix + "system", params, connectorContext));
        quantity.setCodeElement(getCodeType(prefix + "code", params, connectorContext));

        return quantity;
    }

    public static Duration getDuration(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO check for rules get validated at validation phase
        Duration duration = new Duration();
        populateQuantity(prefix, duration, params, connectorContext);
        if (duration.isEmpty()) return null;
        return duration;
    }

    public static Distance getDistance(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO check for rules get validated at validation phase
        Distance distance = new Distance();
        populateQuantity(prefix, distance, params, connectorContext);
        if (distance.isEmpty()) return null;
        return distance;
    }

    public static Count getCount(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO check for rules get validated at validation phase
        Count count = new Count();
        populateQuantity(prefix, count, params, connectorContext);
        if (count.isEmpty()) return null;
        return count;
    }

    public static Age getAge(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO check for rules get validated at validation phase
        Age age = new Age();
        populateQuantity(prefix, age, params, connectorContext);
        if (age.isEmpty()) return null;
        return age;
    }

    public static MoneyQuantity getMoneyQuantity(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO check for rules get validated at validation phase
        MoneyQuantity moneyQuantity = new MoneyQuantity();
        populateQuantity(prefix, moneyQuantity, params, connectorContext);
        if (moneyQuantity.isEmpty()) return null;
        return moneyQuantity;
    }

    public static SimpleQuantity getSimpleQuantity(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //TODO check for rules get validated at validation phase
        SimpleQuantity simpleQuantity = new SimpleQuantity();
        populateQuantity(prefix, simpleQuantity, params, connectorContext);
        if (simpleQuantity.isEmpty()) return null;
        return simpleQuantity;
    }

    public static Range getRange(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Range range = new Range();
        populateElement(prefix, range, params, connectorContext);
        range.setLow(getSimpleQuantity(prefix + "low.", params, connectorContext));
        range.setHigh(getSimpleQuantity(prefix + "high.", params, connectorContext));
        if (range.isEmpty()) return null;
        return range;
    }


    public static Timing getTiming(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {

        Timing timing;
        if (prefix != null && !prefix.trim().isEmpty()) {
            DataType dataType = getDataObject(prefix, params, connectorContext);
            if (dataType != null) {
                return (Timing) dataType.unwrap();
            }
        }

        timing = new Timing();
        String tempParamValue;

        populateElement(prefix, timing, params, connectorContext);

        Timing.TimingRepeatComponent repeatComponent = new Timing.TimingRepeatComponent();
        timing.addEvent(getDateTime(prefix + "event", params, connectorContext));

        /*if ((tempParamValue = params.get(prefix + "repeat.boundsDuration")) != null) {
            DataType dataTypeObj = connectorContext.getDataObject(params.get(tempParamValue));
            if (dataTypeObj != null) {
                repeatComponent.setBounds(dataTypeObj.unwrap());
            } else {
                throw new FHIRConnectException(getDataObjectNotFoundErrorMessage(tempParamValue, params.get(tempParamValue)));
            }
        } else if ((tempParamValue = params.get(prefix + "repeat.boundsRange")) != null) {
            DataType dataTypeObj = connectorContext.getDataObject(params.get(tempParamValue));
            if (dataTypeObj != null) {
                repeatComponent.setBounds(dataTypeObj.unwrap());
            } else {
                throw new FHIRConnectException(getDataObjectNotFoundErrorMessage(tempParamValue, params.get(tempParamValue)));
            }
        } else if ((tempParamValue = params.get(prefix + "repeat.boundsPeriod")) != null) {
            DataType dataTypeObj = connectorContext.getDataObject(params.get(tempParamValue));
            if (dataTypeObj != null) {
                repeatComponent.setBounds(dataTypeObj.unwrap());
            } else {
                throw new FHIRConnectException(getDataObjectNotFoundErrorMessage(tempParamValue, params.get(tempParamValue)));
            }
        } else {
            Duration duration = getDuration(prefix + "repeat.boundsDuration.", params, connectorContext);
            if (!duration.isEmpty()) {
                repeatComponent.setBounds(duration);
            } else {
                Range range = getRange(prefix + "repeat.boundsRange.", params, connectorContext);
                if (!range.isEmpty()) {
                    repeatComponent.setBounds(range);
                } else {
                    Period period = getPeriod(prefix + "repeat.boundsPeriod.", params, connectorContext);
                    if (!period.isEmpty()) {
                        repeatComponent.setBounds(period);
                    }
                }
            }
        }*/

        String[] boundTypes = {"Duration", "Range", "Period"};
        repeatComponent.setBounds(
                resolveAndGetMultipleChoiceType(prefix + "repeat.", "bounds", boundTypes, params, connectorContext));

        repeatComponent.setCountElement(getPositiveIntType(prefix + "repeat.count", params, connectorContext));
        repeatComponent.setCountMaxElement(getPositiveIntType(prefix + "repeat.countMax", params, connectorContext));
        repeatComponent.setDurationElement(getDecimalType(prefix + "repeat.duration", params, connectorContext));
        repeatComponent.setDurationMaxElement(getDecimalType(prefix + "repeat.durationMax", params, connectorContext));

        if ((tempParamValue = params.get(prefix + "repeat.durationUnit")) != null) {
            repeatComponent.setDurationUnit(Timing.UnitsOfTime.fromCode(tempParamValue));
        }

        repeatComponent.setFrequencyElement(getPositiveIntType(prefix + "repeat.frequency", params, connectorContext));
        repeatComponent.setFrequencyMaxElement(getPositiveIntType(prefix + "repeat.frequencyMax", params, connectorContext));
        repeatComponent.setPeriodElement(getDecimalType(prefix + "repeat.period", params, connectorContext));
        repeatComponent.setPeriodMaxElement(getDecimalType(prefix + "repeat.periodMax", params, connectorContext));

        if ((tempParamValue = params.get(prefix + "repeat.periodUnit")) != null) {
            repeatComponent.setPeriodUnit(Timing.UnitsOfTime.fromCode(tempParamValue));
        }

        if ((tempParamValue = params.get(prefix + "repeat.dayOfWeek")) != null) {
            if (tempParamValue.contains(",")) {
                String[] days = tempParamValue.split(",");
                for (String day : days) {
                    repeatComponent.addDayOfWeek(Timing.DayOfWeek.fromCode(day));
                }
            } else {
                repeatComponent.addDayOfWeek(Timing.DayOfWeek.fromCode(tempParamValue));
            }
        }

        if ((tempParamValue = params.get(prefix + "repeat.timeOfDay")) != null) {
            if (tempParamValue.contains(",")) {
                String[] times = tempParamValue.split(",");
                for (String time : times) {
                    repeatComponent.addTimeOfDay(time);
                }
            } else {
                repeatComponent.addTimeOfDay(tempParamValue);
            }
        }

        if ((tempParamValue = params.get(prefix + "repeat.when")) != null) {
            if (tempParamValue.contains(",")) {
                String[] whenArray = tempParamValue.split(",");
                for (String when : whenArray) {
                    repeatComponent.addWhen(Timing.EventTiming.fromCode(when));
                }
            } else {
                repeatComponent.addWhen(Timing.EventTiming.fromCode(tempParamValue));
            }
        }

        repeatComponent.setOffsetElement(getUnsignedIntType(prefix + "repeat.offset", params, connectorContext));
        if (!repeatComponent.isEmpty()) {
            timing.setRepeat(repeatComponent);
        }
        timing.setCode(getCodeableConcept(
                "http://hl7.org/fhir/ValueSet/timing-abbreviation", prefix + "code", params, connectorContext));

        if (timing.isEmpty()) return null;
        return timing;
    }


    public static Ratio getRatio(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Ratio ratio = new Ratio();
        populateElement(prefix, ratio, params, connectorContext);
        //TODO check for rules get validated at validation phase
        ratio.setNumerator(getQuantity(prefix + "numerator.", params, connectorContext));
        ratio.setDenominator(getQuantity(prefix + "denominator.", params, connectorContext));

        if (ratio.isEmpty()) return null;
        return ratio;
    }

    public static SampledData getSampledData(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        SampledData sampledData = new SampledData();
        populateElement(prefix, sampledData, params, connectorContext);
        sampledData.setOrigin(getSimpleQuantity(prefix + "origin.", params, connectorContext));
        sampledData.setPeriodElement(getDecimalType(prefix + "period", params, connectorContext));
        sampledData.setFactorElement(getDecimalType(prefix + "factor", params, connectorContext));
        sampledData.setLowerLimitElement(getDecimalType(prefix + "lowerLimit", params, connectorContext));
        sampledData.setUpperLimitElement(getDecimalType(prefix + "upperLimit", params, connectorContext));
        sampledData.setDimensionsElement(getPositiveIntType(prefix + "dimensions", params, connectorContext));
        sampledData.setDataElement(getStringType(prefix + "data", params, connectorContext));

        if (sampledData.isEmpty()) return null;
        return sampledData;
    }

    public static Money getMoney(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Money money = new Money();
        populateElement(prefix, money, params, connectorContext);
        money.setValueElement(getDecimalType(prefix + "value", params, connectorContext));
        money.setCurrencyElement(getCodeType(prefix + "currency", params, connectorContext));

        if (money.isEmpty()) return null;
        return money;
    }


    public static Annotation getAnnotation(String prefix, Map<String, String> params,
                                                FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Annotation annotation = new Annotation();
        populateElement(prefix, annotation, params, connectorContext);

        String[] authorTypes = {"String", "Reference"};
        annotation.setAuthor(resolveAndGetMultipleChoiceType(prefix, "author", authorTypes, params, connectorContext));

        annotation.setTimeElement(getDateTimeType(prefix + "time", params, connectorContext));
        annotation.setTextElement(getMarkdownType(prefix + "text", params, connectorContext));

        if (annotation.isEmpty()) return null;
        return annotation;
    }

    public static Signature getSignature(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Signature signature = new Signature();
        populateElement(prefix, signature, params, connectorContext);

        signature.addType(getCoding(prefix + "type.", params, connectorContext));
        signature.setWhenElement(getInstantType(prefix + "when", params, connectorContext));
        signature.setWho(getReference(prefix + "who.", params, connectorContext));
        signature.setOnBehalfOf(getReference(prefix + "onBehalfOf.", params, connectorContext));
        signature.setTargetFormatElement(getCodeType(prefix + "targetFormat", params, connectorContext));
        signature.setSigFormatElement(getCodeType(prefix + "sigFormat", params, connectorContext));
        signature.setDataElement(getBase64BinaryType(prefix + "data", params, connectorContext));

        if (signature.isEmpty()) return null;
        return signature;
    }

    public static BackboneElement getBackboneElement(String prefix, Map<String, String> params,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {

        LOG.warn("BackboneElement is not a instantiatable !");

        return null;
    }

    /*****************************************************************************************************************
     *                         Metadata types: A set of types for use with metadata resources
     *****************************************************************************************************************/


    public static ContactDetail getContactDetail(String prefix, Map<String, String> params,
                                                 FHIRConnectorContext connectorContext) throws FHIRConnectException {
        ContactDetail contactDetail = new ContactDetail();
        populateElement(prefix, contactDetail, params, connectorContext);

        contactDetail.setNameElement(getStringType(prefix + "name", params, connectorContext));
        contactDetail.addTelecom(getContactPoint(prefix + "telecom.", params, connectorContext));

        if (contactDetail.isEmpty()) return null;
        return contactDetail;
    }

    public static Contributor getContributor(String prefix, Map<String, String> params,
                                               FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Contributor contributor = new Contributor();
        populateElement(prefix, contributor, params, connectorContext);

        contributor.setType(Contributor.ContributorType.fromCode(params.get(prefix + "type")));
        contributor.setNameElement(getStringType(prefix + "name", params, connectorContext));
        contributor.addContact(getContactDetail(prefix + "contact.", params, connectorContext));

        if (contributor.isEmpty()) return null;
        return contributor;
    }


    public static DataRequirement getDataRequirement(String prefix, Map<String, String> params,
                                                     FHIRConnectorContext connectorContext) throws FHIRConnectException {
        DataRequirement dataRequirement = new DataRequirement();
        populateElement(prefix, dataRequirement, params, connectorContext);

        dataRequirement.setTypeElement(getCodeType(prefix + "type", params, connectorContext));
        if (params.get(prefix + "profile") != null) {
            dataRequirement.addProfile(params.get(prefix + "profile"));
        }

        String tempParamValue;
        /* Let's keep this logic, in case if design changed back
        if ((tempParamValue = params.get(prefix + "subjectReference")) != null) {
            DataType subRef = connectorContext.getDataObject(tempParamValue);
            if (subRef != null && subRef.unwrap() instanceof Reference) {
                dataRequirement.setSubject(subRef.unwrap());
            } else {
                throw new FHIRConnectException(
                        getDataObjectNotFoundErrorMessage(prefix + "subjectReference", tempParamValue));
            }
        } else if ((tempParamValue = params.get(prefix + "subjectCodeableConcept")) != null) {
            DataType subCodeConcept = connectorContext.getDataObject(tempParamValue);
            if (subCodeConcept != null && subCodeConcept.unwrap() instanceof CodeableConcept) {
                dataRequirement.setSubject(subCodeConcept.unwrap());
            } else {
                throw new FHIRConnectException(
                        getDataObjectNotFoundErrorMessage(prefix + "subjectCodeableConcept", tempParamValue));
            }
        } else {
            // Create Data objects
            Reference subRef = getReference(prefix + "subjectReference.", params, connectorContext);
            if (!subRef.isEmpty()) {
                dataRequirement.setSubject(subRef);
            } else {
                CodeableConcept subCode = getCodeableConcept(prefix + "subjectCodeableConcept.", params,
                                                                                                    connectorContext);
                if (!subCode.isEmpty()) {
                    dataRequirement.setSubject(subCode);
                }
            }
        }*/

        String[] subjectTypes = {"CodeableConcept", "Reference"};
        dataRequirement.setSubject(
                resolveAndGetMultipleChoiceType(prefix, "subject", subjectTypes, params, connectorContext));

        String mustSupport = params.get(prefix + "mustSupport");
        String[] supportArray;
        if (mustSupport != null && ((supportArray = mustSupport.split("|")).length > 1)) {
            Arrays.stream(supportArray).forEach(dataRequirement::addMustSupport);
        } else {
            dataRequirement.addMustSupport(mustSupport);
        }

        DataRequirement.DataRequirementCodeFilterComponent codeFilter =
                                                            new DataRequirement.DataRequirementCodeFilterComponent();
        populateElement(prefix + "codeFilter.", codeFilter, params, connectorContext);
        codeFilter.setPathElement(getStringType(prefix + "codeFilter.path", params, connectorContext));
        codeFilter.setSearchParamElement(getStringType(prefix + "codeFilter.searchParam", params, connectorContext));
        codeFilter.setValueSetElement(getCanonicalType(prefix + "codeFilter.valueSet", params, connectorContext));
        codeFilter.addCode(getCoding(prefix + "codeFilter.code.", params, connectorContext));

        if (!codeFilter.isEmpty()) {
            dataRequirement.addCodeFilter(codeFilter);
        }

        DataRequirement.DataRequirementDateFilterComponent dateFilter =
                                                            new DataRequirement.DataRequirementDateFilterComponent();
        populateElement(prefix + "dateFilter.", dateFilter, params, connectorContext);
        dateFilter.setPathElement(getStringType(prefix + "dateFilter.path", params, connectorContext));
        dateFilter.setSearchParamElement(getStringType(prefix + "dateFilter.searchParam", params, connectorContext));

        /* Let's keep this logic, in case if design changed back
        DateTimeType timeType = getDateTimeType(prefix + "dateFilter.valueDateTime", params, connectorContext);
        if (timeType != null) {
            dateFilter.setValue(timeType);
        } else if ((tempParamValue = params.get(prefix + "dateFilter.valuePeriod")) != null){
            DataType period = connectorContext.getDataObject(tempParamValue);
            if (period != null && period.unwrap() instanceof Period) {
                dateFilter.setValue(period.unwrap());
            } else {
                throw new FHIRConnectException(
                                getDataObjectNotFoundErrorMessage(prefix + "dateFilter.valuePeriod", tempParamValue));
            }
        } else if ((tempParamValue = params.get(prefix + "dateFilter.valueDuration")) != null) {
            DataType duration = connectorContext.getDataObject(tempParamValue);
            if (duration != null && duration.unwrap() instanceof Duration) {
                dateFilter.setValue(duration.unwrap());
            } else {
                throw new FHIRConnectException(
                        getDataObjectNotFoundErrorMessage(prefix + "dateFilter.valueDuration", tempParamValue));
            }
        } else {

            Period period = getPeriod(prefix + "dateFilter.valuePeriod.", params, connectorContext);
            if (!period.isEmpty()) {
                dateFilter.setValue(period);
            } else {
                Duration duration = getDuration(prefix + "dateFilter.valueDuration.", params, connectorContext);
                if (!duration.isEmpty()) {
                    dateFilter.setValue(duration);
                }
            }
        }*/

        String[] valueTypes = {"DateTime", "Period", "Duration"};
        dateFilter.setValue(
                resolveAndGetMultipleChoiceType(prefix + "dateFilter.", "value", valueTypes, params, connectorContext));

        if (!dateFilter.isEmpty()) {
            dataRequirement.addDateFilter(dateFilter);
        }

        dataRequirement.setLimitElement(getPositiveIntType(prefix + "limit", params, connectorContext));

        DataRequirement.DataRequirementSortComponent sort = new DataRequirement.DataRequirementSortComponent();
        populateElement(prefix + "sort.", sort, params, connectorContext);
        sort.setPathElement(getStringType(prefix + "sort.path", params, connectorContext));
        sort.setDirection(DataRequirement.SortDirection.fromCode(params.get(prefix + "sort.direction")));
        if (!sort.isEmpty()) {
            dataRequirement.addSort(sort);
        }

        if (dataRequirement.isEmpty()) return null;
        return dataRequirement;
    }


    public static ParameterDefinition getParameterDefinition(String prefix, Map<String, String> params,
                                                              FHIRConnectorContext connectorContext) throws FHIRConnectException {
        ParameterDefinition paramDef = new ParameterDefinition();
        populateElement(prefix, paramDef, params, connectorContext);

        paramDef.setNameElement(getCodeType(prefix + "name", params, connectorContext));
        paramDef.setUse(ParameterDefinition.ParameterUse.fromCode(params.get(prefix + "use")));
        paramDef.setMinElement(getIntegerType(prefix + "min", params, connectorContext));
        paramDef.setMaxElement(getStringType(prefix + "max", params, connectorContext));
        paramDef.setDocumentationElement(getStringType(prefix + "documentation", params, connectorContext));
        paramDef.setTypeElement(getCodeType(prefix + "type", params, connectorContext));
        paramDef.setProfileElement(getCanonicalType(prefix + "profile", params, connectorContext));

        if (paramDef.isEmpty()) return null;
        return paramDef;
    }

    public static RelatedArtifact getRelatedArtifact(String prefix, Map<String, String> params,
                                                            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        RelatedArtifact relatedArtifact = new RelatedArtifact();
        populateElement(prefix, relatedArtifact, params, connectorContext);

        relatedArtifact.setType(RelatedArtifact.RelatedArtifactType.fromCode(params.get(prefix + "type")));
        relatedArtifact.setLabelElement(getStringType(prefix + "label", params, connectorContext));
        relatedArtifact.setDisplayElement(getStringType(prefix + "display", params, connectorContext));
        relatedArtifact.setCitationElement(getMarkdownType(prefix + "citation", params, connectorContext));
        relatedArtifact.setUrlElement(getUrlType(prefix + "url", params, connectorContext));
        relatedArtifact.setDocument(getAttachment(prefix + "document.", params, connectorContext));
        relatedArtifact.setResourceElement(getCanonicalType(prefix + "resource", params, connectorContext));

        if (relatedArtifact.isEmpty()) return null;
        return relatedArtifact;
    }

    public static TriggerDefinition getTriggerDefinition(String prefix, Map<String, String> params,
                                                          FHIRConnectorContext connectorContext) throws FHIRConnectException {
        TriggerDefinition triggerDefinition = new TriggerDefinition();
        populateElement(prefix, triggerDefinition, params, connectorContext);

        triggerDefinition.setType(TriggerDefinition.TriggerType.fromCode(params.get(prefix + "type")));
        triggerDefinition.setNameElement(getStringType(prefix + "name", params, connectorContext));

        String[] timingTypes = {"Timing", "Reference", "Date", "DateTime"};
        triggerDefinition.setTiming(
                resolveAndGetMultipleChoiceType(prefix, "timing", timingTypes, params, connectorContext));

        triggerDefinition.addData(getDataRequirement(prefix + "data.", params, connectorContext));
        triggerDefinition.setCondition(getExpression(prefix + "condition.", params, connectorContext));

        if (triggerDefinition.isEmpty()) return null;
        return triggerDefinition;
    }

    public static UsageContext getUsageContext(String prefix, Map<String, String> params,
                                               FHIRConnectorContext connectorContext) throws FHIRConnectException {
        UsageContext usageContext = new UsageContext();
        populateElement(prefix, usageContext, params, connectorContext);

        usageContext.setCode(getCoding(prefix + "code.", params, connectorContext));

        String[] valueTypes = {"CodeableConcept", "Quantity", "Range", "Reference"};
        usageContext.setValue(resolveAndGetMultipleChoiceType(prefix, "value", valueTypes, params, connectorContext));

        if (usageContext.isEmpty()) return null;
        return usageContext;
    }


    public static Expression getExpression(String prefix, Map<String, String> params,
                                           FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Expression expression = new Expression();
        populateElement(prefix, expression, params, connectorContext);

        expression.setDescriptionElement(getStringType(prefix + "description", params, connectorContext));
        expression.setNameElement(getIdType(prefix + "name", params, connectorContext));
        expression.setLanguageElement(getCodeType(prefix + "language", params, connectorContext));
        expression.setReferenceElement(getUriType(prefix + "reference", params, connectorContext));

        if (expression.isEmpty()) return null;
        return expression;
    }

    /*****************************************************************************************************************
     *             Special purpose data types - defined elsewhere in the specification for specific usages
     *****************************************************************************************************************/

    public static Extension getExtension(String prefix, Map<String, String> params,
                                         FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Extension extension = new Extension();
        populateElement(prefix, extension, params, connectorContext);

        extension.setUrlElement(getUriType(prefix + "url", params, connectorContext));

        //Extension will be populated at populateElement() function
        String nestedExtension = params.get(prefix + "extension");
        if (nestedExtension == null) {
            String[] valueTypes =
                    {"String", "Id", "Integer", "UnsignedInt", "PositiveInt", "Decimal", "Instant",
                            "Code", "Boolean", "CodeableConcept", "Coding", "Date", "Count", "Reference", "HumanName",
                            "Address", "DateTime", "Markdown", "Oid", "Time", "Uri", "Url", "Uuid", "Age", "Annotation",
                            "Attachment", "ContactPoint", "Distance", "Duration", "Identifier", "Money", "Period",
                            "Quantity", "Range", "Ratio", "SampledData", "Signature", "Timing", "ContactDetail",
                            "Contributor", "DataRequirement", "Expression", "ParameterDefinition", "RelatedArtifact",
                            "TriggerDefinition", "UsageContext", "Dosage", "Meta", "Base64Binary", "Canonical"};

            extension.setValue(resolveAndGetMultipleChoiceType(prefix, "value", valueTypes, params, connectorContext));
        }

        if (extension.isEmpty()) return null;
        return extension;
    }

    public static Extension getExtensionByValueType(String dataType, String prefix, Map<String, String> params,
                                                           FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Extension extension = new Extension();
        populateElement(prefix, extension, params, connectorContext);

        extension.setUrlElement(getUriType(prefix + "url", params, connectorContext));

        TypeFactory typeFactory = typeMap.get(dataType);
        if (typeFactory != null) {
            extension.setValue(typeFactory.createObject(prefix, params, connectorContext));
        } else {
            throw new FHIRConnectException("Unknown Data Type : " + dataType);
        }
        if (extension.isEmpty()) return null;
        return extension;
    }

    public static Dosage getDosage(String prefix, Map<String, String> params,
                                   FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Dosage dosage = new Dosage();
        populateElement(prefix, dosage, params, connectorContext);

        dosage.setSequenceElement(getIntegerType(prefix + "sequence", params, connectorContext));
        dosage.setTextElement(getStringType(prefix + "text", params, connectorContext));
        dosage.addAdditionalInstruction(
                getCodeableConcept("http://hl7.org/fhir/ValueSet/additional-instruction-codes",
                        prefix + "additionalInstruction", params, connectorContext));
        dosage.setPatientInstructionElement(getStringType(prefix + "patientInstruction", params, connectorContext));
        dosage.setTiming(getTiming(prefix + "timing.", params, connectorContext));

        String[] asNeededTypes = {"Boolean", "CodeableConcept"};
        dosage.setAsNeeded(resolveAndGetMultipleChoiceType(prefix, "asNeeded", asNeededTypes, params, connectorContext));

        dosage.setSite(
                getCodeableConcept("http://hl7.org/fhir/ValueSet/approach-site-codes", prefix + "site",
                        params, connectorContext));
        dosage.setRoute(
                getCodeableConcept("http://hl7.org/fhir/ValueSet/route-codes", prefix + "route",
                        params, connectorContext));
        dosage.setMethod(
                getCodeableConcept("http://hl7.org/fhir/ValueSet/administration-method-codes", prefix + "method",
                        params, connectorContext));

        Dosage.DosageDoseAndRateComponent doseAndRateComponent = new Dosage.DosageDoseAndRateComponent();
        populateElement(prefix + "doseAndRate", doseAndRateComponent, params, connectorContext);

        doseAndRateComponent.setType(
                getCodeableConcept("http://hl7.org/fhir/ValueSet/dose-rate-type", prefix + "doseAndRate.type",
                        params, connectorContext));

        String[] supportedDoseTypes = {"Range", "Quantity"};
        doseAndRateComponent.setDose(
                resolveAndGetMultipleChoiceType(prefix + "doseAndRate", "dose", supportedDoseTypes, params, connectorContext));

        String[] supportedRateTypes = {"Ratio", "Range", "Quantity"};
        doseAndRateComponent.setRate(
                resolveAndGetMultipleChoiceType(prefix + "doseAndRate", "rate", supportedRateTypes, params, connectorContext));

        dosage.addDoseAndRate(doseAndRateComponent);

        dosage.setMaxDosePerPeriod(getRatio(prefix + "maxDosePerPeriod.", params, connectorContext));
        dosage.setMaxDosePerAdministration(getSimpleQuantity(prefix + "maxDosePerAdministration.", params, connectorContext));
        dosage.setMaxDosePerLifetime(getSimpleQuantity(prefix + "maxDosePerLifetime.", params, connectorContext));

        if (dosage.isEmpty()) return null;
        return dosage;
    }


    public static Reference getReference(String prefix, Map<String, String> params,
                                         FHIRConnectorContext connectorContext) throws FHIRConnectException{
        Reference reference = new Reference();
        populateElement(prefix, reference, params, connectorContext);

        String refValue = params.get(prefix + "reference");
        String typeValue = params.get(prefix + "type");
        if (refValue != null) {
            if (!Boolean.parseBoolean(params.get("custom")) && !(refValue.charAt(0) == '#') && connectorContext.getBaseUrl() != null) {
                //resolve the url
                try {
                    URL url = new URL(refValue);
                } catch (MalformedURLException e) {
                    //User has given relative URL
                    if (typeValue != null && !refValue.startsWith(typeValue)) refValue = typeValue + "/" + reference;
                    refValue = connectorContext.getBaseUrl() + refValue;
                }

            }
            reference.setReference(refValue);
        }

        reference.setType(typeValue);
        reference.setIdentifier(getIdentifier(prefix + "identifier.", params, connectorContext));
        reference.setDisplay(params.get(prefix + "display"));

        if (reference.isEmpty()) return null;
        return reference;
    }

    public static Meta getMeta(String prefix, Map<String, String> params,
                                               FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Meta meta = new Meta();
        populateElement(prefix, meta, params, connectorContext);

        meta.setVersionIdElement(getIdType(prefix + "versionId", params, connectorContext));
        meta.setLastUpdatedElement(getInstantType(prefix + "lastUpdated", params, connectorContext));
        meta.setSourceElement(getUriType(prefix + "source", params, connectorContext));

        String profileValue;
        if ((profileValue = params.get(prefix + "profile")) != null) {
            if (profileValue.contains(",")) {
                for (String profile : profileValue.split(",")) {
                    meta.addProfile(profile);
                }
            } else {
                meta.addProfile(profileValue);
            }
        }

        meta.addSecurity(getCoding(prefix + "security.", params, connectorContext));
        meta.addSecurity(getCoding(prefix + "tag.", params, connectorContext));

        if (meta.isEmpty()) return null;
        return meta;
    }

    public static ElementDefinition getElementDefinition(String prefix, Map<String, String> params,
                                         FHIRConnectorContext connectorContext) throws FHIRConnectException {
        //Element definition is not implemented
        LOG.error("Element Definition is not implemented within the WSO2-fhir-connector. " +
                "If you have interest please reach: https://wso2.com/support or https://github.com/wso2/product-ei");
        return null;
    }

    public static XhtmlType getXhtml(String prefix, Map<String, String> params,
                                         FHIRConnectorContext connectorContext) throws FHIRConnectException {

        XhtmlType xhtmlType = new XhtmlType();
        populateElement(prefix, xhtmlType, params, connectorContext);

        return null;
    }

    public static Narrative getNarrative(String prefix, Map<String, String> params,
                                         FHIRConnectorContext connectorContext) throws FHIRConnectException {
        Narrative narrative = new Narrative();
        populateElement(prefix, narrative, params, connectorContext);

        narrative.setStatus(Narrative.NarrativeStatus.fromCode(params.get(prefix + "status")));
        narrative.setDivAsString(getString(prefix + "div", params, connectorContext));

        if (narrative.isEmpty()) return null;
        return narrative;
    }



    public static String getFeildName(String prefix, String field) {
        if (prefix != null && !prefix.isEmpty()) {
            return prefix + field + ".";
        }
        return field;
    }

    public static boolean assertNotNullAndEmpty(String string) {
        if (string != null && !string.isEmpty()) return true;
        return false;
    }

    public static String trimFHIRPrefixPath(String path) {
        if (path.endsWith(".")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
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


    public static Type resolveAndGetMultipleChoiceType(String prefix,
                                                        String elementName,
                                                        String[] supportedTypes,
                                                        Map<String, String> params,
                                                        FHIRConnectorContext context) throws FHIRConnectException {


        //Cleanup elementName
        if (elementName.charAt(elementName.length() - 1) == ']' && elementName.endsWith("[x]")) {
            elementName = elementName.substring(0, elementName.length() - 3);
        }

        String absolutePrefix = prefix + elementName;

        // Filter related params
        ArrayList<String> relatedKeys = new ArrayList<>();
        Set<String> keys = params.keySet();
        Iterator<String> keyIter = keys.iterator();

        String foundType = null;
        String tempKey;
        String absoluteParam = "";
        while (keyIter.hasNext()) {
            tempKey = keyIter.next();
            if (tempKey.startsWith(absolutePrefix)) {

                for (String supportedType : supportedTypes) {
                    absoluteParam = absolutePrefix + supportedType;
                    if (tempKey.startsWith(absoluteParam)) {
                        // Need to check verify this is not a substring of another type.
                        // example: Code and CodeableConcept
                        if (tempKey.length() == absoluteParam.length()) {
                            // Primitive variable or complex element without child params
                            foundType = supportedType;
                            break;
                        } else if (tempKey.charAt(absoluteParam.length()) == '.') {
                            //Complex element with it's sub element parameters
                            foundType = supportedType;
                            break;
                        }
                    }
                }

                if (foundType != null) break;
            }
        }
        if (foundType == null) {
            return null;
        }

        //String absoluteParam = prefix + elementName + foundType;

        // Check for existence of data object
        String objectId = params.get(absoluteParam);
        if (objectId != null) {
            DataType dataType = context.getDataObject(objectId);
            if (dataType != null) {
                if (typeMap.get(foundType).isInstance(dataType.unwrap())) {
                    return dataType.unwrap();
                } else {
                    throw new FHIRConnectException(getDataObjectNotFoundErrorMessage(absoluteParam, objectId));
                }
            }
        }

        Type typeObject;
        TypeFactory typeFactory = typeMap.get(foundType);
        if (typeFactory.isComplexType()) {
            typeObject = typeFactory.createObject(absoluteParam + ".", params, context);
        } else {
            typeObject = typeFactory.createObject(absoluteParam, params, context);
        }

        return typeObject;
    }




    private static void populateTypeMap() {
        //Primitive Types
        typeMap.put("Boolean", new BooleanTypeFactory());
        typeMap.put("Integer", new IntegerTypeFactory());
        typeMap.put("String", new StringTypeFactory());
        typeMap.put("Decimal", new DecimalTypeFactory());
        typeMap.put("Uri", new UriTypeFactory());
        typeMap.put("Url", new UrlTypeFactory());
        typeMap.put("Canonical", new CanonicalTypeFactory());
        typeMap.put("Base64Binary", new Base64BinaryTypeFactory());
        typeMap.put("Instant", new InstantTypeFactory());
        typeMap.put("Date", new DateTypeFactory());
        typeMap.put("DateTime", new DateTimeTypeFactory());
        typeMap.put("Time", new TimeTypeFactory());
        typeMap.put("Code", new CodeTypeFactory());
        typeMap.put("Oid", new OidTypeFactory());
        typeMap.put("Id", new IdTypeFactory());
        typeMap.put("Markdown", new MarkdownTypeFactory());
        typeMap.put("UnsignedInt", new UnsignedIntTypeFactory());
        typeMap.put("PositiveInt", new PositiveIntTypeFactory());
        typeMap.put("Uuid", new UuidTypeFactory());

        // General purpose Data Types
        typeMap.put("Identifier", new IdentifierFactory());
        typeMap.put("HumanName", new HumanNameFactory());
        typeMap.put("Address", new AddressFactory());
        typeMap.put("ContactPoint", new ContactPointFactory());
        typeMap.put("Timing", new TimingFactory());
        typeMap.put("Quantity", new QuantityFactory());
        typeMap.put("SimpleQuantity", new SimpleQuantityFactory());
        typeMap.put("Attachment", new AttachmentFactory());
        typeMap.put("Range", new RangeFactory());
        typeMap.put("Period", new PeriodFactory());
        typeMap.put("Ratio", new RatioFactory());
        typeMap.put("CodeableConcept", new CodeableConceptFactory());
        typeMap.put("Coding", new CodingFactory());
        typeMap.put("SampledData", new SampledDataFactory());
        typeMap.put("Age", new AgeFactory());
        typeMap.put("Distance", new DistanceFactory());
        typeMap.put("Duration", new DurationFactory());
        typeMap.put("Count", new CountFactory());
        typeMap.put("Money", new MoneyFactory());
        typeMap.put("MoneyQuantity", new MoneyQuantityFactory());
        typeMap.put("Annotation", new AnnotationFactory());
        typeMap.put("Signature", new SignatureFactory());


        // Metadata Types
        typeMap.put("ContactDetail", new ContactDetailFactory());
        typeMap.put("Contributor", new ContributorFactory());
        typeMap.put("DataRequirement", new DataRequirementFactory());
        typeMap.put("ParameterDefinition", new ParameterDefinitionFactory());
        typeMap.put("RelatedArtifact", new RelatedArtifactFactory());
        typeMap.put("TriggerDefinition", new TriggerDefinitionFactory());
        typeMap.put("UsageContext", new UsageContextFactory());
        typeMap.put("Expression", new ExpressionFactory());



        // Special Purpose Data types
        typeMap.put("Reference", new ReferenceFactory());
        typeMap.put("Narrative", new NarrativeFactory());
        typeMap.put("Extension", new ExtensionFactory());
        typeMap.put("Meta", new MetaFactory());
        typeMap.put("Dosage", new DosageFactory());

        /*typeMap.put("ElementDefinition", ElementDefinition.class);
        typeMap.put("Xhtml", XhtmlType.class);*/

    }






}
