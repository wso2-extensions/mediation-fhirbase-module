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
package org.wso2.healthcare.integration.fhir.operations.basic;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.RESTConstants;
import org.w3c.dom.Document;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConnectorBase;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.utils.MiscellaneousUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class perform FHIR server connector initialization
 */
public class FHIRConnectorInit extends FHIRConnectorBase {

    private static final Log LOG = LogFactory.getLog(FHIRConnectorInit.class);
    public FHIRConnectorInit() {
    }

    @Override
    protected void execute(MessageContext messageContext,
                           FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        String baseUrl = configuredParams.get(FHIRConstants.FHIR_BASE_URL);
        if (StringUtils.isNotEmpty(baseUrl)) {
            fhirConnectorContext.setBaseUrl(baseUrl);
        }

        String incomingUrl = messageContext.getTo().getAddress();
        if (incomingUrl != null) {
            fhirConnectorContext.setIncomingRequestUrl(incomingUrl);
        }

        //Extract Accept header
        String acceptHeader = null;
        Axis2MessageContext axis2smc = (Axis2MessageContext) messageContext;
        org.apache.axis2.context.MessageContext axis2MessageCtx = axis2smc.getAxis2MessageContext();
        Object headers = axis2MessageCtx.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        if (headers instanceof Map) {
            Map headersMap = (Map) headers;
            acceptHeader = (String) headersMap.get(FHIRConstants.HTTP_HEADER_ACCEPT);
        } else {
            LOG.debug("No HTTP Accept header found");
        }
        if (acceptHeader != null) {
            fhirConnectorContext.setClientAcceptMediaType(acceptHeader);
        }
        //setting if there's a consent decision available
        FHIRConnectorUtils.setConsentDecision(messageContext, fhirConnectorContext);
    }

    @Override
    public String getOperationName() {
        return "init";
    }

    //populate URL _include and _revinclude parameters values as property
    public void getURLParameters(MessageContext messageContext) throws OpenHealthcareException {

        String fullResourceURL = (String) messageContext.getProperty(RESTConstants.REST_FULL_REQUEST_PATH);
        DocumentBuilder docBuilder;
        Document doc;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            docBuilder = dbFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new OpenHealthcareException("Error occurred while retrieving new document builder", e);
        }

        OMElement queryParamProperty;
        queryParamProperty = MiscellaneousUtils.elementToOMElement(doc.createElement("root"));
        messageContext.setProperty("queryParam",queryParamProperty);

        // TODO following logic on retrieving resource type will not work and will be rectified with
        //  https://github.com/wso2-enterprise/open-healthcare/issues/515
        String[] urlParts = fullResourceURL.split("\\?");
        String[] urlFirstParts = urlParts[0].split("/");
        String baseResource = urlFirstParts[2];

        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {

                String paramName = param.split("=")[0];
                String values = param.split("=")[1];
                String value[] ;
                String sourceResource = "";
                String searchParam = "";

                if (paramName.equals("_include") || paramName.equals("_revinclude")) {
                    value = values.split(":");
                    sourceResource = value[0];
                    if (value.length>1)
                        searchParam = value[1];
                }

                if (paramName.equals("_include")) {
                    if (sourceResource.equals(baseResource)) {
                        FHIRConstants.IS_INCLUDE_SEARCH_PARAMETER = true;
                        org.w3c.dom.Element element = doc.createElement(paramName);
                        element.setTextContent(searchParam);
                        queryParamProperty.addChild(MiscellaneousUtils.elementToOMElement(element));
                    } else {
                        throw new OpenHealthcareException("Invalid source resource name " );
                    }
                } else if (paramName.equals("_revinclude")) {
                    OMElement omProp2 = MiscellaneousUtils.elementToOMElement(doc.createElement(paramName));
                    queryParamProperty.addChild(omProp2);
                    org.w3c.dom.Element element1 = doc.createElement("sourceResource");
                    org.w3c.dom.Element element2 = doc.createElement("searchParam");
                    element1.setTextContent(sourceResource);
                    element2.setTextContent(searchParam);
                    omProp2.addChild(MiscellaneousUtils.elementToOMElement(element1));
                    omProp2.addChild(MiscellaneousUtils.elementToOMElement(element2));
                } else {
                    org.w3c.dom.Element element = doc.createElement(paramName);
                    element.setTextContent(values);
                    queryParamProperty.addChild(MiscellaneousUtils.elementToOMElement(element));
                }
            }
        }
        org.w3c.dom.Element element2 = doc.createElement("baseResourceType");
        element2.setTextContent(baseResource);
        queryParamProperty.addChild(MiscellaneousUtils.elementToOMElement(element2));
    }
}
