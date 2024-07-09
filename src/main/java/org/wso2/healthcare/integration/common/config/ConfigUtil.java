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

package org.wso2.healthcare.integration.common.config;

import org.wso2.healthcare.integration.common.Constants;

import java.io.File;

/**
 * Contains utilities related to Configuration
 */
public class ConfigUtil {

    /**
     * Utility to retrieve carbon home
     *
     * @return path to carbon home
     */
    public static String getCarbonHome() {
        String carbonHome = System.getProperty(Constants.CARBON_HOME);
        if (carbonHome == null) {
            carbonHome = System.getenv(Constants.CARBON_HOME_ENV);
            System.setProperty(Constants.CARBON_HOME, carbonHome);
        }
        if (!(carbonHome.charAt(carbonHome.length() - 1) == File.separatorChar)) {
            carbonHome = carbonHome + File.separatorChar;
        }
        return carbonHome;
    }

    /**
     * Utility function to retrieve config directory path
     *
     * @return configuration root directory of the server
     */
    public static String getCarbonConfigDirPath() {

        String carbonConfigDirPath = System.getProperty(Constants.CARBON_CONFIG_DIR_PATH);
        if (carbonConfigDirPath == null) {
            carbonConfigDirPath = System.getenv(Constants.CARBON_CONFIG_DIR_PATH_ENV);
            if (carbonConfigDirPath == null) {
                return getCarbonHome() + File.separator + "conf";
            }
        }
        return carbonConfigDirPath;
    }

    /**
     * Utility function to retrieve absolute config file path
     *
     * @param configFileName file name
     * @return get file path of the given configuration file
     */
    public static String getConfigFilePath(String configFileName) {
        String configDir = getCarbonConfigDirPath();
        if (!(configDir.charAt(configDir.length() - 1) == File.separatorChar)) {
            configDir = configDir + File.separatorChar;
        }
        return configDir + configFileName;
    }
}
