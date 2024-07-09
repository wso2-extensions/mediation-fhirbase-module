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
package org.wso2.healthcare.integration.fhir.codesystems;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ValueSet;
import org.wso2.healthcare.integration.fhir.config.TerminologyHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeSystemEnumGenFactory {

    private static Log LOG = LogFactory.getLog(CodeSystemEnumGenFactory.class);
    private TerminologyHolder terminologyHolder;

    public CodeSystemEnumGenFactory() {
        this.terminologyHolder = TerminologyHolder.getInstance();
    }

    public Map<String,Coding> fetchCodeSystem(String uri) {
        ValueSet valueSet = terminologyHolder.getValueSets().get(uri);
        ValueSet.ValueSetComposeComponent compose = valueSet.getCompose();
        List<ValueSet.ConceptSetComponent> include = compose.getInclude();
        Map<String,Coding> codings = new HashMap<>();
        for (ValueSet.ConceptSetComponent conceptSetComponent : include) {
            String system = conceptSetComponent.getSystem();
            List<ValueSet.ConceptReferenceComponent> concepts = conceptSetComponent.getConcept();
            List<String> includedCodes = new ArrayList<>();
            for (ValueSet.ConceptReferenceComponent concept : concepts) {
                if (concept.getDisplay() != null) {
                    Coding coding = new Coding();
                    coding.setCode(concept.getCode());
                    coding.setDisplay(concept.getDisplay());
                    coding.setSystem(system);
                    codings.put(concept.getCode(), coding);
                } else {
                    includedCodes.add(concept.getCode());
                }
            }

            CodeSystem codeSystem = terminologyHolder.getCodeSystems().get(system);
            if (codeSystem != null) {
                List<CodeSystem.ConceptDefinitionComponent> concept = codeSystem.getConcept();
                for (CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent : concept) {
                    List<CodeSystem.ConceptPropertyComponent> property = conceptDefinitionComponent.getProperty();
                    // check if the code is deprecated
                    boolean isDeprecated = false;
                    for (CodeSystem.ConceptPropertyComponent conceptPropertyComponent : property) {
                        if ("status".equals(conceptPropertyComponent.getCode()) && "deprecated"
                                .equals(conceptPropertyComponent.getValue().toString())) {
                            isDeprecated = true;
                            break;
                        }
                    }
                    if (!isDeprecated) {
                        if (concepts.size() > 0 && !includedCodes.contains(conceptDefinitionComponent.getCode())) {
                            continue;
                        }
                        Coding coding = new Coding();
                        coding.setCode(conceptDefinitionComponent.getCode());
                        coding.setDisplay(conceptDefinitionComponent.getDisplay());
                        coding.setSystem(system);
                        codings.put(conceptDefinitionComponent.getCode(), coding);
                    }
                }
            }
        }
        return codings;
    }

    /**
     * Function to create coding data object from given value set url and code
     *
     * @param valueSetUrl
     * @param code
     * @return
     */
    public Coding createCoding(String valueSetUrl, String code) {

        ValueSet valueSet = terminologyHolder.getValueSets().get(valueSetUrl);
        Coding codingObj = null;
        if (valueSet != null) {
            List<ValueSet.ConceptSetComponent> includeList = valueSet.getCompose().getInclude();
            for (ValueSet.ConceptSetComponent include : includeList) {
                if (include.getSystem() != null) {
                    if (include.getConcept() != null && include.getConcept().size() > 0) {
                        // ValueSet contains inline concepts
                        ValueSet.ConceptReferenceComponent concept = null;
                        for (ValueSet.ConceptReferenceComponent conceptObj : include.getConcept()) {
                            if (code.equals(conceptObj.getCode())) {
                                concept = conceptObj;
                                break;
                            }
                        }
                        if (concept != null) {
                            return getCodingFromValueSetConcept(include.getSystem(), concept);
                        }
                    }
                    codingObj = getCodingFromCodeSystem(include.getSystem(), code);
                } else if (include.getValueSet() != null) {
                    for (CanonicalType innerValueSet : include.getValueSet()) {
                        codingObj = createCoding(innerValueSet.asStringValue(), code);
                        if (codingObj != null) break;
                    }
                }
                if (codingObj != null) break;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to find Value Set : " + valueSetUrl + ". Hence checking in CodeSystems");
            }
            codingObj = getCodingFromCodeSystem(valueSetUrl, code);
        }
        if (codingObj == null) {
            LOG.warn("Coding not found : valueSet/CodeSystem - " + valueSetUrl + " for code -" + code);
        }
        return codingObj;
    }

    /**
     * Function to get Coding from a code system
     *
     * @param systemUrl
     * @param code
     * @return
     */
    public Coding getCodingFromCodeSystem (String systemUrl, String code) {
        CodeSystem codeSystem = terminologyHolder.getCodeSystems().get(systemUrl);
        if (codeSystem != null) {
            Coding coding = null;
            for(CodeSystem.ConceptDefinitionComponent concept : codeSystem.getConcept()) {
                coding = getCodingFromConcept(systemUrl, concept, code);
                if (coding != null) return coding;
            }
        } else {
            LOG.warn("Unknown Code System : " + systemUrl);
        }
        return null;
    }

    private Coding getCodingFromConcept (String system, CodeSystem.ConceptDefinitionComponent concept, String code) {
        Coding coding = null;
        if (concept.getCode().equals(code)){
            coding = new Coding(concept.getDefinition(), concept.getCode(), concept.getDisplay());
            coding.setSystem(system);
        } else if (concept.getConcept() != null) {
            for (CodeSystem.ConceptDefinitionComponent subconcept : concept.getConcept()) {
                coding = getCodingFromConcept(system, subconcept, code);
                if (coding != null) break;
            }
        }
        return coding;
    }

    private Coding getCodingFromValueSetConcept(String system, ValueSet.ConceptReferenceComponent concept) {
        return new Coding(system, concept.getCode(), concept.getDisplay());
    }
}
