/*
 *  Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.healthcare.integration.common.config.model;

/**
 * Contains configuration related to FHIR Repository Connector
 */

public class FHIRRepositoryConfig {

    private String base;
    private String clientId;
    private String clientSecret;
    private String tokenEndpoint;
    private String repositoryType;

    // for Azure FHIR repository
    private String storageResourceUrl;
    private String storageAccountUrl;
    private String storageApiVersion;

    private String repoTokenHashSalt;

    public FHIRRepositoryConfig(){}

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

    public String getStorageResourceUrl() {

        return storageResourceUrl;
    }

    public void setStorageResourceUrl(String storageResourceUrl) {

        this.storageResourceUrl = storageResourceUrl;
    }

    public String getStorageAccountUrl() {

        return storageAccountUrl;
    }

    public void setStorageAccountUrl(String storageAccountUrl) {

        this.storageAccountUrl = storageAccountUrl;
    }

    public String getRepoTokenHashSalt() {

        return repoTokenHashSalt;
    }

    public void setRepoTokenHashSalt(String repoTokenHashSalt) {

        this.repoTokenHashSalt = repoTokenHashSalt;
    }

    public String getStorageApiVersion() {

        return storageApiVersion;
    }

    public void setStorageApiVersion(String storageApiVersion) {

        this.storageApiVersion = storageApiVersion;
    }
}
