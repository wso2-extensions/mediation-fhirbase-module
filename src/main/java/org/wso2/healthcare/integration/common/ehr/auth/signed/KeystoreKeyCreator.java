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

import org.apache.axiom.om.OMText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.ehr.EHRConnectException;

import javax.activation.DataHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

/**
 * KeyCreator implementation for keys stored in Java KeyStore.
 */
public class KeystoreKeyCreator implements KeyCreator {

    private static final Log log = LogFactory.getLog(KeystoreKeyCreator.class);
    private static final String REG_GOV_PREFIX = "gov:";
    private static final String REG_CONF_PREFIX = "conf:";
    private static final String REG_DEFAULT_PATH = "gov:/repository/security/key-stores/";
    private static final String FILE_PATH_PREFIX = "file:";

    private final String keystore;
    private final char[] storePass;
    private final String alias;

    public KeystoreKeyCreator(String keystore, char[] storePass, String alias) {

        this.keystore = keystore;
        this.storePass = storePass;
        this.alias = alias;
    }

    private static KeyStore loadKeyStoreFromRegistry(String keyStorePath, char[] storePass, MessageContext context)
            throws EHRConnectException {

        try {
            Object obj = context.getEntry(keyStorePath);
            if (obj instanceof OMText) {
                KeyStore keyStore = KeyStore.getInstance("JKS");
                OMText objText = (OMText) obj;
                if (objText.isBinary() && objText.getDataHandler() instanceof DataHandler) {
                    DataHandler dataHandler = (DataHandler) objText.getDataHandler();
                    keyStore.load(dataHandler.getInputStream(), storePass);
                    return keyStore;

                } else {
                    String message = "Unable to read keystore from the registry. Ensure the Media Type of the registry resource ("
                            + keyStorePath + ") is set to \"application/x-java-keystore\".";
                    log.error(message);
                    throw new EHRConnectException(message);
                }

            } else {
                String message = "Unexpected resource entry: " + keyStorePath + ".";
                log.error(message);
                throw new EHRConnectException(message);
            }

        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            String message = "Error occurred while loading Keystore from registry.";
            log.error(message, e);
            throw new EHRConnectException(e, message);
        }
    }

    @Override
    public PrivateKey getKey(MessageContext context) throws EHRConnectException {

        KeyStore keyStore;
        if (log.isDebugEnabled()) {
            log.debug("Loading private ker from :" + keystore + ".");
        }
        if (keystore.startsWith(FILE_PATH_PREFIX)) {
            keyStore = loadKeyStoreFromFile(keystore, storePass);
        } else if (keystore.startsWith(REG_CONF_PREFIX) || keystore.startsWith(REG_GOV_PREFIX)) {
            keyStore = loadKeyStoreFromRegistry(keystore, storePass, context);
        } else {
            keyStore = loadKeyStoreFromRegistry(REG_DEFAULT_PATH + keystore, storePass, context);
        }

        try {
            Key key = keyStore.getKey(alias, storePass);
            if (key instanceof PrivateKey) {
                cleanSensitiveData();
                return (PrivateKey) key;
            } else {
                String message = "The key alias:" + alias + " is not pointing a private key.";
                log.error(message);
                throw new EHRConnectException(message);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            String message = "Error occurred while retrieving private key : " + alias + " from the KeyStore: "
                    + keystore + ".";
            log.error(message, e);
            throw new EHRConnectException(e, message);
        }
    }

    private KeyStore loadKeyStoreFromFile(String keyStoreFilePath, char[] storePass) throws EHRConnectException {

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keyStoreFilePath), storePass);
            return keyStore;

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            String message = "Error occurred while loading the keystore.";
            log.error(message, e);
            throw new EHRConnectException(e, message);
        }

    }

    /**
     * The method to clear the private key data in memory.
     */
    private void cleanSensitiveData() {

        Arrays.fill(storePass, '0');
    }
}
