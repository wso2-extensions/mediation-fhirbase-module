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

// Generated on 18-Sun, 10, 2020 19:48:11+0530

package org.wso2.healthcare.integration.fhir.model;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.UnsignedIntType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.UsageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class CodeSystem extends DomainResource {

    private org.hl7.fhir.r4.model.CodeSystem fhirCodeSystem;
    private String operationPrefix;

    public CodeSystem(String parentPrefix, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        super(parentPrefix, connectorInputParameters);
        this.fhirCodeSystem = new org.hl7.fhir.r4.model.CodeSystem();
        this.operationPrefix = "";
        if (parentPrefix != null) {
            this.operationPrefix = parentPrefix;
        }
        this.init(connectorInputParameters, connectorContext);
    }

    @Override
    public Resource unwrap() {
        return fhirCodeSystem;
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
        this.setPurpose(parameters, connectorContext);
        this.setCopyright(parameters, connectorContext);
        this.setCaseSensitive(parameters, connectorContext);
        this.setValueSet(parameters, connectorContext);
        this.setHierarchyMeaning(parameters, connectorContext);
        this.setCompositional(parameters, connectorContext);
        this.setVersionNeeded(parameters, connectorContext);
        this.setContent(parameters, connectorContext);
        this.setSupplements(parameters, connectorContext);
        this.setCount(parameters, connectorContext);
        this.addFilter(parameters, connectorContext);
        this.addProperty(parameters, connectorContext);
        this.addConcept(parameters, connectorContext);
    }

    public Resource getBaseResource() {
        return this.fhirCodeSystem.castToResource(fhirCodeSystem);
    }

    public void setFhirCodeSystem(org.hl7.fhir.r4.model.CodeSystem fhirCodeSystem) {
        this.fhirCodeSystem = fhirCodeSystem;
    }

    public org.hl7.fhir.r4.model.CodeSystem getFhirCodeSystem() {
        return fhirCodeSystem;
    }

    public void setUrl(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem
                .setUrl(FHIRDataTypeUtils.getUri(operationPrefix + "url", connectorInputParameters, connectorContext));
    }

    public UriType getUrl() {
        return this.fhirCodeSystem.getUrlElement();
    }

    public void addIdentifier(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.addIdentifier(FHIRDataTypeUtils.getIdentifier(operationPrefix + "identifier.",
                connectorInputParameters, connectorContext));
    }

    public void setIdentifier(List<Identifier> identifierList) {
        this.fhirCodeSystem.setIdentifier(identifierList);
    }

    public List<Identifier> getIdentifier() {
        return this.fhirCodeSystem.getIdentifier();
    }

    public void setVersion(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setVersion(
                FHIRDataTypeUtils.getString(operationPrefix + "version", connectorInputParameters, connectorContext));
    }

    public String getVersion() {
        return this.fhirCodeSystem.getVersion();
    }

    public void setName(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setName(
                FHIRDataTypeUtils.getString(operationPrefix + "name", connectorInputParameters, connectorContext));
    }

    public String getName() {
        return this.fhirCodeSystem.getName();
    }

    public void setTitle(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setTitle(
                FHIRDataTypeUtils.getString(operationPrefix + "title", connectorInputParameters, connectorContext));
    }

    public String getTitle() {
        return this.fhirCodeSystem.getTitle();
    }

    public void setStatus(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        try {
            fhirCodeSystem.setStatus(
                    Enumerations.PublicationStatus.fromCode(connectorInputParameters.get(operationPrefix + "status")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the status field";
            FHIRConnectorUtils.handleException(msg, e);
        }
    }

    public Enumerations.PublicationStatus getStatus() {
        return this.fhirCodeSystem.getStatus();
    }

    public void setExperimental(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setExperimental(FHIRDataTypeUtils.getBoolean(operationPrefix + "experimental",
                connectorInputParameters, connectorContext));
    }

    public boolean getExperimental() {
        return this.fhirCodeSystem.getExperimental();
    }

    public void setDate(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setDate(
                FHIRDataTypeUtils.getDateTime(operationPrefix + "date", connectorInputParameters, connectorContext));
    }

    public Date getDate() {
        return this.fhirCodeSystem.getDate();
    }

    public void setPublisher(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setPublisher(
                FHIRDataTypeUtils.getString(operationPrefix + "publisher", connectorInputParameters, connectorContext));
    }

    public String getPublisher() {
        return this.fhirCodeSystem.getPublisher();
    }

    public void addContact(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.addContact(FHIRDataTypeUtils.getContactDetail(operationPrefix + "contact.",
                connectorInputParameters, connectorContext));
    }

    public void setContact(List<ContactDetail> contactList) {
        this.fhirCodeSystem.setContact(contactList);
    }

    public List<ContactDetail> getContact() {
        return this.fhirCodeSystem.getContact();
    }

    public void setDescription(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setDescription(FHIRDataTypeUtils.getMarkdown(operationPrefix + "description",
                connectorInputParameters, connectorContext));
    }

    public String getDescription() {
        return this.fhirCodeSystem.getDescription();
    }

    public void addUseContext(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.addUseContext(FHIRDataTypeUtils.getUsageContext(operationPrefix + "useContext.",
                connectorInputParameters, connectorContext));
    }

    public void setUseContext(List<UsageContext> useContextList) {
        this.fhirCodeSystem.setUseContext(useContextList);
    }

    public List<UsageContext> getUseContext() {
        return this.fhirCodeSystem.getUseContext();
    }

    public void addJurisdiction(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        String jurisdictionValueSet = "http://hl7.org/fhir/ValueSet/jurisdiction";
        fhirCodeSystem.addJurisdiction(FHIRDataTypeUtils.getCodeableConcept(jurisdictionValueSet,
                operationPrefix + "jurisdiction", connectorInputParameters, connectorContext));
    }

    public void setJurisdiction(List<CodeableConcept> jurisdictionList) {
        this.fhirCodeSystem.setJurisdiction(jurisdictionList);
    }

    public List<CodeableConcept> getJurisdiction() {
        return this.fhirCodeSystem.getJurisdiction();
    }

    public void setPurpose(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setPurpose(
                FHIRDataTypeUtils.getMarkdown(operationPrefix + "purpose", connectorInputParameters, connectorContext));
    }

    public String getPurpose() {
        return this.fhirCodeSystem.getPurpose();
    }

    public void setCopyright(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setCopyright(FHIRDataTypeUtils.getMarkdown(operationPrefix + "copyright",
                connectorInputParameters, connectorContext));
    }

    public String getCopyright() {
        return this.fhirCodeSystem.getCopyright();
    }

    public void setCaseSensitive(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setCaseSensitive(FHIRDataTypeUtils.getBoolean(operationPrefix + "caseSensitive",
                connectorInputParameters, connectorContext));
    }

    public boolean getCaseSensitive() {
        return this.fhirCodeSystem.getCaseSensitive();
    }

    public void setValueSet(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setValueSet(FHIRDataTypeUtils.getCanonical(operationPrefix + "valueSet",
                connectorInputParameters, connectorContext));
    }

    public CanonicalType getValueSet() {
        return this.fhirCodeSystem.getValueSetElement();
    }

    public void setHierarchyMeaning(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        try {
            fhirCodeSystem.setHierarchyMeaning(org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning
                    .fromCode(connectorInputParameters.get(operationPrefix + "hierarchyMeaning")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the hierarchyMeaning field";
            FHIRConnectorUtils.handleException(msg, e);
        }
    }

    public org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning getHierarchyMeaning() {
        return this.fhirCodeSystem.getHierarchyMeaning();
    }

    public void setCompositional(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setCompositional(FHIRDataTypeUtils.getBoolean(operationPrefix + "compositional",
                connectorInputParameters, connectorContext));
    }

    public boolean getCompositional() {
        return this.fhirCodeSystem.getCompositional();
    }

    public void setVersionNeeded(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setVersionNeeded(FHIRDataTypeUtils.getBoolean(operationPrefix + "versionNeeded",
                connectorInputParameters, connectorContext));
    }

    public boolean getVersionNeeded() {
        return this.fhirCodeSystem.getVersionNeeded();
    }

    public void setContent(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        try {
            fhirCodeSystem.setContent(org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode
                    .fromCode(connectorInputParameters.get(operationPrefix + "content")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the content field";
            FHIRConnectorUtils.handleException(msg, e);
        }
    }

    public org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode getContent() {
        return this.fhirCodeSystem.getContent();
    }

    public void setSupplements(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirCodeSystem.setSupplements(FHIRDataTypeUtils.getCanonical(operationPrefix + "supplements",
                connectorInputParameters, connectorContext));
    }

    public CanonicalType getSupplements() {
        return this.fhirCodeSystem.getSupplementsElement();
    }

    public void setCount(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        Integer count = FHIRDataTypeUtils.getUnsignedInt(operationPrefix + "count", connectorInputParameters,
                connectorContext);
        if (count != null) {
            this.fhirCodeSystem.setCount(count);
        }
    }

    public UnsignedIntType getCount() {
        return this.fhirCodeSystem.getCountElement();
    }

    public void addFilter(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirCodeSystem.addFilter(getCodeSystemFilter(connectorInputParameters, connectorContext));
    }

    public void setFilter(List<org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent> filterList) {
        this.fhirCodeSystem.setFilter(filterList);
    }

    public List<org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent> getFilter() {
        return this.fhirCodeSystem.getFilter();
    }

    private org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent getCodeSystemFilter(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent filterComponent = new org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent();

        filterComponent.setCode(FHIRDataTypeUtils.getString(operationPrefix + "filter.code", connectorInputParameters,
                connectorContext));

        filterComponent.setDescription(FHIRDataTypeUtils.getString(operationPrefix + "filter.description.",
                connectorInputParameters, connectorContext));

        try {
            filterComponent.addOperator(org.hl7.fhir.r4.model.CodeSystem.FilterOperator
                    .fromCode(connectorInputParameters.get(operationPrefix + "filter.operator")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the filter.operator field";
            FHIRConnectorUtils.handleException(msg, e);
        }

        filterComponent.setValue(FHIRDataTypeUtils.getString(operationPrefix + "filter.value.",
                connectorInputParameters, connectorContext));
        return (filterComponent.isEmpty() ? null : filterComponent);
    }

    public void addProperty(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirCodeSystem.addProperty(getCodeSystemProperty(connectorInputParameters, connectorContext));
    }

    public void setProperty(List<org.hl7.fhir.r4.model.CodeSystem.PropertyComponent> propertyList) {
        this.fhirCodeSystem.setProperty(propertyList);
    }

    public List<org.hl7.fhir.r4.model.CodeSystem.PropertyComponent> getProperty() {
        return this.fhirCodeSystem.getProperty();
    }

    private org.hl7.fhir.r4.model.CodeSystem.PropertyComponent getCodeSystemProperty(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.CodeSystem.PropertyComponent propertyComponent = new org.hl7.fhir.r4.model.CodeSystem.PropertyComponent();

        propertyComponent.setCode(FHIRDataTypeUtils.getString(operationPrefix + "property.code",
                connectorInputParameters, connectorContext));

        propertyComponent.setUri(FHIRDataTypeUtils.getUri(operationPrefix + "property.uri.", connectorInputParameters,
                connectorContext));

        propertyComponent.setDescription(FHIRDataTypeUtils.getString(operationPrefix + "property.description.",
                connectorInputParameters, connectorContext));

        try {
            propertyComponent.setType(org.hl7.fhir.r4.model.CodeSystem.PropertyType
                    .fromCode(connectorInputParameters.get(operationPrefix + "property.type")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the property.type field";
            FHIRConnectorUtils.handleException(msg, e);
        }
        return (propertyComponent.isEmpty() ? null : propertyComponent);
    }

    private org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent getCodeSystemConceptDesignation(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent conceptDesignationComponent = new org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent();

        conceptDesignationComponent.setLanguage(FHIRDataTypeUtils.getString(
                operationPrefix + "concept.designation.language", connectorInputParameters, connectorContext));

        conceptDesignationComponent.setUse(FHIRDataTypeUtils.getCoding(operationPrefix + "concept.designation.use.",
                connectorInputParameters, connectorContext));

        conceptDesignationComponent.setValue(FHIRDataTypeUtils.getString(operationPrefix + "concept.designation.value.",
                connectorInputParameters, connectorContext));
        return (conceptDesignationComponent.isEmpty() ? null : conceptDesignationComponent);
    }

    private org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent getCodeSystemConceptProperty(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent conceptPropertyComponent = new org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent();

        conceptPropertyComponent.setCode(FHIRDataTypeUtils.getString(operationPrefix + "concept.property.code",
                connectorInputParameters, connectorContext));

        String[] supportedTypesValue = { "Code", "Coding", "String", "Integer", "Boolean", "DateTime", "Decimal" };
        conceptPropertyComponent.setValue(FHIRDataTypeUtils.resolveAndGetMultipleChoiceType(operationPrefix,
                "concept.property.value", supportedTypesValue, connectorInputParameters, connectorContext));

        return (conceptPropertyComponent.isEmpty() ? null : conceptPropertyComponent);
    }

    public void addConcept(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirCodeSystem.addConcept(getCodeSystemConcept(connectorInputParameters, connectorContext));
    }

    public void setConcept(List<org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent> conceptList) {
        this.fhirCodeSystem.setConcept(conceptList);
    }

    public List<org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent> getConcept() {
        return this.fhirCodeSystem.getConcept();
    }

    private org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent getCodeSystemConcept(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent conceptComponent = new org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent();
        String backboneElementPrefix = "concept.";
        if (connectorInputParameters.containsKey("contentReferenceAttrPrefix")) {
            backboneElementPrefix = connectorInputParameters.get("contentReferenceAttrPrefix");
        }
        String operationPrefix = this.operationPrefix + backboneElementPrefix;

        conceptComponent.setCode(
                FHIRDataTypeUtils.getString(operationPrefix + "code", connectorInputParameters, connectorContext));

        conceptComponent.setDisplay(
                FHIRDataTypeUtils.getString(operationPrefix + "display.", connectorInputParameters, connectorContext));

        conceptComponent.setDefinition(FHIRDataTypeUtils.getString(operationPrefix + "definition.",
                connectorInputParameters, connectorContext));

        conceptComponent.addDesignation(getCodeSystemConceptDesignation(connectorInputParameters, connectorContext));

//        conceptComponent.addProperty(getCodeSystemProperty(connectorInputParameters, connectorContext));

        connectorInputParameters.put("contentReferenceAttrPrefix", "concept.concept.");
        conceptComponent.addConcept(getCodeSystemConcept(connectorInputParameters, connectorContext));
        return (conceptComponent.isEmpty() ? null : conceptComponent);
    }

}