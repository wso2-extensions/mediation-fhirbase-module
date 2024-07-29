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

package org.wso2.healthcare.integration.common.fhir.server.model;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.utils.SynapseUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represent HTTP request information
 */
public class HTTPInfo extends AbstractSerializableInfo {

    private final ArrayList<QueryParamInfo> queryParams = new ArrayList<>();

    @Override
    protected String getRootElementName() {
        return "http";
    }

    @Override
    public OMElement serialize() {
        OMElement queryParamsElement = getOMFactory().createOMElement(new QName("query_params"));
        for (QueryParamInfo param : this.queryParams) {
            queryParamsElement.addChild(param.serialize());
        }

        OMElement httpElement = getRootOMElement();
        httpElement.addChild(queryParamsElement);

        return httpElement;
    }

    public void addQueryParam(QueryParamInfo queryParam) {
        this.queryParams.add(queryParam);
    }

    public Iterator<QueryParamInfo> getQueryParameters() {
        return this.queryParams.iterator();
    }

    /**
     * Function to retrieve query param with matching name
     *
     * @param name
     * @return
     */
    public QueryParamInfo findQueryParam(String name) {
        for (QueryParamInfo param : this.queryParams) {
            if (param.getName().equals(name)) return param;
        }
        return null;
    }

    /**
     * Function to retrieve multiple matching query params
     *
     * @param name
     * @return
     */
    public ArrayList<QueryParamInfo> findQueryParams(String name) {
        ArrayList<QueryParamInfo> result = new ArrayList<>();
        for (QueryParamInfo param : this.queryParams) {
            if (param.getName().equals(name)) result.add(param);
        }
        return result;
    }

    public boolean isQueryParamPresent(String name) {
        return findQueryParam(name) != null;
    }

    public static HTTPInfo build(MessageContext msgCtx) throws OpenHealthcareException {
        HTTPInfo httpInfo = new HTTPInfo();
        httpInfo.populateQueryParams(msgCtx);
        return httpInfo;
    }

    private void populateQueryParams(MessageContext msgCtx) throws OpenHealthcareException {
        String fullResourceURL = SynapseUtils.getRestFullRequestPath(msgCtx);
        String[] urlParts = fullResourceURL.split("\\?");

        if (urlParts.length > 1) {
            String queryParams = urlParts[1];
            for (String param : queryParams.split("&")) {
                String[] paramParts = param.split("=");
                if (paramParts.length != 2) {
                    throw new OpenHealthcareException("Malformed query parameter : " + param);
                }
                addQueryParam(new QueryParamInfo(paramParts[0], paramParts[1]));
            }
        }
    }
}
