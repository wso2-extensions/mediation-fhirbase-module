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

import org.apache.synapse.registry.Registry;

public class OHServerCommonDataHolder {

    private Registry registry;
    private org.wso2.healthcare.integration.common.ServerType serverType;
    private HealthcareIntegratorEnvironment healthcareIntegratorEnvironment;
    private boolean isInitialized = false;

    private static OHServerCommonDataHolder dataHolder = new OHServerCommonDataHolder();

    public static OHServerCommonDataHolder getInstance() {
        return dataHolder;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public org.wso2.healthcare.integration.common.ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public HealthcareIntegratorEnvironment getHealthcareIntegratorEnvironment() {
        return healthcareIntegratorEnvironment;
    }

    public void setHealthcareIntegratorEnvironment(HealthcareIntegratorEnvironment healthcareIntegratorEnvironment) {
        this.healthcareIntegratorEnvironment = healthcareIntegratorEnvironment;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
}
