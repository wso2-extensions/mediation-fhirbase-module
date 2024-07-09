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

package org.wso2.healthcare.integration.common.config.model;

/**
 * Class containing FHIR server pagination configurations
 * which is immediately under [healthcare.fhir.pagination] tag
 */
public class FHIRPaginationConfig {

    int maxPageSize = 50;
    int defaultPageSize = 10;

    public int getMaxPageSize() {

        return maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {

        this.maxPageSize = maxPageSize;
    }

    public int getDefaultPageSize() {

        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {

        this.defaultPageSize = defaultPageSize;
    }
}
