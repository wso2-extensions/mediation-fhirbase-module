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

package org.wso2.healthcare.integration.common.test.utils;

import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2SynapseEnvironment;
import org.apache.synapse.rest.RESTConstants;
import org.wso2.healthcare.integration.common.test.TestMessageContext;

/**
 * Synapse related test utilities
 */
public class SynapseTestUtils {

    /**
     * Function to create synapse environment
     *
     * @return
     */
    public static SynapseEnvironment createSynapseEnvironment() {
        SynapseConfiguration synCfg = new SynapseConfiguration();
        return new Axis2SynapseEnvironment(synCfg);
    }

    /**
     * Create Message Context for API invocation
     * This will populate properties populated during an API call
     *
     * @param httpMethod
     * @param apiContext
     * @param resourceContext
     * @return
     */
    public static TestMessageContext createAPIRequestMessageContext(String httpMethod, String apiContext,
                                                                    String resourceContext, String queryParam,
                                                                    String restUrlPattern) {
        TestMessageContext messageContext = new TestMessageContext();
        messageContext.setProperty(RESTConstants.REST_METHOD, httpMethod);
        messageContext.setProperty(RESTConstants.REST_API_CONTEXT, apiContext);
        messageContext.setProperty(RESTConstants.REST_SUB_REQUEST_PATH, resourceContext);
        String fullRequestPath = apiContext + resourceContext;
        if (queryParam != null) {
            fullRequestPath = fullRequestPath + "?" + queryParam;
        }
        messageContext.setProperty(RESTConstants.REST_FULL_REQUEST_PATH, fullRequestPath);
        messageContext.setProperty(RESTConstants.REST_URL_PATTERN, restUrlPattern);

        return messageContext;
    }
}
