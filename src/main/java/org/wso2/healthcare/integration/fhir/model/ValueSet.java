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

// Generated on 18-Sun, 10, 2020 19:47:57+0530

package org.wso2.healthcare.integration.fhir.model;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.UsageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class ValueSet extends DomainResource {

    private org.hl7.fhir.r4.model.ValueSet fhirValueSet;
    private String operationPrefix;

    public ValueSet(String parentPrefix, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        super(parentPrefix, connectorInputParameters);
        this.fhirValueSet = new org.hl7.fhir.r4.model.ValueSet();
        this.operationPrefix = "";
        if (parentPrefix != null) {
            this.operationPrefix = parentPrefix;
        }
        this.init(connectorInputParameters, connectorContext);
    }

    @Override
    public Resource unwrap() {
        return fhirValueSet;
    }

    protected void init(Map<String, String> parameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        super.init(parameters, connectorContext);
        this.setUrl(parameters, connectorContext);
        this.addIdentifier(parameters, connectorContext);
        this.setVersion(parameters, connectorContext);
        this.setName(parameters, connectorContext);
        this.setTitle(parameters, connectorContext);
        this.setStatus(parameters, connectorContext);
        this.setExperimental(parameters, connectorContext);
        this.setDate(parameters, connectorContext);
        this.setPublisher(parameters, connectorContext);
        this.addContact(parameters, connectorContext);
        this.setDescription(parameters, connectorContext);
        this.addUseContext(parameters, connectorContext);
        this.addJurisdiction(parameters, connectorContext);
        this.setImmutable(parameters, connectorContext);
        this.setPurpose(parameters, connectorContext);
        this.setCopyright(parameters, connectorContext);
        this.setCompose(parameters, connectorContext);
        this.setExpansion(parameters, connectorContext);
    }

    public Resource getBaseResource() {
        return this.fhirValueSet.castToResource(fhirValueSet);
    }

    public void setFhirValueSet(org.hl7.fhir.r4.model.ValueSet fhirValueSet) {
        this.fhirValueSet = fhirValueSet;
    }

    public org.hl7.fhir.r4.model.ValueSet getFhirValueSet() {
        return fhirValueSet;
    }

    public void setUrl(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet
                .setUrl(FHIRDataTypeUtils.getUri(operationPrefix + "url", connectorInputParameters, connectorContext));
    }

    public UriType getUrl() {
        return this.fhirValueSet.getUrlElement();
    }

    public void addIdentifier(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.addIdentifier(FHIRDataTypeUtils.getIdentifier(operationPrefix + "identifier.",
                connectorInputParameters, connectorContext));
    }

    public void setIdentifier(List<Identifier> identifierList) {
        this.fhirValueSet.setIdentifier(identifierList);
    }

    public List<Identifier> getIdentifier() {
        return this.fhirValueSet.getIdentifier();
    }

    public void setVersion(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setVersion(
                FHIRDataTypeUtils.getString(operationPrefix + "version", connectorInputParameters, connectorContext));
    }

    public String getVersion() {
        return this.fhirValueSet.getVersion();
    }

    public void setName(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setName(
                FHIRDataTypeUtils.getString(operationPrefix + "name", connectorInputParameters, connectorContext));
    }

    public String getName() {
        return this.fhirValueSet.getName();
    }

    public void setTitle(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setTitle(
                FHIRDataTypeUtils.getString(operationPrefix + "title", connectorInputParameters, connectorContext));
    }

    public String getTitle() {
        return this.fhirValueSet.getTitle();
    }

    public void setStatus(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        try {
            fhirValueSet.setStatus(
                    Enumerations.PublicationStatus.fromCode(connectorInputParameters.get(operationPrefix + "status")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the status field";
            handleException(msg, e);
        }
    }

    public Enumerations.PublicationStatus getStatus() {
        return this.fhirValueSet.getStatus();
    }

    public void setExperimental(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setExperimental(FHIRDataTypeUtils.getBoolean(operationPrefix + "experimental",
                connectorInputParameters, connectorContext));
    }

    public boolean getExperimental() {
        return this.fhirValueSet.getExperimental();
    }

    public void setDate(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setDate(
                FHIRDataTypeUtils.getDateTime(operationPrefix + "date", connectorInputParameters, connectorContext));
    }

    public Date getDate() {
        return this.fhirValueSet.getDate();
    }

    public void setPublisher(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setPublisher(
                FHIRDataTypeUtils.getString(operationPrefix + "publisher", connectorInputParameters, connectorContext));
    }

    public String getPublisher() {
        return this.fhirValueSet.getPublisher();
    }

    public void addContact(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.addContact(FHIRDataTypeUtils.getContactDetail(operationPrefix + "contact.",
                connectorInputParameters, connectorContext));
    }

    public void setContact(List<ContactDetail> contactList) {
        this.fhirValueSet.setContact(contactList);
    }

    public List<ContactDetail> getContact() {
        return this.fhirValueSet.getContact();
    }

    public void setDescription(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setDescription(FHIRDataTypeUtils.getMarkdown(operationPrefix + "description",
                connectorInputParameters, connectorContext));
    }

    public String getDescription() {
        return this.fhirValueSet.getDescription();
    }

    public void addUseContext(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.addUseContext(FHIRDataTypeUtils.getUsageContext(operationPrefix + "useContext.",
                connectorInputParameters, connectorContext));
    }

    public void setUseContext(List<UsageContext> useContextList) {
        this.fhirValueSet.setUseContext(useContextList);
    }

    public List<UsageContext> getUseContext() {
        return this.fhirValueSet.getUseContext();
    }

    public void addJurisdiction(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        String jurisdictionValueSet = "http://hl7.org/fhir/ValueSet/jurisdiction";
        fhirValueSet.addJurisdiction(FHIRDataTypeUtils.getCodeableConcept(jurisdictionValueSet,
                operationPrefix + "jurisdiction", connectorInputParameters, connectorContext));
    }

    public void setJurisdiction(List<CodeableConcept> jurisdictionList) {
        this.fhirValueSet.setJurisdiction(jurisdictionList);
    }

    public List<CodeableConcept> getJurisdiction() {
        return this.fhirValueSet.getJurisdiction();
    }

    public void setImmutable(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setImmutable(FHIRDataTypeUtils.getBoolean(operationPrefix + "immutable",
                connectorInputParameters, connectorContext));
    }

    public boolean getImmutable() {
        return this.fhirValueSet.getImmutable();
    }

    public void setPurpose(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setPurpose(
                FHIRDataTypeUtils.getMarkdown(operationPrefix + "purpose", connectorInputParameters, connectorContext));
    }

    public String getPurpose() {
        return this.fhirValueSet.getPurpose();
    }

    public void setCopyright(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirValueSet.setCopyright(FHIRDataTypeUtils.getMarkdown(operationPrefix + "copyright",
                connectorInputParameters, connectorContext));
    }

    public String getCopyright() {
        return this.fhirValueSet.getCopyright();
    }

    private org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent getValueSetComposeIncludeConceptDesignation(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent composeIncludeConceptDesignationComponent = new org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent();
        String backboneElementPrefix = "compose.include.concept.designation.";
        if (connectorInputParameters.containsKey("contentReferenceAttrPrefix")) {
            backboneElementPrefix = connectorInputParameters.get("contentReferenceAttrPrefix");
        }
        String operationPrefix = this.operationPrefix + backboneElementPrefix;

        composeIncludeConceptDesignationComponent.setLanguage(
                FHIRDataTypeUtils.getString(operationPrefix + "language", connectorInputParameters, connectorContext));

        composeIncludeConceptDesignationComponent.setUse(
                FHIRDataTypeUtils.getCoding(operationPrefix + "use.", connectorInputParameters, connectorContext));

        composeIncludeConceptDesignationComponent.setValue(
                FHIRDataTypeUtils.getString(operationPrefix + "value.", connectorInputParameters, connectorContext));
        return (composeIncludeConceptDesignationComponent.isEmpty() ? null : composeIncludeConceptDesignationComponent);
    }

    private org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent getValueSetComposeIncludeConcept(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent composeIncludeConceptComponent = new org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent();

        composeIncludeConceptComponent.setCode(FHIRDataTypeUtils.getString(
                operationPrefix + "compose.include.concept.code", connectorInputParameters, connectorContext));

        composeIncludeConceptComponent.setDisplay(FHIRDataTypeUtils.getString(
                operationPrefix + "compose.include.concept.display.", connectorInputParameters, connectorContext));

        composeIncludeConceptComponent.addDesignation(
                getValueSetComposeIncludeConceptDesignation(connectorInputParameters, connectorContext));
        return (composeIncludeConceptComponent.isEmpty() ? null : composeIncludeConceptComponent);
    }

    private org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent getValueSetComposeIncludeFilter(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent composeIncludeFilterComponent = new org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent();

        composeIncludeFilterComponent.setProperty(FHIRDataTypeUtils.getString(
                operationPrefix + "compose.include.filter.property", connectorInputParameters, connectorContext));

        try {
            composeIncludeFilterComponent.setOp(org.hl7.fhir.r4.model.ValueSet.FilterOperator
                    .fromCode(connectorInputParameters.get(operationPrefix + "compose.include.filter.op")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the compose.include.filter.op field";
            handleException(msg, e);
        }

        composeIncludeFilterComponent.setValue(FHIRDataTypeUtils.getString(
                operationPrefix + "compose.include.filter.value.", connectorInputParameters, connectorContext));
        return (composeIncludeFilterComponent.isEmpty() ? null : composeIncludeFilterComponent);
    }

    private org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent getValueSetComposeInclude(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent composeIncludeComponent = new org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent();
        String backboneElementPrefix = "compose.include.";
        if (connectorInputParameters.containsKey("contentReferenceAttrPrefix")) {
            backboneElementPrefix = connectorInputParameters.get("contentReferenceAttrPrefix");
        }
        String operationPrefix = this.operationPrefix + backboneElementPrefix;

        composeIncludeComponent.setSystem(
                FHIRDataTypeUtils.getUri(operationPrefix + "system.", connectorInputParameters, connectorContext));

        composeIncludeComponent.setVersion(
                FHIRDataTypeUtils.getString(operationPrefix + "version.", connectorInputParameters, connectorContext));

        composeIncludeComponent.addConcept(getValueSetComposeIncludeConcept(connectorInputParameters, connectorContext));

        composeIncludeComponent.addFilter(getValueSetComposeIncludeFilter(connectorInputParameters, connectorContext));

        composeIncludeComponent.addValueSet(FHIRDataTypeUtils.getCanonical(operationPrefix + "valueSet.",
                connectorInputParameters, connectorContext));
        return (composeIncludeComponent.isEmpty() ? null : composeIncludeComponent);
    }

    public void setCompose(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirValueSet.setCompose(getValueSetCompose(connectorInputParameters, connectorContext));
    }

    public org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent getCompose() {
        return this.fhirValueSet.getCompose();
    }

    private org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent getValueSetCompose(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent composeComponent = new org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent();

        composeComponent.setLockedDate(FHIRDataTypeUtils.getDate(operationPrefix + "compose.lockedDate.",
                connectorInputParameters, connectorContext));

        composeComponent.setInactiveElement(FHIRDataTypeUtils.getBooleanType(operationPrefix + "compose.inactive.",
                connectorInputParameters, connectorContext));

        composeComponent.addInclude(getValueSetComposeInclude(connectorInputParameters, connectorContext));

        connectorInputParameters.put("contentReferenceAttrPrefix", "compose.exclude.");
        composeComponent.addExclude(getValueSetComposeInclude(connectorInputParameters, connectorContext));
        return (composeComponent.isEmpty() ? null : composeComponent);
    }

    private org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent getValueSetExpansionParameter(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent expansionParameterComponent = new org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent();

        expansionParameterComponent.setName(FHIRDataTypeUtils.getString(operationPrefix + "expansion.parameter.name.",
                connectorInputParameters, connectorContext));

        String[] supportedTypesValue = { "String", "Boolean", "Integer", "Decimal", "Uri", "Code", "DateTime" };
        expansionParameterComponent.setValue(FHIRDataTypeUtils.resolveAndGetMultipleChoiceType(operationPrefix,
                "expansion.parameter.value", supportedTypesValue, connectorInputParameters, connectorContext));

        return (expansionParameterComponent.isEmpty() ? null : expansionParameterComponent);
    }

    private org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent getValueSetExpansionContains(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent expansionContainsComponent = new org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent();
        String backboneElementPrefix = "expansion.contains.";
        if (connectorInputParameters.containsKey("contentReferenceAttrPrefix")) {
            backboneElementPrefix = connectorInputParameters.get("contentReferenceAttrPrefix");
        }
        String operationPrefix = this.operationPrefix + backboneElementPrefix;

        expansionContainsComponent.setSystem(
                FHIRDataTypeUtils.getUri(operationPrefix + "system.", connectorInputParameters, connectorContext));

        expansionContainsComponent.setAbstractElement(FHIRDataTypeUtils.getBooleanType(operationPrefix + "abstract.",
                connectorInputParameters, connectorContext));

        expansionContainsComponent.setInactiveElement(FHIRDataTypeUtils.getBooleanType(operationPrefix + "inactive.",
                connectorInputParameters, connectorContext));

        expansionContainsComponent.setVersion(
                FHIRDataTypeUtils.getString(operationPrefix + "version.", connectorInputParameters, connectorContext));

        expansionContainsComponent.setCode(
                FHIRDataTypeUtils.getString(operationPrefix + "code", connectorInputParameters, connectorContext));

        expansionContainsComponent.setDisplay(
                FHIRDataTypeUtils.getString(operationPrefix + "display.", connectorInputParameters, connectorContext));

        connectorInputParameters.put("contentReferenceAttrPrefix", "expansion.contains.designation.");
        expansionContainsComponent.addDesignation(
                getValueSetComposeIncludeConceptDesignation(connectorInputParameters, connectorContext));

        connectorInputParameters.put("contentReferenceAttrPrefix", "expansion.contains.contains.");
        expansionContainsComponent
                .addContains(getValueSetExpansionContains(connectorInputParameters, connectorContext));
        return (expansionContainsComponent.isEmpty() ? null : expansionContainsComponent);
    }

    public void setExpansion(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirValueSet.setExpansion(getValueSetExpansion(connectorInputParameters, connectorContext));
    }

    public org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent getExpansion() {
        return this.fhirValueSet.getExpansion();
    }

    private org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent getValueSetExpansion(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent expansionComponent = new org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent();

        expansionComponent.setIdentifier(FHIRDataTypeUtils.getUri(operationPrefix + "expansion.identifier.",
                connectorInputParameters, connectorContext));

        expansionComponent.setTimestamp(FHIRDataTypeUtils.getDateTime(operationPrefix + "expansion.timestamp.",
                connectorInputParameters, connectorContext));

        IntegerType total = FHIRDataTypeUtils.getIntegerType(operationPrefix + "expansion.total.", connectorInputParameters,
                connectorContext);
        if (total != null) {
            expansionComponent.setTotalElement(total);
        }

        IntegerType offset = FHIRDataTypeUtils.getIntegerType(operationPrefix + "expansion.offset.",
                connectorInputParameters, connectorContext);
        if (offset != null) {
            expansionComponent.setOffsetElement(offset);
        }

        expansionComponent.addParameter(getValueSetExpansionParameter(connectorInputParameters, connectorContext));

        expansionComponent.addContains(getValueSetExpansionContains(connectorInputParameters, connectorContext));
        return (expansionComponent.isEmpty() ? null : expansionComponent);
    }

}