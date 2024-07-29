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

package org.wso2.healthcare.integration.fhir.template.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNamespaceImpl;
import org.apache.axiom.om.impl.dom.ElementImpl;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.Constants;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.config.xml.SynapsePath;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.util.MessageHelper;
import org.apache.synapse.util.xpath.SynapseJsonPath;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.model.HolderFHIRResource;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateException;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;
import org.wso2.healthcare.integration.fhir.template.model.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to handle template specific Message context related operations
 */
public class MsgCtxUtil {
    /**
     * This method will evaluate source path expressions
     *
     * @param messageContext {@link MessageContext} object
     * @param source         holds single source element info
     * @return returns list of evaluated results
     */
    public static List<?> evaluateExpression(MessageContext messageContext,
                                          Source source) {

        List evaluatedResults = new ArrayList();
        if (source.isArrayElement()) {
            for (String arrayPath : source.getArrayPaths()) {
                List evaluatedArray;
                if (arrayPath.startsWith(FHIRConstants.JSON_EVAL)) {
                    evaluatedArray = JsonPathEvaluator.getInstance().evaluate(messageContext,
                            arrayPath.substring(10, arrayPath.length() - 1));
                    if (evaluatedArray != null) {
                        for (int i = 0; i < evaluatedArray.size(); i++) {
                            String leafNodePath = arrayPath + "[" + i + "]." +
                                    ((source.getLeafNodePath().startsWith("$."))
                                            ? source.getLeafNodePath().substring(2) : source.getLeafNodePath());
                            List<?> evaluatedLeafNodes = JsonPathEvaluator.getInstance().evaluate(messageContext,
                                    leafNodePath.substring(10, leafNodePath.length() - 1));
                            evaluatedResults.add(evaluatedLeafNodes);
                        }
                    }
                } else {
                    //xpath case
                    evaluateArrayXpathExpression(messageContext, source, 0, arrayPath,
                            evaluatedResults);
                    break;
                }
            }
        } else if (StringUtils.isNotBlank(source.getExpression())) {
            if (source.getExpression().startsWith(FHIRConstants.JSON_EVAL)) {
                evaluatedResults.add(JsonPathEvaluator.getInstance().evaluate(messageContext,
                        source.getExpression().substring(10, source.getExpression().length() - 1)));
            } else {
                //xpath case
                if ((source.getExpression().startsWith("//") || source.getExpression().startsWith("$ctx:")) &&
                        !source.getExpression().endsWith("text()")) {
                    evaluatedResults.add(XPathEvaluator.getInstance().evaluate(messageContext,
                            source.getExpression() + "/text()"));
                } else {
                    evaluatedResults.add(XPathEvaluator.getInstance().evaluate(messageContext, source.getExpression()));
                }
            }
        }
        return evaluatedResults;
    }

    /**
     * This method will specifically evaluates xml array elements in a way to preserve order
     *
     * @param messageContext   {@link MessageContext} object
     * @param source           holds single source element info
     * @param index            position of the array element
     * @param path             xpath of the source array element
     * @param evaluatedResults list of evaluated results
     */
    private static void evaluateArrayXpathExpression(MessageContext messageContext,
                                                     Source source,
                                                     int index, String path, List evaluatedResults) {

        List<String> arrayPaths = source.getArrayPaths();
        if (index == arrayPaths.size() - 1) {
            if (StringUtils.isBlank(source.getLeafNodePath())) {
                throw new TemplateException("Leaf node path is not correctly defined for array function.");
            }
            List evaluatedLeafNodes = XPathEvaluator.getInstance().evaluate(messageContext, path);
            if (evaluatedLeafNodes != null) {
                List<?> allEvaluatedNodes = new ArrayList<>();
                for (int i = 0; i < evaluatedLeafNodes.size(); i++) {
                    String leafNodePath = path + source.getLeafNodePath();
                    if (source.getLeafNodePath().startsWith("//")) {
                        leafNodePath = path + "[" + (i + 1) + "]" + source.getLeafNodePath().substring(1);
                    }
                    if (!leafNodePath.endsWith("text()")) {
                        leafNodePath += "/text()";
                    }
                    List evaluatedNodes = XPathEvaluator.getInstance().evaluate(messageContext, leafNodePath);
                    if (path.contains("[") && path.contains("]") && i > 0) {
                        allEvaluatedNodes.addAll(evaluatedNodes);
                    } else {
                        allEvaluatedNodes = evaluatedNodes;
                        evaluatedResults.add(allEvaluatedNodes);
                    }
                }
            }
        }
        List evaluatedArray = XPathEvaluator.getInstance().evaluate(messageContext, path);
        index++;
        if (evaluatedArray != null && arrayPaths.size() > index) {
            for (int i = 0; i < evaluatedArray.size(); i++) {
                String nodePath = path + "[" + (i + 1) + "]/" + arrayPaths.get(index);
                if (arrayPaths.get(index).startsWith("//")) {
                    nodePath = path + "[" + (i + 1) + "]/" + arrayPaths.get(index).substring(1);
                }
                evaluateArrayXpathExpression(messageContext, source, index, nodePath, evaluatedResults);
            }
        }
    }

