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
package org.wso2.healthcare.integration.common.ehr;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.healthcare.integration.common.ehr.auth.TokenManager;

import static org.wso2.healthcare.integration.common.ehr.Constants.*;
import static org.wso2.healthcare.integration.common.utils.MiscellaneousUtils.rewriteInMsgCtx;

/**
 * The ResponseProcessingHandler class validates the response status code
 * and cleans the relevant token from the map.
 */
public class ResponseProcessingHandler extends AbstractConnector {

    private static final Log log = LogFactory.getLog(ResponseProcessingHandler.class);

    public ResponseProcessingHandler() {

    }

    @Override
    public void connect(MessageContext messageContext) throws EHRConnectException {

        if (messageContext instanceof Axis2MessageContext) {
            org.apache.axis2.context.MessageContext axis2mc = ((Axis2MessageContext) messageContext)
                    .getAxis2MessageContext();
            int httpStatus = (Integer) axis2mc.getProperty("HTTP_SC");
            String clientId = (String) messageContext.getProperty(org.wso2.healthcare.integration.common.ehr.Constants.PROPERTY_CLIENT_ID);
            String tokenEndpoint = (String) messageContext.getProperty(org.wso2.healthcare.integration.common.ehr.Constants.PROPERTY_TOKEN_ENDPOINT);
            if ((clientId != null || tokenEndpoint != null) && httpStatus >= 400) {
                if (log.isDebugEnabled()) {
                    log.debug("Unauthorized response received, hence removing access token from the token map.");
                }
                // Remove the access token from the token map.
                TokenManager.removeToken(clientId, tokenEndpoint);
            }
            if (isUrlRewriteEnabled(messageContext)) {
                String serverUrl = getServerUrl(messageContext);
                if (StringUtils.isEmpty(serverUrl)) {
                    EHRConnectException exp = new EHRConnectException("Server URL for rewriting not defined.");
                    Utils.setErrorResponse(messageContext, exp, org.wso2.healthcare.integration.common.ehr.Constants.BAD_REQUEST_ERROR_CODE, exp.getMessage());
                    throw exp;
                } else {
                    String baseUrl = getBaseUrl(messageContext);
                    try {
                        rewriteInMsgCtx(axis2mc, baseUrl, serverUrl);
                    } catch (AxisFault e) {
                        Utils.handleError(messageContext, e, Constants.INTERNAL_SERVER_ERROR_CODE,
                                "Error occurred while rewriting server URL.");
                    }
                }
            }
        }
    }

    private boolean isUrlRewriteEnabled(MessageContext messageContext) {

        boolean enabled = false;
        if (getParameter(messageContext, ENABLE_URL_REWRITE) != null) {
            enabled = Boolean.valueOf((String) getParameter(messageContext, ENABLE_URL_REWRITE));
        } else if (messageContext.getProperty(OH_INTERNAL_URL_REWRITE_) != null) {
            enabled =  (boolean) messageContext.getProperty(OH_INTERNAL_URL_REWRITE_);
        }
        return enabled;
    }

    private String getBaseUrl(MessageContext messageContext) {

        String baseUrl;
        if (getParameter(messageContext, BASE_URL) != null) {
            baseUrl = (String) getParameter(messageContext, BASE_URL);
        } else if (messageContext.getProperty(OH_INTERNAL_OPTIONAL_BASE_URL) != null) {
            baseUrl = (String) messageContext.getProperty(OH_INTERNAL_OPTIONAL_BASE_URL);
        } else {
            baseUrl = (String) messageContext.getProperty(URI_VAR_BASE);
        }
        return baseUrl;
    }

    private String getServerUrl(MessageContext messageContext) {

        String serverUrl = "";
        if (getParameter(messageContext, SERVER_URL) != null) {
            serverUrl = (String) getParameter(messageContext, SERVER_URL);
        } else if (messageContext.getProperty(OH_INTERNAL_SERVER_URL_) != null) {
            serverUrl = (String) messageContext.getProperty(OH_INTERNAL_SERVER_URL_);
        }
        return serverUrl;
    }
}
