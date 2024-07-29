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

/**
 * Implementation of the bundle operation
 */
package org.wso2.healthcare.integration.fhir.operations.basic;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Reference;
import org.w3c.dom.Document;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRQueryOperation;
import org.wso2.healthcare.integration.fhir.core.Query;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of evaluateFHIRPath operation
 */
public class FHIREvaluateFHIRPath extends FHIRQueryOperation {

    private static Log LOG = LogFactory.getLog(FHIREvaluateFHIRPath.class);
    @Override
    public String getOperationName() {
        return "evaluateFHIRPath";
    }

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext context,
                           HashMap<String, String> configuredParams, Query query) throws FHIRConnectException {
        String property = configuredParams.get(FHIRConstants.FHIR_PARAM_TARGET_PROPERTY);
        if (property == null || property.isEmpty()) {
            throw new FHIRConnectException(FHIRConstants.FHIR_PARAM_TARGET_PROPERTY +
                    " is a mandatory parameter for a Query operation");
        }

        List<Base> result = query.evaluate();
        if (result.size() == 1) {
            Base resultData = result.get(0);
            if (resultData instanceof PrimitiveType) {
                messageContext.setProperty(property, resultData.primitiveValue());
            }
        } else if (result.size()>1) {

            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                OMElement omProp = XMLUtils.toOM(doc.createElement("root"));
                messageContext.setProperty(property, omProp);

                Set<String> set = new HashSet<String>();
                String referenceName="";

                for (Base base : result) {
                    if (base instanceof Reference) {
                        if (FHIRConstants.IS_INCLUDE_SEARCH_PARAMETER) {
                            String reference = ((Reference) base).getReference();
                            referenceName = reference.split("/")[0];
                            String id = reference.split("/")[1];
                            set.add(id);
                        } else {
                            org.w3c.dom.Element element = doc.createElement("value");
                            element.setTextContent(((Reference) base).getReference());
                            omProp.addChild(XMLUtils.toOM(element));
                        }
                    } else if (base instanceof PrimitiveType) {
                        org.w3c.dom.Element element = doc.createElement("value");
                        element.setTextContent(base.primitiveValue());
                        omProp.addChild(XMLUtils.toOM(element));
                    }
                }
                for (String list : set) {
                    org.w3c.dom.Element element = doc.createElement("value");
                    element.setTextContent(list);
                    omProp.addChild(XMLUtils.toOM(element));
                }
                org.w3c.dom.Element element1 = doc.createElement("referenceResource");
                element1.setTextContent(referenceName);
                omProp.addChild(XMLUtils.toOM(element1));
            } catch (Exception e) {
                throw new FHIRConnectException("Error in constructing XML Object"+ e );
            }

        }
    }
}
