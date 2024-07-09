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

package org.wso2.healthcare.integration.common.test;

import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;
import org.apache.axiom.om.OMElement;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.fhir.server.FHIRAPIStore;
import org.wso2.healthcare.integration.common.fhir.server.search.control.IncludeSearchParameter;
import org.wso2.healthcare.integration.common.test.utils.XMLTestUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract healthcare unit test base class
 * Healthcare unit tests should extend this
 */
public abstract class AbstractHealthcareUnitTestBase {

    /**
     * Function to initialize healthcare test environment
     *
     * @param configFile
     * @param carbonHome
     * @throws Exception
     */
    protected static void initHealthcareTestEnvironment(String configFile, String carbonHome) throws HealthcareTestException {

        updateCarbonHome(carbonHome);
        HealthcareIntegratorConfig config;
        try {
            // create instance of healthcare config
            Constructor<HealthcareIntegratorConfig> configConstructor =
                                                            HealthcareIntegratorConfig.class.getDeclaredConstructor();
            configConstructor.setAccessible(true);
            config = configConstructor.newInstance();
            // trigger to load config file
            Method parseMethod = HealthcareIntegratorConfig.class.getDeclaredMethod("parse", TomlParseResult.class);
            parseMethod.setAccessible(true);
            parseMethod.invoke(config, readTomlResourceFile(configFile));

        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new HealthcareTestException("Error occurred while loading healthcare configuration from: " + configFile, e);
        }

        // initialize healthcare environment
        HealthcareIntegratorEnvironment healthcareIntegratorEnvironment = new HealthcareIntegratorEnvironment(config);
        OHServerCommonDataHolder.getInstance().setHealthcareIntegratorEnvironment(healthcareIntegratorEnvironment);

        // initialize FHIR API store
        FHIRAPIStore fhirapiStore = new FHIRAPIStore();
        // register global search parameters in FHIR API Store
        IncludeSearchParameter includeSearchParameter = new IncludeSearchParameter();
        includeSearchParameter.setActive(true);
        fhirapiStore.addSearchControlParameter(includeSearchParameter);
        healthcareIntegratorEnvironment.setFHIRAPIStore(fhirapiStore);
    }

    /**
     * Function to cleanup healthcare environment
     */
    protected static void cleanupHealthcareTestEnvironment() {
        OHServerCommonDataHolder.getInstance().setHealthcareIntegratorEnvironment(null);
    }

    /**
     * Function to update CARBON_HOME
     *
     * @param carbonHome
     */
    protected static void updateCarbonHome(String carbonHome) {
        System.setProperty(Constants.CARBON_HOME, carbonHome);
    }

    /**
     * Function to read resource file
     *
     * @param relativeResourcePath
     * @return
     */
    protected static InputStream readResourceFile(String relativeResourcePath) {
        return AbstractHealthcareUnitTestBase.class.getClassLoader().getResourceAsStream(relativeResourcePath);
    }

    /**
     * Function to read XML file and parse to OMElement
     * @param relativeResourcePath
     * @return
     * @throws XMLStreamException
     */
    protected static OMElement readXMLResourceFile(String relativeResourcePath) throws HealthcareTestException {
        InputStream inputStream = readResourceFile(relativeResourcePath);
        try {
            return XMLTestUtils.toOMElement(inputStream);
        } catch (XMLStreamException e) {
            throw new HealthcareTestException("Error occurred while parsing XML file : " + relativeResourcePath, e);
        }
    }

    /**
     * Function to read Toml file and parse
     *
     * @param relativeResourcePath
     * @return
     * @throws Exception
     */
    protected static TomlParseResult readTomlResourceFile(String relativeResourcePath) throws HealthcareTestException {

        TomlParseResult tomlParseResult = null;
        try {
            tomlParseResult = Toml.parse(readResourceFile(relativeResourcePath));
        } catch (IOException e) {
            throw new HealthcareTestException("Error occurred while parsing toml file : " + relativeResourcePath, e);
        }
        if (tomlParseResult.hasErrors()) {
            StringBuilder strBuilder =
                    new StringBuilder("Error occurred while parsing the configuration file. Errors: \n");
            tomlParseResult.errors().forEach(tomlParseError -> {
                strBuilder.append(tomlParseError.toString());
                strBuilder.append("\n");
            });
            throw new HealthcareTestException(strBuilder.toString());
        }
        return tomlParseResult;
    }
}
