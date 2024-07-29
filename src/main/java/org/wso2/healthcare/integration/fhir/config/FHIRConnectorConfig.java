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
import org.eclipse.core.runtime.Path;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.utils.FHIRPathEngine;
import org.wso2.healthcare.integration.fhir.internal.FHIRMessageContextCreator;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class holds the configurations related to the connector
 */
public class FHIRConnectorConfig {

    private static Log log = LogFactory.getLog(FHIRConnectorConfig.class);
    private static FHIRConnectorConfig configInstance;

    private boolean initialized = false;
    private Map<String, LinkedHashMap<String, String>> opPropMap = new HashMap<>();

    private FhirContext fhirContext;
    private FHIRPathEngine fhirPathEngine;

    private TerminologyHolder terminologyHolder;

    private FHIRConnectorConfig() {
    }

    public boolean isInitialized() {
        return initialized;
    }

    public FhirContext getFhirContext() {
        return fhirContext;
    }

    public FHIRPathEngine getFhirPathEngine() {
        return fhirPathEngine;
    }

    public static FHIRConnectorConfig getInstance() {
        if (configInstance != null) {
            return configInstance;
        }
        configInstance = new FHIRConnectorConfig();
        return configInstance;
    }

    /**
     * Function to initialize connector config
     */
    public synchronized void initialize() {
        if (!initialized) {
            //Initialize the connector config. This should be happen only once at deployment
            log.info("Initializing the FHIR Server connector");
            HealthcareIntegratorEnvironment environment =
                    OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();

            fhirContext = environment.getFhirContext();
            fhirPathEngine = environment.getFhirPathEngine();
            terminologyHolder = TerminologyHolder.getInstance();

            initXPathConfig();

            //Loading CodeSystems and ValueSets supported by the connector
            loadConnectorCodeSystemsAndValueSets();

            FHIRMessageContextCreator contextCreator = new FHIRMessageContextCreator();
            environment.registerMessageContextCreator(contextCreator);

            //Init complete
            initialized = true;
        }
    }

    public Map<String, LinkedHashMap<String, String>> getOpPropMap() {
        return opPropMap;
    }

    /**
     * This function will read all the properties files located in the inpu_schema directory.
     * <p>
     * The properties files names should be in following format : <OPERATION_NAME>.properties
     */
    private void initXPathConfig() {
        URL schemaURL = this.getClass().getClassLoader().getResource("fhir/input_schema");
        if (schemaURL != null) {
            File schemaDir = new File(schemaURL.getFile());
            //get all properties files
            String[] propFiles = schemaDir.list(((dir, name) -> name.toLowerCase().endsWith(".properties")));
            if (propFiles == null) return;
            for (String propFile : propFiles) {
                LinkedHashMap<String, String> propMap =
                        propertiesToMap(new File(schemaURL.getFile() + Path.SEPARATOR + propFile));
                String opName = propFile.substring(0, propFile.lastIndexOf('.'));
                opPropMap.put(opName, propMap);
            }
        }
    }

    private LinkedHashMap<String, String> propertiesToMap(File file) {
        LinkedHashMap<String, String> propertyMap = new LinkedHashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] keyValues = line.split("=");
                propertyMap.put(keyValues[0], keyValues[1]);
            }
        } catch (IOException e) {
            log.error("Error occurred while loading properties file: " + file.getName(), e);
        }
        return propertyMap;
    }

    /**
     * Function to load CodeSystems and ValueSets supported by the Connector
     */
    private void loadConnectorCodeSystemsAndValueSets() {
        InputStream valueSetInputStream = FHIRConnectorConfig.class.getResourceAsStream("/ValueSetBundle.xml");
        if (valueSetInputStream != null) {
            InputStreamReader reader = new InputStreamReader(valueSetInputStream);
            Bundle valueSetBundle = fhirContext.newXmlParser().parseResource(Bundle.class, reader);
            for (Bundle.BundleEntryComponent entry : valueSetBundle.getEntry()) {
                ValueSet valueSet = (ValueSet) entry.getResource();
                terminologyHolder.getValueSets().put(valueSet.getUrl(), valueSet);
                terminologyHolder.getUserDefinedValueSets().add(valueSet);
            }
        }
        InputStream codeSystemInputStream = FHIRConnectorConfig.class.getResourceAsStream("/CodeSystemBundle.xml");
        if (codeSystemInputStream != null) {
            InputStreamReader reader = new InputStreamReader(codeSystemInputStream);
            Bundle valueSetBundle = fhirContext.newXmlParser().parseResource(Bundle.class, reader);
            for (Bundle.BundleEntryComponent entry : valueSetBundle.getEntry()) {
                CodeSystem codeSystem = (CodeSystem) entry.getResource();
                terminologyHolder.getCodeSystems().put(codeSystem.getUrl(), codeSystem);
                terminologyHolder.getUserDefinedCodeSystems().add(codeSystem);
            }
        }
    }
}