    /**
     * This method will provide list of message contexts for the child elements of the array path
     *
     * @param messageContext Original {@link MessageContext} instance
     * @param expression     Expression for the child element path
     * @return List of child message contexts
     * @throws TemplateException
     */
    public static List<MessageContext> getIteratedMessage(MessageContext messageContext, SynapsePath expression)
            throws TemplateException {

        List<MessageContext> iteratedMsgContexts = new ArrayList<>();
        SOAPEnvelope processingEnvelope = messageContext.getEnvelope();
        if (expression instanceof SynapseXPath) {
            DetachedElementContainer detachedElementContainer =
                    getDetachedMatchingElements(processingEnvelope, messageContext, (SynapseXPath) expression);
            List<?> splitElements = detachedElementContainer.getDetachedElements();

            int splitElementCount = splitElements.size();
            if (splitElementCount == 0) {  // Continue the message flow if no matching elements found
                return null;
            }
            for (Object element : splitElements) {
                if (!(element instanceof OMNode)) {
                    throw new TemplateException("Error splitting message with XPath: " + expression);
                }
                MessageContext iteratedMsgCtx;
                try {
                    iteratedMsgCtx = MessageHelper.cloneMessageContext(getIteratedMessage(messageContext, processingEnvelope,
                            (OMNode) element));
                    //Removes the json stream property from the iterated context.
                    ((Axis2MessageContext) iteratedMsgCtx).getAxis2MessageContext().
                            removeProperty(Constants.ORG_APACHE_SYNAPSE_COMMONS_JSON_JSON_INPUT_STREAM);
                    iteratedMsgContexts.add(iteratedMsgCtx);
                } catch (AxisFault axisFault) {
                    throw new TemplateException("Error creating an iterated copy of the message", axisFault);
                }
            }
        } else if (expression instanceof SynapseJsonPath) {
            String jsonPayload = JsonUtil.jsonPayloadToString(((Axis2MessageContext) messageContext).getAxis2MessageContext());
            DocumentContext parsedJsonPayload = JsonPath.parse(jsonPayload);
            JsonElement iterableChildElements = parsedJsonPayload.read(((SynapseJsonPath) expression).getJsonPath());

            if (!(iterableChildElements instanceof JsonArray)) {
                throw new TemplateException("Error splitting message with jsonpath: " + expression);
            }
            JsonArray iterableJsonArray = iterableChildElements.getAsJsonArray();
            for (JsonElement element : iterableJsonArray) {
                try {
                    MessageContext clonedMessageContext = MessageHelper.cloneMessageContext(messageContext);
                    updateIteratedMessage(clonedMessageContext, element);
                    iteratedMsgContexts.add(clonedMessageContext);
                } catch (AxisFault axisFault) {
                    throw new TemplateException(
                            "Error creating an iterated copy of the message for json source", axisFault);
                }
            }
        }
        return iteratedMsgContexts;
    }

