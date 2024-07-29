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
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.wso2.healthcare.integration.common.OpenHealthcareException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

/**
 * Utility class for FHIR resource parsing operations, such as File to StructureDefinition and IBaseResource etc.
 */
public class FHIRParserUtils {

    private static final FhirContext CTX = FhirContext.forR4();

    /**
     * Utility function to parse structured definition
     *
     * @param file input a fhir structure definition resource as file object
     * @return Structured definition object of the resource
     * @throws OpenHealthcareException
     */
    public static StructureDefinition parseStructuredDefinition(File file) throws OpenHealthcareException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(StructureDefinition.class, new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new OpenHealthcareException("Error occurred while parsing structured definition : " + file.getPath(), e);
        }
    }

    /**
     * Utility function to parse structured definition
     *
     * @param resourceStr input a fhir structure definition resource string
     * @return Structured definition object of resource
     * @throws OpenHealthcareException
     */
    public static StructureDefinition parseStructuredDefinition(String resourceStr) throws OpenHealthcareException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(StructureDefinition.class, new FileReader(resourceStr));
        } catch (FileNotFoundException e) {
            throw new OpenHealthcareException("Error occurred while parsing structured definition.", e);
        }
    }

    /**
     * Utility function to parse ValueSet definition
     *
     * @param file input a ValueSet definition resource as file object
     * @return ValueSet definition object
     * @throws OpenHealthcareException
     */
    public static ValueSet parseValueSet(File file) throws OpenHealthcareException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(ValueSet.class, new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new OpenHealthcareException("Error occurred while parsing structured definition : " + file.getPath(), e);
        }
    }

    /**
     * Utility function to parse a FHIR bundle
     * @param file input a resource Bundle as file object
     * @return Bundle object
     * @throws OpenHealthcareException
     */
    public static Bundle parseBundle(File file) throws OpenHealthcareException {
        // Instantiate a new parser
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(Bundle.class, new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new OpenHealthcareException("Error occurred while parsing structured definition : " + file.getPath(), e);
        }
    }

    /**
     * Utility function to parse a FHIR resource
     * @param file input a resource as a file object
     * @return IBaseResource object
     * @throws OpenHealthcareException
     */
    public static IBaseResource parseFHIRResource(File file) throws OpenHealthcareException {
        // Instantiate a new parser
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new OpenHealthcareException("Error occurred while parsing structured definition : " + file.getPath(), e);
        }
    }

    /**
     * Utility function to parse a FHIR resource.
     * @param inputStream input a resource as a FileInputStream object
     * @return IBaseResource object
     * @throws OpenHealthcareException
     */
    public static IBaseResource parseFHIRResource(InputStream inputStream) throws OpenHealthcareException {
        // Instantiate a new parser
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(inputStream);
        } catch (Exception e) {
            throw new OpenHealthcareException("Error occurred while parsing structured definition : " + inputStream, e);
        }
    }
}
