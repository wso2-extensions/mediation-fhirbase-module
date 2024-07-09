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

package org.wso2.healthcare.integration.common.fhir.server.model;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;

/**
 * contains information about FHIR HTTP request to FHIR API implementations
 */
public class FHIRRequestInfo extends AbstractSerializableInfo {

    private static final Log LOG = LogFactory.getLog(FHIRRequestInfo.class);
    public static final String FHIR_REQUEST_INFO_ROOT_NAME = "request_info";

    private HTTPInfo httpInfo;
    private FHIRInfo fhirInfo;

    @Override
    public OMElement serialize() {
        OMElement requestInfo = getRootOMElement();
        requestInfo.addChild(this.httpInfo.serialize());
        requestInfo.addChild(this.fhirInfo.serialize());
        return requestInfo;
    }

    @Override
    protected String getRootElementName() {
        return FHIR_REQUEST_INFO_ROOT_NAME;
    }

    public static FHIRRequestInfo build(MessageContext msgCtx, ResourceAPI resourceAPI) throws OpenHealthcareException {
        FHIRRequestInfo requestInfo = new FHIRRequestInfo();
        HTTPInfo httpInfo = HTTPInfo.build(msgCtx);
        requestInfo.setHttpInfo(httpInfo);
        requestInfo.setFhirInfo(FHIRInfo.build(resourceAPI, msgCtx, httpInfo));
        return requestInfo;
    }

    public HTTPInfo getHttpInfo() {

        return httpInfo;
    }

    public void setHttpInfo(HTTPInfo httpInfo) {

        this.httpInfo = httpInfo;
    }

    public FHIRInfo getFhirInfo() {

        return fhirInfo;
    }

    public void setFhirInfo(FHIRInfo fhirInfo) {

        this.fhirInfo = fhirInfo;
    }
}
