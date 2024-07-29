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

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.r4.hapi.ctx.IValidationSupport;
import org.hl7.fhir.r4.utils.FHIRPathEngine;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.context.AbstractMessageContextCreator;
import org.wso2.healthcare.integration.common.context.MessageContextType;
import org.wso2.healthcare.integration.common.fhir.server.FHIRAPIStore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Open Healthcare Integrator Environment
 * Contains information of the healthcare integrator environment which is registered as an osgi service
 */
public class HealthcareIntegratorEnvironment {
    private final HealthcareIntegratorConfig healthcareIntegratorConfig;
    private FhirContext fhirContext;
    private FHIRPathEngine fhirPathEngine;
    private FHIRAPIStore FHIRAPIStore;
    private final HashMap<MessageContextType, ArrayList<AbstractMessageContextCreator>> messageContextCreators =
                                                                                                        new HashMap<>(1);

    public HealthcareIntegratorEnvironment(HealthcareIntegratorConfig healthcareIntegratorConfig) {

        this.healthcareIntegratorConfig = healthcareIntegratorConfig;

        // initialize environment
        initialize();
    }

    private void initialize() {
        this.fhirContext = FhirContext.forR4();
        this.fhirPathEngine = new FHIRPathEngine(
                new HapiWorkerContext(fhirContext, (IValidationSupport) fhirContext.getValidationSupport()));
    }

    public HealthcareIntegratorConfig getHealthcareIntegratorConfig() {
        return healthcareIntegratorConfig;
    }

    public FhirContext getFhirContext() {

        return fhirContext;
    }

    public FHIRPathEngine getFhirPathEngine() {

        return fhirPathEngine;
    }

    public FHIRAPIStore getFHIRAPIStore() {
        return FHIRAPIStore;
    }

    public void setFHIRAPIStore(FHIRAPIStore FHIRAPIStore) {
        this.FHIRAPIStore = FHIRAPIStore;
    }

    /**
     * Register message context creator
     *
     * @param creator
     */
    public void registerMessageContextCreator(AbstractMessageContextCreator creator) {

        ArrayList<AbstractMessageContextCreator> creators = messageContextCreators.get(creator.getType());
        if (creators == null) {
            creators = new ArrayList<>();
            this.messageContextCreators.put(creator.getType(), creators);
        }
        creators.add(creator);
    }

    /**
     * Retrieve message context creators of given type
     *
     * @param type
     * @return
     */
    public ArrayList<AbstractMessageContextCreator> getMessageContextCreators(MessageContextType type) {

        return this.messageContextCreators.get(type);
    }
}
