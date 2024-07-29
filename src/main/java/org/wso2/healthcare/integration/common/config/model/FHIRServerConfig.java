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

import org.wso2.healthcare.integration.common.config.ConfigUtil;

import java.io.File;

/**
 * Class containing server level configurations
 * which is immediately under [healthcare.fhir] tag
 */
public class FHIRServerConfig {

    public static final String DEFAULT_RESOURCE_ROOT = "fhir" + File.separator;

    private String resourceRoot;
    private String baseUrl;
    private FHIRPaginationConfig paginationConfig;
    private FHIRTerminologyConfig terminologyConfig;
    private FHIRDataSourceConfig dataSourceConfig;
    private FHIRConnectorConfig fhirConnectorConfig;
    private FHIRPreprocessorConfig fhirPreprocessorConfig;
    private org.wso2.healthcare.integration.common.config.model.FHIRTemplateConfig fhirTemplateConfig;
    private FHIRRepositoryConfig fhirRepositoryConfig;

    public FHIRServerConfig() {
        resourceRoot = ConfigUtil.getCarbonHome() + DEFAULT_RESOURCE_ROOT;
    }

    public String getResourceRoot() {
        return resourceRoot;
    }

    public void setResourceRoot(String resourceRoot) {
        this.resourceRoot = resourceRoot;
    }

    public FHIRPaginationConfig getPaginationConfig() {

        return paginationConfig;
    }

    public void setPaginationConfig(FHIRPaginationConfig paginationConfig) {

        this.paginationConfig = paginationConfig;
    }

    public FHIRTerminologyConfig getTerminologyConfig() {

        return terminologyConfig;
    }

    public void setTerminologyConfig(FHIRTerminologyConfig terminologyConfig) {

        this.terminologyConfig = terminologyConfig;
    }

    public FHIRDataSourceConfig getDataSourceConfig() {

        return dataSourceConfig;
    }

    public void setDataSourceConfig(FHIRDataSourceConfig dataSourceConfig) {

        this.dataSourceConfig = dataSourceConfig;
    }

    public FHIRConnectorConfig getFHIRConnectorConfig() {

        return fhirConnectorConfig;
    }

    public void setFHIRConnectorConfig(FHIRConnectorConfig fhirConnectorConfig) {

        this.fhirConnectorConfig = fhirConnectorConfig;
    }

    public FHIRPreprocessorConfig getFhirPreprocessorConfig() {

        return fhirPreprocessorConfig;
    }

    public void setFhirPreprocessorConfig(FHIRPreprocessorConfig fhirPreprocessorConfig) {

        this.fhirPreprocessorConfig = fhirPreprocessorConfig;
    }

    public org.wso2.healthcare.integration.common.config.model.FHIRTemplateConfig getFhirTemplateConfig() {

        return fhirTemplateConfig;
    }

    public void setFhirTemplateConfig(FHIRTemplateConfig fhirTemplateConfig) {

        this.fhirTemplateConfig = fhirTemplateConfig;
    }

    public FHIRRepositoryConfig getFhirRepositoryConfig() {
        return fhirRepositoryConfig;
    }

    public void setFhirRepositoryConfig(FHIRRepositoryConfig fhirRepositoryConfig) {
        this.fhirRepositoryConfig = fhirRepositoryConfig;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