    /**
     * This method will remove namespaces(if any) from a XML message
     *
     * @param messageContext {@link MessageContext} instance
     */
    public static void removeNamespacesFromXmlMessage(MessageContext messageContext) {
        //check if a xml payload
        if (!JsonUtil.hasAJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext())) {
            OMElement message = messageContext.getEnvelope().getBody().getFirstElement();
            if (message.getAllDeclaredNamespaces().hasNext()) {
                removeNamespacesFromOMElement(message);
                try {
                    SOAPFactory soapFactory;
                    if (messageContext.isSOAP11()) {
                        soapFactory = OMAbstractFactory.getSOAP11Factory();
                    } else {
                        soapFactory = OMAbstractFactory.getSOAP12Factory();
                    }
                    SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
                    soapEnvelope.addChild(soapFactory.createSOAPBody());
                    soapEnvelope.getBody().addChild(message);
                    messageContext.setEnvelope(soapEnvelope);
                } catch (AxisFault axisFault) {
                    throw new TemplateException(
                            "Error occurred while setting updated SOAP envelope to the msg context.", axisFault);
                }
            }
        }
    }

    private static void removeNamespacesFromOMElement(OMElement element) {

        element.setNamespace(new OMNamespaceImpl("", ""));
        if (element instanceof ElementImpl) {
            Iterator itr = element.getAllDeclaredNamespaces();
            while (itr.hasNext()) {
                itr.next();
                itr.remove();
            }
        }
        for (Iterator iterator = element.getChildElements(); iterator.hasNext(); ) {
            Object childElement = iterator.next();
            if (childElement instanceof OMElement) {
                removeNamespacesFromOMElement((OMElement) childElement);
            }
        }
    }

    /**
     * Create a new message context using the given original message context,
     * the envelope
     * and the split result element.
     *
     * @param synCtx           original message context
     * @param omNode           element which participates in the iteration replacement
     * @param originalEnvelope original envelope when reaching foreach
     * @return modified message context with new envelope created with omNode
     * @throws AxisFault if there is a message creation failure
     */
    private static MessageContext getIteratedMessage(MessageContext synCtx, SOAPEnvelope originalEnvelope,
                                                     OMNode omNode) throws AxisFault {

        SOAPEnvelope newEnvelope = createNewSoapEnvelope(originalEnvelope);
        if (newEnvelope.getBody() != null) {
            newEnvelope.getBody().addChild(omNode);
        }
        //set the new envelop to original message context
        synCtx.setEnvelope(newEnvelope);

        return synCtx;
    }

    public static SOAPEnvelope createNewSoapEnvelope(SOAPEnvelope envelope) {
        SOAPFactory fac;
        if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI
                .equals(envelope.getBody().getNamespace().getNamespaceURI())) {
            fac = OMAbstractFactory.getSOAP11Factory();
        } else {
            fac = OMAbstractFactory.getSOAP12Factory();
        }
        return fac.getDefaultEnvelope();
    }

    /**
     * Return the set of detached elements and parent specified by the XPath over the given envelope
     *
     * @param envelope   SOAPEnvelope from which the elements will be extracted
     * @param synCtx     Message context from which to extract the elements
     * @param expression SynapseXPath expression describing the elements to be extracted
     * @return data container which hold the detached OMElements in the envelope matching the expression
     * and the parent
     */
    private static DetachedElementContainer getDetachedMatchingElements(SOAPEnvelope envelope,
                                                                        MessageContext synCtx,
                                                                        SynapseXPath expression) {

        DetachedElementContainer resultContainer = new DetachedElementContainer();
        List<OMNode> elementList = new ArrayList<>();
        Object evaluatedResults = expression.evaluate(envelope, synCtx);
        if (evaluatedResults instanceof OMNode) {
            resultContainer.setParent(((OMNode) evaluatedResults).getParent());
            elementList.add(((OMNode) evaluatedResults).detach());
        } else if (evaluatedResults instanceof List) {
            List oList = (List) evaluatedResults;
            if (oList.size() > 0) {
                resultContainer.setParent((((OMNode) oList.get(0)).getParent()));
            }
            for (Object elem : oList) {
                if (elem instanceof OMNode) {
                    elementList.add(((OMNode) elem).detach());
                }
            }
        }
        resultContainer.setDetachedElements(elementList);
        return resultContainer;
    }

    /**
     * Update the message context using the given original message context and
     * jsonElement
     *
     * @param synCtx       original message context
     * @param splitElement JsonElement which participates in the foreach operation
     * @return modified message context with new envelope created with new jsonstream
     * @throws AxisFault if there is a message creation failure
     */
    private static void updateIteratedMessage(MessageContext synCtx, JsonElement splitElement)
            throws AxisFault {
        // write the new JSON message to the stream
        JsonUtil.getNewJsonPayload(((Axis2MessageContext) synCtx).getAxis2MessageContext(),
                splitElement.toString(), true, true);
    }

    /**
     * Returns parsed FHIR resource from the message context payload(if FHIR resource available)
     *
     * @param synCtx {@link MessageContext} instance
     * @return parsed FHIR resource
     * @throws FHIRConnectException
     */
    public static Resource parseFHIRResourceFromMessageCtx(MessageContext synCtx) throws FHIRConnectException {
        HolderFHIRResource parsedResource = new HolderFHIRResource(null, new HashMap<>());
        String requestPayload;
        if (JsonUtil.hasAJsonPayload(((Axis2MessageContext) synCtx).getAxis2MessageContext())) {
            requestPayload =
                    JsonUtil.jsonPayloadToString(((Axis2MessageContext) synCtx).getAxis2MessageContext());
            parsedResource.setHolderResource((org.hl7.fhir.r4.model.Resource) FHIRDataTypeUtils.parseResource(
                    requestPayload, FHIRConstants.JSON));
        } else {
            requestPayload = synCtx.getEnvelope().getBody().getFirstElement().toString();
            parsedResource.setHolderResource((org.hl7.fhir.r4.model.Resource) FHIRDataTypeUtils.parseResource(
                    requestPayload, FHIRConstants.XML));
        }
        return parsedResource;
    }

    /**
     * Initializes and populate given message ctx property to save constructed payload skeleton for write template
     * fields
     *
     * @param messageContext  {@link MessageContext} instance
     * @param propertyName    name of the property
     * @param payloadSkeleton payload structure
     * @throws FHIRConnectException
     */
    public static void initAndPopulateMsgCtxProperty(MessageContext messageContext, String propertyName,
                                                     Object payloadSkeleton) throws FHIRConnectException {
        if (messageContext.getProperty(propertyName) != null) {
            Object property = messageContext.getProperty(propertyName);
            if (property instanceof OMElement) {
                Iterator childElements = ((OMElement) payloadSkeleton).getChildElements();
                while (childElements.hasNext()) {
                    ((OMElement) property).addChild((OMElement) childElements.next());
                }
            } else if (property instanceof JSONObject) {
                JSONArray nodes = ((JSONObject) property).names();
                for (int i = 0; i < nodes.length(); i++) {
                    try {
                        String element = (String) nodes.get(i);
                        if (((JSONObject) payloadSkeleton).names().length() > i &&
                                ((JSONObject) payloadSkeleton).names().get(i).equals(element)) {
                            Object childElement = ((JSONObject) property).get(element);
                            if (childElement instanceof JSONArray) {
                                Object value = ((JSONObject) payloadSkeleton).get(element);
                                if (value instanceof JSONObject) {
                                    ((JSONArray) childElement).put(value);
                                } else if (value instanceof JSONArray) {
                                    JSONArray arr = (JSONArray) value;
                                    for (int j = 0; j < arr.length(); j++) {
                                        ((JSONArray) childElement).put(arr.get(j));
                                    }
                                }
                            } else if (childElement instanceof JSONObject) {
                                ((JSONObject) childElement).append(element,
                                        ((JSONObject) payloadSkeleton).get(element));
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        throw new FHIRConnectException("Error occurred while setting json payload in " +
                                "property: " + propertyName, e);
                    }
                }
            }
        } else {
            if (payloadSkeleton instanceof JSONObject || payloadSkeleton instanceof JSONArray) {
                if (messageContext.getProperty(FHIRConstants.FHIR_WRITE_API_JSON_PROPERTY_NAMES) != null) {
                    ((ArrayList) messageContext.getProperty(FHIRConstants.FHIR_WRITE_API_JSON_PROPERTY_NAMES)).add(propertyName);
                } else {
                    messageContext.setProperty(FHIRConstants.FHIR_WRITE_API_JSON_PROPERTY_NAMES,
                            new ArrayList<>(Collections.singleton(propertyName)));
                }
            }
            messageContext.setProperty(propertyName, payloadSkeleton);
        }
    }

    /**
     * Result container for detached elements and parent
     */
    private static class DetachedElementContainer {

        private OMContainer parent;
        private List<OMNode> detachedElements;

        public OMContainer getParent() {
            return parent;
        }

        public void setParent(OMContainer parent) {
            this.parent = parent;
        }

        public List<OMNode> getDetachedElements() {
            return detachedElements;
        }

        public void setDetachedElements(List<OMNode> detachedElements) {
            this.detachedElements = detachedElements;
        }
    }

}
