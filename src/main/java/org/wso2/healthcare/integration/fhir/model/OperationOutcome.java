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

// Generated on 21-Thu, 05, 2020 18:11:16+0530

package org.wso2.healthcare.integration.fhir.model;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Resource;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;

import java.util.List;
import java.util.Map;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class OperationOutcome extends DomainResource {

    private org.hl7.fhir.r4.model.OperationOutcome fhirOperationOutcome;
    private String operationPrefix;

    public OperationOutcome(String parentPrefix, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        super(parentPrefix, connectorInputParameters);
        this.fhirOperationOutcome = new org.hl7.fhir.r4.model.OperationOutcome();
        this.operationPrefix = "";
        if (parentPrefix != null) {
            this.operationPrefix = parentPrefix;
        }
        this.init(connectorInputParameters, connectorContext);
    }

    @Override
    public Resource unwrap() {
        return fhirOperationOutcome;
    }

    protected void init(Map<String, String> parameters, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        super.init(parameters, connectorContext);
        this.addIssue(parameters, connectorContext);
    }

    public Resource getBaseResource() {
        return this.fhirOperationOutcome.castToResource(fhirOperationOutcome);
    }

    public void setFhirOperationOutcome(org.hl7.fhir.r4.model.OperationOutcome fhirOperationOutcome) {
        this.fhirOperationOutcome = fhirOperationOutcome;
    }

    public org.hl7.fhir.r4.model.OperationOutcome getFhirOperationOutcome() {
        return fhirOperationOutcome;
    }

    public void addIssue(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirOperationOutcome.addIssue(getOperationOutcomeIssue(connectorInputParameters, connectorContext));
    }

    public void setIssue(List<org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent> issueList) {
        this.fhirOperationOutcome.setIssue(issueList);
    }

    public List<org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent> getIssue() {
        return this.fhirOperationOutcome.getIssue();
    }

    private org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent getOperationOutcomeIssue(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent issueComponent = new org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent();

        issueComponent.addLocation(FHIRDataTypeUtils.getString(operationPrefix + "issue.location.",
                connectorInputParameters, connectorContext));

        try {
            issueComponent.setSeverity(org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity
                    .fromCode(connectorInputParameters.get(operationPrefix + "issue.severity")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the issue.severity field";
            FHIRConnectorUtils.handleException(msg, e);
        }

        String detailsValueSet = "http://hl7.org/fhir/ValueSet/operation-outcome";
        issueComponent.setDetails(FHIRDataTypeUtils.getCodeableConcept(detailsValueSet,
                operationPrefix + "issue.details", connectorInputParameters, connectorContext));

        issueComponent.setDiagnostics(FHIRDataTypeUtils.getString(operationPrefix + "issue.diagnostics.",
                connectorInputParameters, connectorContext));

        try {
            issueComponent.setCode(org.hl7.fhir.r4.model.OperationOutcome.IssueType
                    .fromCode(connectorInputParameters.get(operationPrefix + "issue.code")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the issue.code field";
            FHIRConnectorUtils.handleException(msg, e);
        }

        issueComponent.addExpression(FHIRDataTypeUtils.getString(operationPrefix + "issue.expression.",
                connectorInputParameters, connectorContext));
        return issueComponent;
    }

}