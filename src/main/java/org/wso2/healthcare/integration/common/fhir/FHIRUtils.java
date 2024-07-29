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

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.common.fhir.server.FHIRAPIStore;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;

/**
 * FHIR related utilities
 */
public class FHIRUtils {

    /**
     * Get FHIR API Store
     *
     * @return
     */
    public static FHIRAPIStore getAPIStore() {

        return OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment().getFHIRAPIStore();
    }

    /**
     * Get FHIR Resource API by name from the API Store
     *
     * @param name
     * @return
     */
    public static ResourceAPI getResourceAPIByName(String name) {

        return getAPIStore().getAPI(name);
    }

    /**
     * Set FHIR message context to Synapse message context
     *
     * @param fhirCtx
     * @param msgCtx
     */
    public static void setFHIRMessageContext(AbstractFHIRMessageContext fhirCtx, MessageContext msgCtx) {
        msgCtx.setProperty(Constants.OH_INTERNAL_FHIR_MESSAGE_CONTEXT, fhirCtx);
    }

    /**
     * Get FHIR message context from Synapse Message Context
     *
     * @param msgCtx
     * @return
     */
    public static AbstractFHIRMessageContext getFHIRMessageContext(MessageContext msgCtx) {
        return (AbstractFHIRMessageContext) msgCtx.getProperty(Constants.OH_INTERNAL_FHIR_MESSAGE_CONTEXT);
    }

    /**
     * Set FHIR request information to Synapse message context
     *
     * @param requestInfo
     * @param msgCtx
     */
    public static void setFHIRRequestInfo(FHIRRequestInfo requestInfo, MessageContext msgCtx) {
        msgCtx.setProperty(Constants.OH_PROP_REQUEST_INFO_OBJ, requestInfo);
    }

    /**
     * Get FHIR request information from Synapse message context
     *
     * @param msgCtx
     * @return
     */
    public static FHIRRequestInfo getFHIRRequestInfo(MessageContext msgCtx) {
        return (FHIRRequestInfo) msgCtx.getProperty(Constants.OH_PROP_REQUEST_INFO_OBJ);
    }

    /**
     * Set FHIR request in info XML form in Synapse Message context
     *
     * @param requestInfo
     * @param msgCtx
     */
    public static void setFHIRRequestInfoXML(FHIRRequestInfo requestInfo, MessageContext msgCtx) {
        msgCtx.setProperty(Constants.OH_PROP_REQUEST_INFO_XML, requestInfo.serialize());
    }

    /**
     * Get FHIR request info in XML form from Synapse Message context
     *
     * @param msgCtx
     * @return
     */
    public static OMElement getFHIRRequestInfoXML(MessageContext msgCtx) {
        return (OMElement) msgCtx.getProperty(Constants.OH_PROP_REQUEST_INFO_XML);
    }
}
