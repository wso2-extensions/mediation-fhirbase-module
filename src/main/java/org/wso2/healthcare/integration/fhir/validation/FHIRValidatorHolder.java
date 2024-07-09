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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorConfig;
import org.wso2.healthcare.integration.fhir.config.TerminologyHolder;
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
import java.util.List;

/**
 * This is a singleton class which holds the FHIR validator object to validate the FHIR payload.
 */
public class FHIRValidatorHolder {

    private Log log = LogFactory.getLog(FHIRConnectorConfig.class);
    private static FHIRValidatorHolder fhirValidatorHolder = new FHIRValidatorHolder();
    private TerminologyHolder terminologyHolder;
    private FHIRPayloadValidator fhirPayloadValidator;
    private FhirContext fhirContext;
    private List<StructureDefinition> customProfiles = new ArrayList<>();
    private List<ValueSet> userDefinedValueSets = new ArrayList<>();
    private List<CodeSystem> userDefinedCodeSystems = new ArrayList<>();


    private FHIRValidatorHolder() {
    }

    public static FHIRValidatorHolder getInstance() {
        if (fhirValidatorHolder == null) {
            fhirValidatorHolder = new FHIRValidatorHolder();
            fhirValidatorHolder.initialize();
        }
        return fhirValidatorHolder;
    }

    public synchronized void initialize() {

        HealthcareIntegratorEnvironment environment =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        fhirContext = environment.getFhirContext();

        terminologyHolder = TerminologyHolder.getInstance();

        userDefinedCodeSystems = terminologyHolder.getUserDefinedCodeSystems();
        userDefinedValueSets = terminologyHolder.getUserDefinedValueSets();

        initFHIRPayloadValidator();
    }

    private void initFHIRPayloadValidator() {
        fhirPayloadValidator = new FHIRPayloadValidator(fhirContext);
        String profilePath = System.getProperty("fhir.connector.resources.profiles");
        if (profilePath == null) {
            HealthcareIntegratorConfig hConfig = HealthcareIntegratorConfig.getInstance();
            if (hConfig != null) {
                profilePath = hConfig.getFHIRServerConfig().getResourceRoot() + "profiles";
            } else {
                profilePath =
                        ConfigUtil.getCarbonHome() + "fhir" + File.separatorChar + "profiles";
            }
        }
        File profilesDir = new File(profilePath);
        if (profilesDir.exists() && profilesDir.isDirectory()) {
            File[] profileFiles = profilesDir.listFiles();
            if (profileFiles == null) return;
            for (File profileDefinition : profileFiles) {
                try {
                    InputStream inputStream = new FileInputStream(profileDefinition);
                    InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
                    StructureDefinition definition;
                    if (profileDefinition.getName().endsWith(".xml")) {
                        definition = fhirContext.newXmlParser()
                                .parseResource(StructureDefinition.class, reader);
                        customProfiles.add(definition);
                    } else if (profileDefinition.getName().endsWith(".json")) {
                        definition = fhirContext.newJsonParser()
                                .parseResource(StructureDefinition.class, reader);
                        customProfiles.add(definition);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Profile structure definition is loaded from: " + profileDefinition.getPath());
                    }
                } catch (FileNotFoundException e) {
                    log.error("Error occurred while loading external Profile from : " + profileDefinition.getPath(), e);
                }
            }
            fhirPayloadValidator
                    .initValidatorForCustomProfiles(customProfiles, userDefinedValueSets, userDefinedCodeSystems);
        } else {
            fhirPayloadValidator.initValidator();
        }
    }

    public FHIRPayloadValidator getFhirPayloadValidator() {
        return fhirPayloadValidator;
    }
}
