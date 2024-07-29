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

public class Constants {

    public static final String URI_VAR_BASE = "uri.var.base";
    public static final String ENABLE_URL_REWRITE = "enableUrlRewrite";
    public static final String SERVER_URL = "serverUrl";
    public static final String BASE_URL = "baseUrl";
    public static final String OH_INTERNAL_URL_REWRITE_ = "_OH_INTERNAL_URL_REWRITE_";
    public static final String OH_INTERNAL_OPTIONAL_BASE_URL = "_OH_INTERNAL_OPTIONAL_BASE_URL_";
    public static final String OH_INTERNAL_SERVER_URL_ = "_OH_INTERNAL_SERVER_URL_";

    public static final String TOKEN_KEY_SEPARATOR = "_";

    public static final String BASE = "base";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String TOKEN_ENDPOINT = "tokenEndpoint";
    public static final String SCOPE = "scope";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String KEY_STORE = "keyStore";
    public static final String PRIVATE_KEY_ALIAS = "privateKeyAlias";
    public static final String KEY_STORE_PASS = "keyStorePass";

    public static final String PROPERTY_BASE = "uri.var.base";
    public static final String PROPERTY_ACCESS_TOKEN = "_OH_INTERNAL_ACCESS_TOKEN_";
    public static final String PROPERTY_CLIENT_ID = "_OH_INTERNAL_CLIENT_ID_";
    public static final String PROPERTY_CLIENT_SECRET = "_OH_INTERNAL_CLIENT_SECRET_";
    public static final String PROPERTY_TOKEN_ENDPOINT = "_OH_INTERNAL_TOKEN_ENDPOINT_";
    public static final String PROPERTY_OAUTH_RESOURCE = "_OH_INTERNAL_OAUTH_RESOURCE_";
    public static final String PROPERTY_REPOSITORY_TYPE = "_OH_INTERNAL_REPOSITORY_TYPE_";

    public static final int BAD_REQUEST_ERROR_CODE = 400;
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String HTTP_SC = "HTTP_SC";

    public static final String ACCESS_TOKEN_REQUEST_PAYLOAD_ERROR =
            "Error occurred while preparing access token request payload";
    public static final String RESOURCE = "resource";
}
