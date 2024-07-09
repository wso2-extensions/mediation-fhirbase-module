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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory Token Store.
 */
public class InMemoryTokenStore implements TokenStore {

    private final Map<String, Token> TOKEN_MAP = new ConcurrentHashMap<>(2);

    @Override
    public Token get(String tokenKey) {

        return TOKEN_MAP.get(tokenKey);
    }

    @Override
    public void add(String tokenKey, Token token) {

        TOKEN_MAP.put(tokenKey, token);
    }

    @Override
    public Token remove(String tokenKey) {

        return TOKEN_MAP.remove(tokenKey);
    }

    @Override
    public void clean() {

        TOKEN_MAP.clear();
    }
}
