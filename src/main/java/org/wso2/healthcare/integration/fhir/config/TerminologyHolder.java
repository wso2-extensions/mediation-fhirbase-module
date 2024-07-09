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

package org.wso2.healthcare.integration.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.hapi.ctx.DefaultProfileValidationSupport;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ValueSet;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.config.ConfigUtil;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a singleton class which holds the FHIR Terminology configs like ValueSets and CodeSystems etc.
 */
public class TerminologyHolder {

    private static Log log = LogFactory.getLog(TerminologyHolder.class);
    private static TerminologyHolder terminologyHolder = new TerminologyHolder();
    private boolean initialized = false;
    private FhirContext fhirContext;
    private List<ValueSet> userDefinedValueSets = new ArrayList<>();
    private List<CodeSystem> userDefinedCodeSystems = new ArrayList<>();
    private Map<String, CodeSystem> codeSystems = new HashMap<>();
    private Map<String, ValueSet> valueSets = new HashMap<>();

    public static TerminologyHolder getInstance() {
        if (terminologyHolder == null) {
            terminologyHolder = new TerminologyHolder();
            terminologyHolder.initialize();
        }
        return terminologyHolder;
    }

    /**
     * Function to initialize connector config
     */
    public synchronized void initialize() {
        if (!initialized) {
            log.info("Initializing the FHIR Terminology holder");
            HealthcareIntegratorEnvironment environment =
                    OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();

            fhirContext = environment.getFhirContext();

            loadCodeAndValueSets();

            //Loading user defined CodeSystems and ValueSets
            loadUserDefinedCodeSystem();
            loadUserDefinedValueSet();

            //Init complete
            initialized = true;
        }
    }

    private void loadUserDefinedCodeSystem() {
        String codeSystemRootPath = System.getProperty("fhir.connector.resources.codeSystems");
        if (codeSystemRootPath == null) {
            HealthcareIntegratorConfig hConfig = HealthcareIntegratorConfig.getInstance();
            if (hConfig != null) {
                codeSystemRootPath = hConfig.getFHIRServerConfig().getResourceRoot() + "codeSystems";
            } else {
                codeSystemRootPath = ConfigUtil.getCarbonHome() + "fhir" + File.separatorChar + "codeSystems";
            }
        }
        File codeSystemsDir = new File(codeSystemRootPath);
        if (codeSystemsDir.exists() && codeSystemsDir.isDirectory()) {
            File[] codeSystemFiles = codeSystemsDir.listFiles();
            if (codeSystemFiles == null) return;
            for (File codeSystemFile : codeSystemFiles) {
                try {
                    InputStream inputStream = new FileInputStream(codeSystemFile);
                    InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
                    CodeSystem codeSystem = null;
                    if (codeSystemFile.getName().endsWith(".xml")) {
                        codeSystem = fhirContext.newXmlParser().parseResource(CodeSystem.class, reader);
                    } else if (codeSystemFile.getName().endsWith(".json")) {
                        codeSystem = fhirContext.newJsonParser().parseResource(CodeSystem.class, reader);
                    }
                    if (codeSystem != null) {
                        if (!codeSystems.containsKey(codeSystem.getUrl())) {
                            codeSystems.put(codeSystem.getUrl(), codeSystem);
                        }
                        userDefinedCodeSystems.add(codeSystem);
                        if (log.isDebugEnabled()) {
                            log.debug("CodeSystem " + codeSystem.getUrl() + " loaded from :" + codeSystemFile.getPath());
                        }
                    }
                } catch (FileNotFoundException e) {
                    log.error("Error occurred while loading external ValueSet from : " + codeSystemFile.getPath(), e);
                }
            }
        }
    }

    /**
     * Function to load user defined value sets
     */
    private void loadUserDefinedValueSet() {
        String valueSetRootPath = System.getProperty("fhir.connector.resources.valueSets");
        if (valueSetRootPath == null) {
            HealthcareIntegratorConfig hConfig = HealthcareIntegratorConfig.getInstance();
            if (hConfig != null) {
                valueSetRootPath = hConfig.getFHIRServerConfig().getResourceRoot() + "valueSets";
            } else {
                valueSetRootPath =
                        System.getProperty("carbon.home") + "fhir" + File.separatorChar + "valueSets";
            }
        }
        File valueSetDir = new File(valueSetRootPath);
        if (valueSetDir.exists() && valueSetDir.isDirectory()) {
            File[] valueSetFiles = valueSetDir.listFiles();
            if (valueSetFiles == null) return;
            for (File valueSetFile : valueSetFiles) {
                try {
                    InputStream inputStream = new FileInputStream(valueSetFile);
                    InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
                    ValueSet valueSet = null;
                    if (valueSetFile.getName().endsWith(".xml")) {
                        valueSet = fhirContext.newXmlParser().parseResource(ValueSet.class, reader);
                    } else if (valueSetFile.getName().endsWith(".json")) {
                        valueSet = fhirContext.newJsonParser().parseResource(ValueSet.class, reader);
                    }
                    if (valueSet != null) {
                        if (!valueSets.containsKey(valueSet.getUrl())) {
                            valueSets.put(valueSet.getUrl(), valueSet);
                        }
                        userDefinedValueSets.add(valueSet);
                        if (log.isDebugEnabled()) {
                            log.debug("ValueSet " + valueSet.getUrl() + " loaded from :" + valueSetFile.getPath());
                        }
                    }
                } catch (FileNotFoundException e) {
                    log.error("Error occurred while loading external ValueSet from : " + valueSetFile.getPath(), e);
                }
            }
        }
    }

    private void loadCodeAndValueSets() {
        DefaultProfileValidationSupport profileValidator = new DefaultProfileValidationSupport();
        //initialize the code, value sets
        profileValidator.fetchValueSet(fhirContext, "");
        profileValidator.fetchStructureDefinition(fhirContext, "");
        List<IBaseResource> iBaseResources = profileValidator.fetchAllConformanceResources(fhirContext);
        for (IBaseResource base : iBaseResources) {
            if (base instanceof CodeSystem) {
                codeSystems.put(((CodeSystem) base).getUrl(), (CodeSystem) base);
            } else if (base instanceof ValueSet) {
                valueSets.put(((ValueSet) base).getUrl(), (ValueSet) base);
            }
        }
    }

    public List<ValueSet> getUserDefinedValueSets() {
        return userDefinedValueSets;
    }

    public List<CodeSystem> getUserDefinedCodeSystems() {
        return userDefinedCodeSystems;
    }

    public Map<String, CodeSystem> getCodeSystems() {
        return codeSystems;
    }

    public Map<String, ValueSet> getValueSets() {
        return valueSets;
    }
}
