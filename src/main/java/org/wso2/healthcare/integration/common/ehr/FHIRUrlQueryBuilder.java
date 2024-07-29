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

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * The FHIRUrlQueryBuilder class builds the URL query for the search component.
 */
public class FHIRUrlQueryBuilder extends AbstractConnector {

    private static final String encoding = "UTF-8";
    private static final String URL_QUERY = "uri.var.urlQuery";
    private static final String ERROR_MESSAGE = "Error occurred while constructing the URL query.";
    private String parameterNames = "";

    public String getParameterNames() {

        return parameterNames;
    }

    public void setParameterNames(String parameterNames) {

        this.parameterNames = parameterNames;
    }

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        Map<String, String> paramNameMap = (Map<String, String>) messageContext.getProperty("paramNameMap");

        String[] parameterList = getParameterNames().split(",");
        StringBuilder urlQueryBuilder = new StringBuilder();

        for (String paramName : parameterList) {
            String paramValue = (String) getParameter(messageContext, paramName);
            if (StringUtils.isNotEmpty(paramValue)) {
                if (paramName.endsWith("UpperBound")) {
                    if (Character.isDigit(paramValue.charAt(0))) {
                        paramValue = "le" + paramValue;
                    }
                }
                if (paramName.endsWith("LowerBound")) {
                    if (Character.isDigit(paramValue.charAt(0))) {
                        paramValue = "ge" + paramValue;
                    }
                }
                try {
                    if (paramName.equals("additionalSearchParameters")) {
                        String[] paramAndValues = paramValue.split("&");
                        for (String s : paramAndValues) {
                            String[] inner = s.split("=");
                            if (inner.length < 2 ) {
                                String msg = "Invalid additional search parameters: " + paramValue;
                                org.wso2.healthcare.integration.common.ehr.EHRConnectException exp = new EHRConnectException(msg);
                                org.wso2.healthcare.integration.common.ehr.Utils.setErrorResponse(messageContext, exp, org.wso2.healthcare.integration.common.ehr.Constants.BAD_REQUEST_ERROR_CODE, msg);
                                throw exp;
                            } else {
                                paramValue = paramValue.replace(inner[1], URLEncoder.encode(inner[1], encoding));
                            }
                        }
                        urlQueryBuilder.append(paramValue).append('&');
                    } else {
                        String encodedParamValue = URLEncoder.encode(paramValue, encoding);
                        String name = paramNameMap.get(paramName);
                        urlQueryBuilder.append(name).append('=').append(encodedParamValue).append('&');
                    }
                } catch (UnsupportedEncodingException e) {
                    Utils.handleError(messageContext, e, Constants.INTERNAL_SERVER_ERROR_CODE, "Error occured while " +
                            "encoding the query parameters");
                }
            }
        }

        String urlQuery = urlQueryBuilder.toString();
        if (StringUtils.isNotEmpty(urlQuery)) {
            urlQuery = "?" + urlQuery.substring(0, urlQuery.lastIndexOf("&"));
        }

        messageContext.setProperty(URL_QUERY, urlQuery);

    }
}
