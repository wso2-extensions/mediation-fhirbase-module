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

package org.wso2.healthcare.integration.fhir.template.util;

import org.apache.synapse.registry.Registry;
import org.wso2.healthcare.integration.fhir.template.FHIRTemplateReader;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used to load registry and templates to the memory.
 */
public class RegistryTemplateLoader {

    private static final int MAX_RETRY = 30;

    public void execute() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        AtomicInteger counter = new AtomicInteger(1);

        scheduler.execute(new Runnable() {
            @Override
            public void run() {
                boolean isRegistryAvailable = loadRegistry();
                if (counter.get() < MAX_RETRY && !isRegistryAvailable) {
                    counter.getAndIncrement();
                    scheduler.schedule(this, 1, TimeUnit.SECONDS);
                } else {
                    loadRegistryTemplateArtifacts();
                    scheduler.shutdown();
                }
            }
        });
    }

    private boolean loadRegistry() {

        return OHServerCommonDataHolder.getInstance().getRegistry() != null;
    }

    /**
     * This method is used to load FHIR resource templates from the path defined
     */
    public void loadRegistryTemplateArtifacts() {

        Registry registry = OHServerCommonDataHolder.getInstance().getRegistry();
        String keyMappingTemplateFilePath = null;
        String conditionsTemplateFilePath = null;
        String resourceTemplatePath = null;
        HealthcareIntegratorConfig hConfig = HealthcareIntegratorConfig.getInstance();
        if (hConfig != null) {
            keyMappingTemplateFilePath =
                    hConfig.getFHIRServerConfig().getFhirTemplateConfig().getKeyMappingTemplateFilePath();
            conditionsTemplateFilePath =
                    hConfig.getFHIRServerConfig().getFhirTemplateConfig().getConditionsTemplateFilePath();
            resourceTemplatePath =
                    hConfig.getFHIRServerConfig().getFhirTemplateConfig().getResourceTemplatesPath();
        }
        //load template configurations
        FHIRTemplateReader fhirTemplateReader =
                new FHIRTemplateReader(resourceTemplatePath, keyMappingTemplateFilePath, conditionsTemplateFilePath);
        fhirTemplateReader.loadTemplates(registry);
    }
}
