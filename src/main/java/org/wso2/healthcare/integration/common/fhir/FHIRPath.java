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

package org.wso2.healthcare.integration.common.fhir;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.utils.FHIRPathEngine;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;

import java.util.List;

/**
 * FHIR Path evaluator
 */
public class FHIRPath {

    private Resource resource;
    private String path;

    public FHIRPath(String fhirPath, Resource resource) {

        this.resource = resource;
        this.path = fhirPath;
    }

    public FHIRPath(String fhirPath) {

        this.path = fhirPath;
    }

    /**
     * Evaluate FHIR path against resource and return the result
     *
     * @return
     */
    public List<Base> evaluate() throws OpenHealthcareFHIRException {

        if (resource != null && StringUtils.isBlank(this.path)) {
            throw new OpenHealthcareFHIRException("Resource or FHIRPath is missing",
                    OpenHealthcareFHIRException.Severity.ERROR, OpenHealthcareFHIRException.IssueType.PROCESSING,
                    OpenHealthcareFHIRException.Details.INTERNAL_SERVER_ERROR, null);
        }
        HealthcareIntegratorEnvironment env = OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        FHIRPathEngine fhirPathEngine = env.getFhirPathEngine();
        return fhirPathEngine.evaluate(getResource(), getPath());
    }

    public Resource getResource() {

        return resource;
    }

    public void setResource(Resource resource) {

        this.resource = resource;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }
}
