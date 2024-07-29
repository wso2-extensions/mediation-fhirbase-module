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

package org.wso2.healthcare.integration.common.config.model;

import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;
import net.consensys.cava.toml.TomlTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.config.ConfigUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Holds configurations of the healthcare integrator
 */
public class HealthcareIntegratorConfig {

    private static final Log LOG = LogFactory.getLog(HealthcareIntegratorConfig.class);

    private static HealthcareIntegratorConfig instance = null;

    private org.wso2.healthcare.integration.common.config.model.FHIRServerConfig fhirServerConfig;

    private HealthcareIntegratorConfig() {
    }

    public static HealthcareIntegratorConfig getInstance() {
        return instance;
    }

    public org.wso2.healthcare.integration.common.config.model.FHIRServerConfig getFHIRServerConfig() {
        return fhirServerConfig;
    }

    public static HealthcareIntegratorConfig build() throws OpenHealthcareException {
        if (getInstance() == null) {

            Path deploymentTOML =  Paths.get(ConfigUtil.getConfigFilePath(Constants.CONFIG_DEPLOYMENT_TOML));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loading Open-Healthcare configuration from : " + deploymentTOML.toString());
            }

            TomlParseResult tomlParseResult;
            try {
                tomlParseResult = Toml.parse(deploymentTOML);
                if (tomlParseResult.hasErrors()) {
                    StringBuilder strBuilder =
                            new StringBuilder("Error occurred while parsing the configuration file. Errors: \n");
                    tomlParseResult.errors().forEach(tomlParseError -> {
                        strBuilder.append(tomlParseError.toString());
                        strBuilder.append("\n");
                    });
                    throw new OpenHealthcareException(strBuilder.toString());
                }
            } catch (IOException e) {
                throw new OpenHealthcareException("Error occurred while parsing the deployment.toml file", e);
            }

            HealthcareIntegratorConfig healthcareConfig = new HealthcareIntegratorConfig();
            healthcareConfig.parse(tomlParseResult);
            instance = healthcareConfig;
        }
        return instance;
    }

    private void parse(TomlParseResult tomlConfig) {
        this.fhirServerConfig = buildServerConfig(tomlConfig);
    }

    private org.wso2.healthcare.integration.common.config.model.FHIRServerConfig buildServerConfig(TomlParseResult tomlConfig) {

        org.wso2.healthcare.integration.common.config.model.FHIRServerConfig fhirConfig = new org.wso2.healthcare.integration.common.config.model.FHIRServerConfig();

        Object fhirTomlConfigObj = tomlConfig.get("healthcare.fhir");
        if (fhirTomlConfigObj instanceof TomlTable) {
            TomlTable fhirTomlConfig = (TomlTable) fhirTomlConfigObj;
            fhirConfig.setResourceRoot(
                    fhirTomlConfig.getString("resource_root",
                            () -> ConfigUtil.getCarbonHome() + org.wso2.healthcare.integration.common.config.model.FHIRServerConfig.DEFAULT_RESOURCE_ROOT));
            setFhirServerConfigObj(fhirTomlConfig,fhirConfig);
        }

        Object fhirPaginationConfig = tomlConfig.get("healthcare.fhir.pagination");
        if (fhirPaginationConfig instanceof TomlTable) {
            fhirConfig.setPaginationConfig(buildPaginationConfig((TomlTable) fhirPaginationConfig));
        } else {
            fhirConfig.setPaginationConfig(new org.wso2.healthcare.integration.common.config.model.FHIRPaginationConfig());
        }

        Object fhirTerminologyConfig = tomlConfig.get("healthcare.fhir.terminology");
        if (fhirTerminologyConfig instanceof TomlTable) {
            org.wso2.healthcare.integration.common.config.model.FHIRTerminologyConfig terminologyConfig = buildTerminologyConfig((TomlTable) fhirTerminologyConfig);
            fhirConfig.setTerminologyConfig(terminologyConfig);
        } else {
            fhirConfig.setTerminologyConfig(new org.wso2.healthcare.integration.common.config.model.FHIRTerminologyConfig());
        }

        Object fhirDatasourceConfig = tomlConfig.get("healthcare.fhir.datasource");
        if (fhirDatasourceConfig instanceof TomlTable) {
            org.wso2.healthcare.integration.common.config.model.FHIRDataSourceConfig datasourceConfig = buildDatasourceConfig((TomlTable) fhirDatasourceConfig);
            fhirConfig.setDataSourceConfig(datasourceConfig);
        } else {
            fhirConfig.setDataSourceConfig(new org.wso2.healthcare.integration.common.config.model.FHIRDataSourceConfig());
        }

        Object fhirPreprocessorConfig = tomlConfig.get("healthcare.fhir.preprocessor");
        if (fhirPreprocessorConfig instanceof TomlTable) {
            fhirConfig.setFhirPreprocessorConfig(buildPreprocessorConfig((TomlTable) fhirPreprocessorConfig));
        } else {
            fhirConfig.setFhirPreprocessorConfig(new org.wso2.healthcare.integration.common.config.model.FHIRPreprocessorConfig());
        }

        org.wso2.healthcare.integration.common.config.model.FHIRConnectorConfig fhirConnectorConfig = new FHIRConnectorConfig();
        Object fhirConnectorConfigObj = tomlConfig.get("healthcare.fhir.connector");
        if (fhirConnectorConfigObj instanceof TomlTable) {
            TomlTable fhirConnectorTomlConfig = (TomlTable) fhirConnectorConfigObj;
            fhirConnectorConfig.setEnableValidateOnSerialization(
                    fhirConnectorTomlConfig.getBoolean("enable_validate_on_serialization", () -> false));
        }
        fhirConfig.setFHIRConnectorConfig(fhirConnectorConfig);

        org.wso2.healthcare.integration.common.config.model.FHIRTemplateConfig fhirTemplateConfig = new org.wso2.healthcare.integration.common.config.model.FHIRTemplateConfig();
        Object fhirTemplateConfigObj = tomlConfig.get("healthcare.fhir.templates");
        if (fhirTemplateConfigObj instanceof TomlTable) {
            TomlTable fhirTemplateTomlConfig = (TomlTable) fhirTemplateConfigObj;
            fhirTemplateConfig.setResourceTemplatesPath(
                    fhirTemplateTomlConfig.getString("integration_templates_resources_path",
                            () -> org.wso2.healthcare.integration.common.config.model.FHIRTemplateConfig.DEFAULT_RESOURCE_TEMPLATES_PATH));
            fhirTemplateConfig.setKeyMappingTemplateFilePath(
                    fhirTemplateTomlConfig.getString("integration_templates_keymapping_path",
                            () -> org.wso2.healthcare.integration.common.config.model.FHIRTemplateConfig.DEFAULT_KEYMAPPING_TEMPLATE_PATH));
            fhirTemplateConfig.setConditionsTemplateFilePath(
                    fhirTemplateTomlConfig.getString("integration_templates_conditions_path",
                            () -> FHIRTemplateConfig.DEFAULT_CONDITIONS_TEMPLATE_PATH));
        }
        fhirConfig.setFhirTemplateConfig(fhirTemplateConfig);

        org.wso2.healthcare.integration.common.config.model.FHIRRepositoryConfig fhirRepositoryConfig = new org.wso2.healthcare.integration.common.config.model.FHIRRepositoryConfig();
        Object fhirRepositoryConfigObj = tomlConfig.get("healthcare.fhir.repository");
        if (fhirRepositoryConfigObj instanceof TomlTable ) {
            TomlTable fhirRepositoryTomlConfig = (TomlTable) fhirRepositoryConfigObj;
            setFhirRepositoryConfigObj(fhirRepositoryTomlConfig,fhirRepositoryConfig);
        }
        fhirConfig.setFhirRepositoryConfig(fhirRepositoryConfig);

        return fhirConfig;
    }

    private void setFhirRepositoryConfigObj(TomlTable fhirRepositoryTomlConfig, FHIRRepositoryConfig fhirRepositoryConfig) {
        fhirRepositoryConfig.setRepositoryType(fhirRepositoryTomlConfig.getString("repository_type"));
        fhirRepositoryConfig.setBase(fhirRepositoryTomlConfig.getString("base"));
        fhirRepositoryConfig.setClientId(fhirRepositoryTomlConfig.getString("client_id"));
        fhirRepositoryConfig.setClientSecret(fhirRepositoryTomlConfig.getString("client_secret"));
        fhirRepositoryConfig.setTokenEndpoint(fhirRepositoryTomlConfig.getString("token_endpoint"));
        fhirRepositoryConfig.setRepoTokenHashSalt(fhirRepositoryTomlConfig.getString("repo_token_salt",
                                                                                     () -> "WSO2_FHIR_REPOSITORY"));
        // for Azure FHIR repository
        fhirRepositoryConfig.setStorageResourceUrl(fhirRepositoryTomlConfig.getString("storage_resource_url",
                           () -> Constants.AZURE_STORAGE_RESOURCE_URL));
        fhirRepositoryConfig.setStorageAccountUrl(fhirRepositoryTomlConfig.getString("storage_account_url"));
        fhirRepositoryConfig.setStorageApiVersion(fhirRepositoryTomlConfig.getString("storage_api_version"));
    }

    private void setFhirServerConfigObj(TomlTable fhirTomlConfig, FHIRServerConfig fhirConfig) {
        fhirConfig.setBaseUrl(fhirTomlConfig.getString("base_url"));
    }

    private org.wso2.healthcare.integration.common.config.model.FHIRPaginationConfig buildPaginationConfig(TomlTable tomlConfig) {
        org.wso2.healthcare.integration.common.config.model.FHIRPaginationConfig paginationConfig = new FHIRPaginationConfig();
        paginationConfig.setMaxPageSize((int) tomlConfig.getLong("max_page_size", () -> 50));
        paginationConfig.setDefaultPageSize((int) tomlConfig.getLong("default_page_size", () -> 10));
        return paginationConfig;
    }

    private org.wso2.healthcare.integration.common.config.model.FHIRTerminologyConfig buildTerminologyConfig(TomlTable tomlConfig) {
        org.wso2.healthcare.integration.common.config.model.FHIRTerminologyConfig terminologyConfig = new FHIRTerminologyConfig();
        String systemUriPrefix = tomlConfig.getString("generated_uri_base", () -> Constants.FHIR_DEFAULT_SYSTEM_URI_PREFIX);
        if (!systemUriPrefix.endsWith("/")) {
            systemUriPrefix = systemUriPrefix + "/";
        }
        terminologyConfig.setCodeSystemURIPrefix(systemUriPrefix);
        return terminologyConfig;
    }

    private org.wso2.healthcare.integration.common.config.model.FHIRDataSourceConfig buildDatasourceConfig(TomlTable tomlConfig) {
        org.wso2.healthcare.integration.common.config.model.FHIRDataSourceConfig dataSourceConfig = new FHIRDataSourceConfig();
        dataSourceConfig.setMatchAnyPattern(tomlConfig.getString("match_any_pattern", () -> "*"));
        return dataSourceConfig;
    }

    private org.wso2.healthcare.integration.common.config.model.FHIRPreprocessorConfig buildPreprocessorConfig(TomlTable tomlConfig){
        org.wso2.healthcare.integration.common.config.model.FHIRPreprocessorConfig fhirPreprocessorConfig = new FHIRPreprocessorConfig();
        fhirPreprocessorConfig.setEnable(tomlConfig.getBoolean("enable", () -> true));
        fhirPreprocessorConfig.setEnableDataServiceQueryGen(tomlConfig.getBoolean("enable_data_services_query_generation", () -> true));
        return fhirPreprocessorConfig;
    }
}
