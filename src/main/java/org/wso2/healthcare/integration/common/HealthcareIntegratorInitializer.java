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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.fhir.server.FHIRAPIStore;
import org.wso2.healthcare.integration.common.fhir.server.search.common.IdSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.common.LastUpdatedSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.common.ProfileSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.control.CountSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.control.IncludeSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.control.OffsetSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.search.control.RevincludeSearchParameter;

public class HealthcareIntegratorInitializer {

    private static final Log LOG = LogFactory.getLog(HealthcareIntegratorInitializer.class);

    /**
     * Initialize Healthcare Integrator Environment
     * @throws OpenHealthcareException
     */
    public HealthcareIntegratorEnvironment initialize() throws OpenHealthcareException {
        if (OHServerCommonDataHolder.getInstance().isInitialized()) {
            return OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        }

        // Initialize healthcare integrator environment
        HealthcareIntegratorEnvironment healthcareIntegratorEnvironment =
                                                new HealthcareIntegratorEnvironment(HealthcareIntegratorConfig.build());
        OHServerCommonDataHolder.getInstance().setHealthcareIntegratorEnvironment(healthcareIntegratorEnvironment);

        // initialize FHIR API Store
        healthcareIntegratorEnvironment.setFHIRAPIStore(createAndInitFHIRAPIStore());
        OHServerCommonDataHolder.getInstance().setInitialized(true);

        LOG.info("Healthcare Integrator Runtime initialized successfully.");

        return healthcareIntegratorEnvironment;
    }

    private FHIRAPIStore createAndInitFHIRAPIStore() {
        // initialize FHIR API store
        FHIRAPIStore fhirapiStore = new FHIRAPIStore();

        // register global Search Control Parameters in FHIR API Store
        fhirapiStore.addSearchControlParameter(new IncludeSearchParameter());
        fhirapiStore.addSearchControlParameter(new RevincludeSearchParameter());
        fhirapiStore.addSearchControlParameter(new CountSearchParameter());
        fhirapiStore.addSearchControlParameter(new OffsetSearchParameter());

        // register Common Parameters defined for all resources
        fhirapiStore.addCommonSearchParameter(new ProfileSearchParameter());
        fhirapiStore.addCommonSearchParameter(new IdSearchParameter());
        fhirapiStore.addCommonSearchParameter(new LastUpdatedSearchParameter());

        // TODO register global operations in FHIR API Store here

        return fhirapiStore;
    }
}
