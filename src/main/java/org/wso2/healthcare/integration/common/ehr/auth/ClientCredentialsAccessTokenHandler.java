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
package org.wso2.healthcare.integration.common.ehr.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.healthcare.integration.common.ehr.Constants;
import org.wso2.healthcare.integration.common.ehr.EHRConnectException;
import org.wso2.healthcare.integration.common.ehr.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for obtaining access token via the client-credentials grant type
 * with basic authentication.
 */
public class ClientCredentialsAccessTokenHandler extends AbstractConnector {
    private static final Log log = LogFactory.getLog(ClientCredentialsAccessTokenHandler.class);
    private static final JsonParser parser = new JsonParser();
    private static final String ERROR_MESSAGE = "\"clientId\", \"clientSecret\", \"tokenEndpoint\" parameters or \"accessToken\" parameter must present.";

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        String base = (String) getParameter(messageContext, Constants.BASE);
        if (StringUtils.endsWith(base, "/")) {
            base = StringUtils.removeEnd(base, "/");
        }
        messageContext.setProperty(Constants.PROPERTY_BASE, base);

        String propertyAccessToken = (String) messageContext.getProperty(Constants.PROPERTY_ACCESS_TOKEN);
        if (StringUtils.isEmpty(propertyAccessToken)) {
            // If the access token is not available in the message context, retrieve from the token endpoint.
            String accessToken = (String) getParameter(messageContext, Constants.ACCESS_TOKEN);
            if (StringUtils.isEmpty(accessToken)) {
                String clientId = (String) getParameter(messageContext, Constants.CLIENT_ID);
                String clientSecret = (String) getParameter(messageContext, Constants.CLIENT_SECRET);
                String tokenEndpoint = (String) getParameter(messageContext, Constants.TOKEN_ENDPOINT);
                String scope = (String) getParameter(messageContext, Constants.SCOPE);

                if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret)
                        || StringUtils.isEmpty(tokenEndpoint)) {
                    EHRConnectException exp = new EHRConnectException(ERROR_MESSAGE);
                    Utils.setErrorResponse(messageContext, exp, Constants.BAD_REQUEST_ERROR_CODE, ERROR_MESSAGE);
                    throw exp;
                }

                // to keep any other parameters need for the token generation
                Map<String, String> payloadParametersMap = new HashMap<>();
                payloadParametersMap.put("scope", scope);

                Token token = TokenManager.getToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
                if (token == null || !token.isActive()) {
                    if (token != null && !token.isActive()) {
                        TokenManager.removeToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
                    }
                    if (log.isDebugEnabled()) {
                        if (token == null) {
                            log.debug("Token does not exists in token store.");
                        } else {
                            log.debug("Access token is inactive.");
                        }
                    }
                    token = getAndAddNewToken(messageContext, clientId, clientSecret.toCharArray(), payloadParametersMap,
                            tokenEndpoint);
                }
                accessToken = token.getAccessToken();
            }
            messageContext.setProperty(Constants.PROPERTY_ACCESS_TOKEN, accessToken);
        }
    }

    /**
     * Function to get new token.
     */
    protected synchronized Token getAndAddNewToken(MessageContext messageContext, String clientId, char[] clientSecret,
                                                   Map<String, String> payloadParametersMap, String tokenEndpoint) throws EHRConnectException {

        Token token = TokenManager.getToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
        if (token == null || !token.isActive()) {
            token = getAccessToken(messageContext, clientId, clientSecret, payloadParametersMap, tokenEndpoint);
            TokenManager.addToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap), token);
        }
        return token;
    }

    /**
     * Function to retrieve new client credential access token from the token endpoint.
     */
    protected Token getAccessToken(MessageContext messageContext, String clientId, char[] clientSecret, Map<String, String> payloadParametersMap,
                                 String tokenEndpoint) throws EHRConnectException {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving new system access token from token endpoint.");
        }

        long curTimeInMillis = System.currentTimeMillis();
        HttpPost postRequest = new HttpPost(tokenEndpoint);

        String authHeader = new String(new Base64().encode((clientId + ':' + String.valueOf(clientSecret)).getBytes()));
        postRequest.addHeader("Authorization", "Basic " + authHeader);

        ArrayList<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("grant_type", "client_credentials"));

        for (Map.Entry<String, String> entry : payloadParametersMap.entrySet()) {
            parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        try {
            postRequest.setEntity(new UrlEncodedFormEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            String errorMessage = "Error occurred while preparing access token request payload.";
            log.error(errorMessage, e);
            throw new EHRConnectException(e, errorMessage);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(postRequest)) {
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity == null) {
                String errorMessage = "Failed to retrieve access token : No entity received.";
                log.error(errorMessage);
                throw new EHRConnectException(errorMessage);
            }

            int responseStatus = response.getStatusLine().getStatusCode();
            String respMessage = EntityUtils.toString(responseEntity);
            if (responseStatus == HttpURLConnection.HTTP_OK) {
                JsonElement jsonElement = parser.parse(respMessage);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String accessToken = jsonObject.get("access_token").getAsString();
                long expireIn = jsonObject.get("expires_in").getAsLong();

                Token token = new Token(accessToken, curTimeInMillis, expireIn * 1000);
                if (log.isDebugEnabled()) {
                    log.debug(token);
                }
                return token;

            } else {
                String errorMessage = "Error occurred while retrieving access token. Response: " + "[Status : "
                        + responseStatus + " " + "Message: " + respMessage + "]";
                log.error(errorMessage);
                EHRConnectException exp = new EHRConnectException(errorMessage);
                Utils.setErrorResponse(messageContext, exp, responseStatus, errorMessage);
                throw exp;

            }
        } catch (IOException e) {
            String errorMessage = "Error occurred while retrieving access token.";
            log.error(errorMessage, e);
            throw new EHRConnectException(e, errorMessage);
        }
    }

    protected String getTokenKey(String tokenEp, Map<String, String> params) {

        StringBuilder sb = new StringBuilder(tokenEp);
        for (String val : params.values()) {
            sb.append("_").append(val);
        }
        return sb.toString();
    }
}
