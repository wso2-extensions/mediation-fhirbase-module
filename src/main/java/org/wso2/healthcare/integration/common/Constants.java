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

package org.wso2.healthcare.integration.common;

import org.wso2.healthcare.integration.common.utils.HealthcareUtils;

/**
 * Contains common constants
 */
public class Constants {

    public static final String CARBON_CONFIG_DIR_PATH_ENV = "CARBON_CONFIG_DIR_PATH";
    public static final String CARBON_HOME = "carbon.home";
    public static final String CARBON_HOME_ENV = "CARBON_HOME";
    public static final String CARBON_CONFIG_DIR_PATH = "carbon.config.dir.path";

    public static final String CONFIG_DEPLOYMENT_TOML = "deployment.toml";

    public static final String SYNAPSE_NS = "http://ws.apache.org/ns/synapse";

    public static final String OH_INTERNAL_PROPERTY_PREFIX = "_OH_INTERNAL_";
    public static final String OH_INTERNAL_PROPERTY_SUFFIX = "_";

    public static final String OH_INTERNAL_FHIR_RESOURCE =
                                        HealthcareUtils.createInternalSynapsePropertyName("FHIR_RESOURCE");
    public static final String OH_INTERNAL_FHIR_MESSAGE_CONTEXT =
                                        HealthcareUtils.createInternalSynapsePropertyName("FHIR_HEALTHCARE_MSG_CTX");

    public static final String ON_PROPERTY_PREFIX = "_OH_";

    public static final String OH_PROP_REQUEST_INFO_XML =
                                                    HealthcareUtils.createOHSynapsePropertyName("OH_REQUEST_INFO_XML");
    public static final String OH_PROP_REQUEST_INFO_OBJ =
                                                    HealthcareUtils.createOHSynapsePropertyName("OH_REQUEST_INFO_OBJ");
    public static final String OH_PROP_FHIR_ERROR_SEVERITY =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_ERROR_SEVERITY");
    public static final String OH_PROP_FHIR_ERROR_CODE =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_ERROR_CODE");
    public static final String OH_PROP_FHIR_ERROR_DETAIL_CODE =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_ERROR_DETAIL_CODE");
    public static final String OH_PROP_FHIR_ERROR_DETAIL_DISPLAY =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_ERROR_DETAIL_DISPLAY");
    public static final String OH_PROP_FHIR_ERROR_DETAIL_SYSTEM =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_ERROR_DETAIL_SYSTEM");
    public static final String OH_PROP_FHIR_ERROR_DIAGNOSTICS =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_ERROR_DIAGNOSTICS");
    public static final String OH_PROP_FHIR_BE_DS_QUERY =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_BE_DS_QUERY");

    public static final String OH_INTERNAL_FHIR_SERVER_URL = HealthcareUtils.createInternalSynapsePropertyName("FHIR_SERVER_URL");

    // _lastUpdated Search parameter
    public static final String OH_PROP_FHIR_LAST_UPDATED =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_LAST_UPDATED");
    // Date time prefix of _lastUpdated search parameter
    public static final String OH_PROP_FHIR_LAST_UPDATED_PREFIX =
                                                    HealthcareUtils.createOHSynapsePropertyName("PROP_FHIR_LAST_UPDATED_PREFIX");

    // Default prefix of generated code systems
    public static final String FHIR_DEFAULT_SYSTEM_URI_PREFIX = "https://healthcare.wso2.org/";

    // FHIR Search parameters
    public static final String FHIR_SEARCH_PARAM_PROFILE = "_profile";
    public static final String FHIR_SEARCH_PARAM_ID = "_id";
    public static final String FHIR_SEARCH_PARAM_LAST_UPDATED = "_lastUpdated";
    public static final String FHIR_SEARCH_PARAM_COUNT = "_count";
    public static final String FHIR_SEARCH_PARAM_OFFSET = "_offset";
    public static final String FHIR_SEARCH_PARAM_INCLUDE = "_include";
    public static final String FHIR_SEARCH_PARAM_REVINCLUDE = "_revinclude";

    public static final String FHIR_DATATYPE_REFERENCE = "reference";
    public static final String FHIR_DATATYPE_STRING = "string";
    public static final String FHIR_DATATYPE_TOKEN = "token";
    public static final String FHIR_DATATYPE_DATE = "date";
    public static final String FHIR_SEARCH_TYPE_SEARCH_CONTROL = "SearchControl";
    public static final String FHIR_SEARCH_TYPE_COMMON = "Common";

    // FHIR REST Interaction levels
    public static final String FHIR_REST_INT_LEVEL_INSTANCE = "INSTANCE_LEVEL";
    public static final String FHIR_REST_INT_LEVEL_TYPE = "TYPE_LEVEL";
    public static final String FHIR_REST_INT_LEVEL_SYSTEM = "SYSTEM_LEVEL";

    public static final String FHIR_SYNAPSE_PROP_COUNT = "uri.var.count";
    public static final String FHIR_SYNAPSE_PROP_OFFSET = "uri.var.offset";

    // Properties for backward compatibility
    @Deprecated
    public static final String FHIR_SYNAPSE_PROP_LAST_UPDATED = "uri.var._lastUpdated";
    @Deprecated
    public static final String FHIR_SYNAPSE_PROP_LAST_UPDATED_OP = "_OH_lastUpdatedOp";
    @Deprecated
    public static final String FHIR_SYNAPSE_PROP_ID = "uri.var._id";

    public static final String REST_SYNAPSE_PROP_ID = "uri.var.id";
    public static final String AZURE_STORAGE_RESOURCE_URL = "https://storage.azure.com/";
}
