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

import org.wso2.healthcare.integration.common.OpenHealthcareException;

/**
 * Open Healthcare FHIR related exception
 */
public class OpenHealthcareFHIRException extends OpenHealthcareException {

    private Severity severity;
    private IssueType code;
    private Details detail;
    private String diagnostic;

    public OpenHealthcareFHIRException(String message, Severity severity, IssueType code) {

        super(message);
        this.severity = severity;
        this.code = code;
    }

    public OpenHealthcareFHIRException(String message, Severity severity, IssueType code, Details detail, String diagnostic) {

        super(message);
        this.severity = severity;
        this.code = code;
        this.detail = detail;
        if (diagnostic == null) {
            this.diagnostic = message;
        }
    }

    public OpenHealthcareFHIRException(String message) {

        super(message);
    }

    public OpenHealthcareFHIRException(String message, Throwable cause) {

        super(message, cause);
    }

    public Severity getSeverity() {

        return severity;
    }

    public IssueType getCode() {

        return code;
    }

    public Details getDetail() {

        return detail;
    }

    public String getDiagnostic() {

        return diagnostic;
    }

    public enum Severity {
        FATAL("fatal"),
        ERROR("error"),
        WARNING("warning"),
        INFORMATION("information");

        private final String code;

        Severity(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum IssueType {
        INVALID("invalid"),
        PROCESSING("processing"),
        NOT_SUPPORTED("not-supported"),
        SECURITY("security"),
        TRANSIENT("transient"),
        INFORMATIONAL("informational");

        private final String code;

        IssueType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum Details {
        INVALID_FHIR_PROFILE("REQ_INVALID_PROFILE", "Invalid profile requested"),
        INVALID_SEARCH_PARAMETER("REQ_INVALID_SEARCH_PARAMETER", "Invalid search parameter"),
        INVALID_SEARCH_PARAMETER_VALUE("REQ_INVALID_SEARCH_PARAMETER_VALUE", "Invalid search parameter value"),
        INVALID_OPERATION("REQ_INVALID_OPERATION", "Invalid Operation"),
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal Server Error"),
        RESOURCE_NOT_SUPPORTED("RESOURCE_NOT_SUPPORTED", "Resource Not Supported");

        private final String code;
        private final String display;

        Details(String code, String display) {
            this.code = code;
            this.display = display;
        }

        public String getCode() {
            return this.code;
        }

        public String getDisplay() {
            return this.display;
        }

        public String getSystem() {
            return "operation-outcome";
        }
    }
}
