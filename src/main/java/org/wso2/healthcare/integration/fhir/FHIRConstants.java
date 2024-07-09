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

package org.wso2.healthcare.integration.fhir;

/**
 * This class holds constants related to FHIR server connector
 */
public class FHIRConstants {

    public static final String FHIR_CONTEXT = "_CONNECTOR_INTERNAL_FHIR_CONTEXT";
    public static final String FHIR_PARAM_OBJECT_ID = "objectId";
    public static final String FHIR_PARAM_DATA_OBJECT_ID = "objectId";
    public static final String FHIR_BASE_URL = "baseUrl";
    //TODO need to give intuitive name
    public static final String FHIR_PARAM_READ_FROM_MESSAGE = "dataMapperEnabled";
    public static final String FHIR_PARAM_SOURCE_OBJECT_ID = "sourceObjectId";
    public static final String FHIR_PARAM_TARGET_OBJECT_ID = "targetObjectId";
    public static final String FHIR_PARAM_TARGET_PROPERTY = "targetProperty";
    public static final String FHIR_PARAM_FHIR_PATH = "FHIRPath";

    public static final String MESSAGE_TYPE = "messageType";

    public final static String JSON_CONTENT_TYPE = "application/json";
    public final static String XML_CONTENT_TYPE = "application/xml";
    public final static String TEXT_XML_CONTENT_TYPE = "text/xml";

    public final static String JSON = "JSON";
    public final static String XML = "XML";

    public final static String FHIR_XML_CONTENT_TYPE = "application/fhir+xml";
    public final static String FHIR_JSON_CONTENT_TYPE = "application/fhir+json";

    public static final String HTTP_HEADER_ACCEPT = "Accept";

    public static final String FHIR_VALIDATION = "FHIRValidation";
    public static final String FHIR_VALIDATION_STATUS = "FHIRValidationStatus";
    public static final String FHIR_VALIDATION_OUTCOME = "FHIRValidationOutcome";
    public static final String FHIR_VALIDATION_SUCCESS = "FHIRValidationSuccess";
    public static final String FHIR_VALIDATE_OPERATION_OUTCOME_ID = "validateOperationOutcomeId";

    public static final String ERROR_CODE_VALIDATING_PAYLOAD = "400010";

    public static boolean IS_INCLUDE_SEARCH_PARAMETER = false;
  
    //template related constants
    public static final String JSON_EVAL = "json-eval(";
    public static final String BACKBONE_ELEMENT = "BackboneElement";
    public static final String EXTENSION = "Extension";
    public static final String FHIR_WRITE_API_JSON_PROPERTY_NAMES = "_OH_INTERNAL_FHIR_WRITE_API_JSON_PROPERTY_NAMES_";
    public static final String FHIR_WRITE_MESSAGE_BODY_PROPERTY = "_OH_INTERNAL_WRITE_MESSAGE_BODY_PROPERTY_";
    public static final String FHIR_ORIGINAL_MSG_BODY_PROPERTY = "_OH_ORIGINAL_MSG_BODY_PROPERTY";
    //consent mgt related constants
    public static final String ALL_RESOURCE_FIELDS = "ALL_RESOURCE_FIELDS";
    public static final String PERMIT = "PERMIT";
    public static final String DENY = "DENY";
}
