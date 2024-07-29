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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.integration.common.ehr.Constants;

/**
 * The TokenManager holds and manages the tokens.
 */
public class TokenManager {
    private static final Log log = LogFactory.getLog(TokenManager.class);
    private static final TokenStore TOKEN_STORE = new InMemoryTokenStore();

    private TokenManager() {

    }

    /**
     * Function to add access token for given client ID and token endpoint.
     */
    public static void addToken(String clientId, String resourceKey, org.wso2.healthcare.integration.common.ehr.auth.Token token) {

        String tokenKey = clientId + Constants.TOKEN_KEY_SEPARATOR + resourceKey;
        TOKEN_STORE.add(tokenKey, token);
    }

    /**
     * Function to get access token for given client ID and token endpoint.
     */
    public static Token getToken(String clientId, String resourceKey) {

        String tokenKey = clientId + Constants.TOKEN_KEY_SEPARATOR + resourceKey;
        return TOKEN_STORE.get(tokenKey);
    }

    /**
     * Function to remove token from the token cache.
     */
    public static void removeToken(String clientId, String resourceKey) {

        String tokenKey = clientId + Constants.TOKEN_KEY_SEPARATOR + resourceKey;
        TOKEN_STORE.remove(tokenKey);
    }

    /**
     * Clean all access tokens from the token cache.
     */
    public static void clean() {

        TOKEN_STORE.clean();
        if (log.isDebugEnabled()) {
            log.debug("Token map cleaned.");
        }
    }
}
