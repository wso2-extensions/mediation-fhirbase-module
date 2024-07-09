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
package org.wso2.healthcare.integration.common.ehr.auth.signed;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
import org.wso2.healthcare.integration.common.ehr.auth.Token;
import org.wso2.healthcare.integration.common.ehr.auth.TokenManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Class responsible for obtaining access token via the client-credentials grant type
 * with JWT authentication.
 */
public class ClientCredentialsWithJWTAccessTokenHandler extends AbstractConnector {
    private static final Log log = LogFactory.getLog(ClientCredentialsWithJWTAccessTokenHandler.class);
    private static final JsonParser parser = new JsonParser();

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
                String clientId = (String) messageContext.getProperty(Constants.PROPERTY_CLIENT_ID);
                String tokenEndpoint = (String) messageContext.getProperty(Constants.PROPERTY_TOKEN_ENDPOINT);
                String privateKey = (String) getParameter(messageContext, Constants.PRIVATE_KEY);
                String privateKeystore = (String) getParameter(messageContext, Constants.KEY_STORE);
                String keyAlias = (String) getParameter(messageContext, Constants.PRIVATE_KEY_ALIAS);
                String keyStorePass = (String) getParameter(messageContext, Constants.KEY_STORE_PASS);

                validateParameters(messageContext, clientId, tokenEndpoint, privateKey, privateKeystore, keyAlias,
                        keyStorePass);

                if (log.isDebugEnabled()) {
                    log.debug("Retrieving access token from TokenManager.");
                }

                Token token = TokenManager.getToken(clientId, tokenEndpoint);
                if (token == null || !token.isActive()) {
                    KeyCreator privateKeyCreator;

                    if (privateKey != null) {
                        privateKeyCreator = new PlaintextKeyCreator(privateKey);
                    } else {
                        privateKeyCreator = new KeystoreKeyCreator(privateKeystore, keyStorePass.toCharArray(),
                                keyAlias);
                    }
                    token = getAndAddNewToken(clientId, privateKeyCreator, tokenEndpoint, messageContext);
                }
                accessToken = token.getAccessToken();
            }
            messageContext.setProperty(Constants.PROPERTY_ACCESS_TOKEN, accessToken);
        }
    }

    private void validateParameters(MessageContext messageContext, String clientId, String tokenEndpoint,
                                    String privateKey, String privateKeystore, String keyAlias, String keyStorePass)
            throws EHRConnectException {

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(tokenEndpoint)
                || (StringUtils.isEmpty(privateKey) && (StringUtils.isEmpty(privateKeystore)
                || StringUtils.isEmpty(keyAlias) || StringUtils.isEmpty(keyStorePass)))) {

            StringBuilder errMessageBuilder = new StringBuilder(
                    "Following parameters are missing in init operation : ");
            if (clientId == null) {
                errMessageBuilder.append(" \"clientId\",");
            }
            if (tokenEndpoint == null) {
                errMessageBuilder.append(" \"tokenEndpoint\",");
            }

            if (privateKeystore != null || keyAlias != null) {
                if (privateKeystore == null) {
                    errMessageBuilder.append(" \"keyStore\",");
                }
                if (keyAlias == null) {
                    errMessageBuilder.append(" \"privateKeyAlias\",");
                }
                if (keyStorePass == null) {
                    errMessageBuilder.append(" \"keyStorePass\",");
                }
            } else if (privateKey == null) {
                errMessageBuilder.append(" \"privateKey\",");
            }

            String errorMsg = errMessageBuilder.toString();
            EHRConnectException exp = new EHRConnectException(errorMsg);
            Utils.setErrorResponse(messageContext, exp, Constants.BAD_REQUEST_ERROR_CODE, errorMsg);
            throw exp;
        }
    }

    /**
     * Function to get new token.
     */
    private synchronized Token getAndAddNewToken(String clientId, KeyCreator keyCreator, String tokenEndpoint,
                                                       MessageContext messageContext) throws EHRConnectException {

        Token token = TokenManager.getToken(clientId, tokenEndpoint);
        if (token == null || !token.isActive()) {
            String jwt = generateJWT(clientId, keyCreator, tokenEndpoint, messageContext);
            token = getAccessToken(messageContext, tokenEndpoint, jwt);
            TokenManager.addToken(clientId, tokenEndpoint, token);
        }
        return token;
    }

    /**
     * Function to generate JWT.
     */
    private String generateJWT(String clientId, KeyCreator keyCreator, String tokenEndpoint,
                                      MessageContext messageContext) throws EHRConnectException {

        try {
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyCreator.getKey(messageContext);
            JWSSigner signer = new RSASSASigner(rsaPrivateKey);
            long curTimeInMillis = System.currentTimeMillis();

            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
            claimsSetBuilder.issuer(clientId);
            claimsSetBuilder.subject(clientId);
            claimsSetBuilder.audience(tokenEndpoint);
            claimsSetBuilder.jwtID(UUID.randomUUID().toString());
            claimsSetBuilder.issueTime((new Date(curTimeInMillis)));
            claimsSetBuilder.notBeforeTime((new Date(curTimeInMillis)));
            claimsSetBuilder.expirationTime(new Date(curTimeInMillis + 300000)); // Maximum expiration time is 5 min.
            JWTClaimsSet claimsSet = claimsSetBuilder.build();

            JWSAlgorithm signatureAlgorithm = new JWSAlgorithm(JWSAlgorithm.RS384.getName());
            JWSHeader.Builder headerBuilder = new JWSHeader.Builder(signatureAlgorithm);
            headerBuilder.type(JOSEObjectType.JWT);
            JWSHeader jwsHeader = headerBuilder.build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (JOSEException e) {
            String message = "Error occurred while signing the JWT.";
            log.error(message, e);
            throw new EHRConnectException(e, message);
        }
    }

    /**
     * Function to retrieve new client credential access token from the token endpoint.
     */
    private static Token getAccessToken(MessageContext messageContext, String tokenEndpoint, String jwt) throws EHRConnectException {

        long curTimeInMillis = System.currentTimeMillis();
        HttpPost postRequest = new HttpPost(tokenEndpoint);
        ArrayList<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        parameters.add(new BasicNameValuePair("client_assertion_type",
                "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        parameters.add(new BasicNameValuePair("client_assertion", jwt));

        try {
            postRequest.setEntity(new UrlEncodedFormEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            String message = "Error occurred while preparing access token request payload.";
            log.error(message);
            throw new EHRConnectException(e, message);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(postRequest)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                String message = "Failed to retrieve access token : No entity received.";
                log.error(message);
                throw new EHRConnectException(message);
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
                String message = "Error occurred while retrieving access token. Response: [Status : " + responseStatus
                        + " Message: " + respMessage + "]";
                log.error(message);
                EHRConnectException exp = new EHRConnectException(message);
                Utils.setErrorResponse(messageContext, exp, responseStatus, message);
                throw exp;
            }

        } catch (IOException e) {
            String message = "Error occurred while retrieving access token.";
            log.error(message, e);
            throw new EHRConnectException(e, message);
        }
    }
}
