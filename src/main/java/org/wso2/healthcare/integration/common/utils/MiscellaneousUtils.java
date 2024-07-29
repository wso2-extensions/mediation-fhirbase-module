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

package org.wso2.healthcare.integration.common.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.util.XMLUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.commons.json.JsonUtil;
import org.w3c.dom.Element;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareException;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This utility class contains miscellaneous utilities
 */
public class MiscellaneousUtils {

    /**
     * Utility to create OMElement from Element
     *
     * @param element
     * @return
     * @throws OpenHealthcareException
     */
    public static OMElement elementToOMElement(Element element) throws OpenHealthcareException {
        try {
            return XMLUtils.toOM(element);
        } catch (Exception exception) {
            throw new OpenHealthcareException("Error occurred while creating OMElement from Element : " + element, exception);
        }
    }

    /**
     * Utility function to build query string
     *
     * @param queryParams
     * @return
     */
    public static String buildQueryString(ArrayList<org.wso2.healthcare.integration.common.utils.Pair<String, String>> queryParams) {
        StringBuilder queryStrBuilder = new StringBuilder();
        boolean addAmp = false;
        for (Pair<String, String> queryKeyValue : queryParams) {
            if (addAmp) {
                queryStrBuilder.append('&');
            } else {
                addAmp = true;
            }
            queryStrBuilder.append(queryKeyValue.getKey()).append('=').append(queryKeyValue.getValue());
        }
        return queryStrBuilder.toString();
    }

    /**
     * Utility function to replace a string in a json payload
     * @param axis2mc Message context that holds the json payload
     * @param source String to be replaced
     * @param target String to replace with
     * @throws AxisFault
     */
    public static void rewriteInMsgCtx(org.apache.axis2.context.MessageContext axis2mc, String source, String target)
            throws AxisFault {

        if (JsonUtil.hasAJsonPayload(axis2mc)) {
            String responsePayload =
                    JsonUtil.jsonPayloadToString(axis2mc);
            String newPayLoad = responsePayload.replaceAll("(?i)" + Pattern.quote(source), target);
            JsonUtil.removeJsonPayload(axis2mc);
            JsonUtil.getNewJsonPayload(axis2mc, newPayLoad, true, true);
        }
    }

    /**
     * Utility function that populates message context with error details and throws synapse exception
     * @param messageContext
     * @param severity
     * @param code
     * @param detail
     * @param display
     * @param diagnostic
     */
    public static void populateAndThrowSynapseException(MessageContext messageContext, String severity, String code,
                                                        String detail, String display, String diagnostic) {

        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_SEVERITY, severity);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_CODE, code);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_CODE, detail);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_SYSTEM, HealthcareUtils.generateCodeSystem("operation-outcome"));
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_DISPLAY, display);
        messageContext.setProperty(Constants.OH_PROP_FHIR_ERROR_DIAGNOSTICS, diagnostic);
        throw new SynapseException((String) messageContext.getProperty(Constants.OH_PROP_FHIR_ERROR_DETAIL_DISPLAY));

    }
}
