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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.ehr.EHRConnectException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * KeyCreator implementation for plaintext keys.
 */
public class PlaintextKeyCreator implements KeyCreator {

    private static final Log log = LogFactory.getLog(PlaintextKeyCreator.class);
    private final byte[] keyString;

    public PlaintextKeyCreator(String keyString) {

        this.keyString = keyString.getBytes();
    }

    @Override
    public PrivateKey getKey(MessageContext context) throws EHRConnectException {

        try {
            byte[] encoded = Base64.decodeBase64(keyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            cleanSensitiveData();
            return privateKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            String message = "Error occurred while retrieving private key object.";
            log.error(message, e);
            throw new EHRConnectException(e, message);
        }
    }

    /**
     * The method to clear the private key data in memory.
     */
    private void cleanSensitiveData() {

        Arrays.fill(keyString, (byte) 0);
    }
}
