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
package org.wso2.healthcare.integration.fhir.utils;

import ca.uhn.fhir.context.FhirContext;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ContinuationState;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.continuation.SeqContinuationState;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.template.TemplateContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorConfig;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public class FHIRConnectorUtils {
    private static final Log log = LogFactory.getLog(FHIRConnectorUtils.class);

    public static FHIRConnectorContext getFHIRConnectorContext(MessageContext messageContext) {
        Object fhirContextObj = messageContext.getProperty(FHIRConstants.FHIR_CONTEXT);
        if (fhirContextObj != null) {
            return (FHIRConnectorContext) fhirContextObj;
        } else {
            FHIRConnectorContext fhirContext = new FHIRConnectorContext();
            messageContext.setProperty(FHIRConstants.FHIR_CONTEXT, fhirContext);
            return fhirContext;
        }
    }

    public static void setConsentDecision(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext) {
        if (fhirConnectorContext.getConsentDecision() == null) {
            //getting Transport Headers
            Axis2MessageContext axis2mc = (Axis2MessageContext) messageContext;
            org.apache.axis2.context.MessageContext axis2MessageCtx = axis2mc.getAxis2MessageContext();
            Object transportHeaders = axis2MessageCtx
                    .getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
            if (transportHeaders instanceof Map) {
                Map headersMap = (Map) transportHeaders;
                // checking for consent decision header
                if (headersMap.containsKey("X-WSO2-ConsentDecision")) {
                    String decision = (String) headersMap.get("X-WSO2-ConsentDecision");
                    fhirConnectorContext.setConsentDecision(decision);
                }
            }
        }
    }

    public static void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

    public static void handleException(String msg, Throwable throwable) {
        log.error(msg, throwable);
        throw new SynapseException(msg, throwable);
    }

    /**
     * Utility function to retrieve all configured parameters in connector runtime configuration
     *
     * @param ctxt
     * @return
     */
    public static HashMap<String, String> getConfiguredParams(MessageContext ctxt) {
        Stack<TemplateContext> funcStack = (Stack<TemplateContext>) ctxt.getProperty(SynapseConstants.SYNAPSE__FUNCTION__STACK);
        TemplateContext currentFuncHolder = funcStack.peek();
        return (HashMap<String, String>) currentFuncHolder.getMappedValues();
    }

    /**
     * Function to retrieve parameter map from the message context for specified operation
     *
     * @param operationName
     * @param msgCtx
     * @return
     */
    public static HashMap<String, String> getOperationParamMapFromMessage(String operationName, MessageContext msgCtx) {

        LinkedHashMap<String, String> opPropertyMap =
                FHIRConnectorConfig.getInstance().getOpPropMap().get(operationName);
        HashMap<String, String> parameterMap = new HashMap<>();
        Set<Map.Entry<String, String>> entrySet = opPropertyMap.entrySet();

        String previousKey = null;
        boolean previousResultSuccess = true;
        for (Map.Entry<String, String> entry : entrySet) {
            if (previousKey == null) previousKey = entry.getKey();
            if (previousResultSuccess || !(entry.getKey().startsWith(previousKey))) {

                ArrayList<OMElement> result = FHIRExpressionUtils.evaluateXPath(entry.getValue(), msgCtx);
                if (result != null && result.size() > 0) {
                    previousResultSuccess = true;
                    if (result.size() == 1) {
                        String resultTxt = result.get(0).getText();
                        if (!resultTxt.isEmpty()) {
                            parameterMap.put(entry.getKey(), resultTxt);
                        }
                    } else {
                        // TODO:handle for multiple elements
                    }
                } else {
                    previousResultSuccess = false;
                }
                previousKey = entry.getKey();
            }
        }
        return parameterMap;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String serializeToJSON(IBaseResource resource) {
        FhirContext fhirContext = FHIRConnectorConfig.getInstance().getFhirContext();
        return fhirContext.newJsonParser().encodeResourceToString(resource);
    }

    public static String serializeToXML(IBaseResource resource) {
        FhirContext fhirContext = FHIRConnectorConfig.getInstance().getFhirContext();
        return fhirContext.newXmlParser().encodeResourceToString(resource);
    }

    public static void logConnectorError(String message,
                                         Exception exception,
                                         FHIRConnectorContext connectorContext,
                                         MessageContext messageContext,
                                         Log logger) {
        //Gather request information
        String restApiName = (String) messageContext.getProperty("SYNAPSE_REST_API");
        String restMethod = (String) messageContext.getProperty("REST_METHOD");
        String restRequestPath = (String) messageContext.getProperty("REST_FULL_REQUEST_PATH");
        String requestMsg = "Request [API=" + restApiName + ", Method=" + restMethod + ", Path=" + restRequestPath + "]";

        // Gather mediation information
        String sequenceName;
        String sequenceType;
        ContinuationState continuationState = messageContext.getContinuationStateStack().peek();
        if (continuationState instanceof SeqContinuationState) {
            SeqContinuationState seqContinuationState = (SeqContinuationState) continuationState;
            sequenceName = seqContinuationState.getSeqName();
            sequenceType = seqContinuationState.getSeqType().name();
        } else {
            sequenceName = "Unknown";
            sequenceType = "Unknown";
        }

        Stack functionStack = (Stack) messageContext.getProperty("_SYNAPSE_FUNCTION_STACK");
        String operationName = null;
        StringBuilder templateStack = new StringBuilder();
        if (functionStack != null) {
            operationName = ((TemplateContext) functionStack.peek()).getName();
            int stackSize = functionStack.size();
            int count = 0;
            for (Object function : functionStack) {
                ++count;
                if (count == stackSize) continue;
                TemplateContext templateContext = (TemplateContext) function;
                if (templateStack.length() > 0) {
                    templateStack.append(" -> ");
                }
                templateStack.append(templateContext.getName());
            }
        }
        if (templateStack.length() == 0) templateStack.append("null");
        String mediationMsg = " Mediation [ Sequence=" + sequenceName + ", SequenceType=" + sequenceType +
                ", TemplateStack=" + templateStack.toString() + " ]";

        // Gather operation information
        if (operationName == null) {
            operationName = "Unknown";
        }
        String connectorMsg = " Connector [ Operation=" + operationName + " ]";

        logger.error(message + " \nDetails: ( " + requestMsg + " , " + mediationMsg + " , " + connectorMsg + " )\n" ,
                exception);
    }

    /**
     * Utility function to serialize resource basic details
     *
     * @param resource
     * @return
     */
    public static String resourceBasicDetailsToString(org.hl7.fhir.r4.model.Resource resource) {
        StringBuilder builder =
                new StringBuilder("Resource : { type : ").append(resource.getResourceType().name()).
                        append(", id : ").append(resource.getId()).
                        append("}");
        return builder.toString();
    }
}
