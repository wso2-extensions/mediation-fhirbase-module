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
package org.wso2.healthcare.integration.fhir.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationOptions;
import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.hapi.ctx.DefaultProfileValidationSupport;
import org.hl7.fhir.r4.hapi.validation.CachingValidationSupport;
import org.hl7.fhir.r4.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.r4.hapi.validation.PrePopulatedValidationSupport;
import org.hl7.fhir.r4.hapi.validation.ValidationSupportChain;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;

import java.util.List;

public class FHIRPayloadValidator {

    private FhirContext ctx;
    private ValidationSupportChain supportChain;
    private CachingValidationSupport cache;
    private FhirValidator fhirValidator;

    private static final Log log = LogFactory.getLog(FHIRPayloadValidator.class);

    public FHIRPayloadValidator(FhirContext ctx) {
        this.ctx = ctx;
        supportChain = new ValidationSupportChain();
        // adding validation support for base FHIR resources
        DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport();
        supportChain.addValidationSupport(defaultSupport);
    }

    public void initValidator() {
        cache = new CachingValidationSupport(supportChain);
        FhirInstanceValidator validatorModule = new FhirInstanceValidator(cache);
        fhirValidator = ctx.newValidator();
        fhirValidator.registerValidatorModule(validatorModule);
    }

    public void initValidatorForCustomProfiles(List<StructureDefinition> definitionsList, List<ValueSet> valueSetList,
            List<CodeSystem> codeSystemList) {
        PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport();
        if (definitionsList != null) {
            for (StructureDefinition structureDefinition : definitionsList) {
                prePopulatedSupport.addStructureDefinition(structureDefinition);
            }
        }
        if (valueSetList != null) {
            for (ValueSet valueSet : valueSetList) {
                prePopulatedSupport.addValueSet(valueSet);
            }
        }
        if(codeSystemList != null) {
            for (CodeSystem codeSystem : codeSystemList) {
                prePopulatedSupport.addCodeSystem(codeSystem);
            }
        }
        supportChain.addValidationSupport(prePopulatedSupport);
        initValidator();
    }

    public ValidationResult validate(IBaseResource resource) {
        return fhirValidator.validateWithResult(resource);
    }

    public ValidationResult validate(String resourceStr) {
        return fhirValidator.validateWithResult(resourceStr);
    }

    public ValidationResult validate(IBaseResource resource, ValidationOptions options) {
        return fhirValidator.validateWithResult(resource, options);
    }

    public ValidationResult validate(String resource, ValidationOptions options) {
        return fhirValidator.validateWithResult(resource, options);
    }

    public static ValidationResult validateFHIRPayload(Resource resource, ValidationOptions options)
            throws FHIRConnectException {
        FHIRPayloadValidator payloadValidator = FHIRValidatorHolder.getInstance().getFhirPayloadValidator();
        ValidationResult result;
        if (options != null) {
            result = payloadValidator.validate(resource.unwrap(), options);
        } else {
            result = payloadValidator.validate(resource.unwrap());
        }
        return result;
    }

    public static String getOperationOutcomePayload(IBaseResource validationOperationOutcome, String contentType) {
        String validationOutcome = null;
        if (validationOperationOutcome != null) {
            if (FHIRConstants.JSON_CONTENT_TYPE.equals(contentType)) {
                validationOutcome = FHIRConnectorUtils.serializeToJSON(validationOperationOutcome);
            } else {
                validationOutcome = FHIRConnectorUtils.serializeToXML(validationOperationOutcome);
            }
        }
        return validationOutcome;
    }
}
