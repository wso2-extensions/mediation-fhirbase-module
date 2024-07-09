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
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.config.xml.SynapsePath;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.registry.Registry;
import org.apache.synapse.util.MessageHelper;
import org.apache.synapse.util.xpath.SynapseJsonPath;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.hl7.fhir.r4.model.Property;
import org.jaxen.JaxenException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateOperation;
import org.wso2.healthcare.integration.fhir.core.Query;
import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;
import org.wso2.healthcare.integration.fhir.model.Bundle;
import org.wso2.healthcare.integration.fhir.model.HolderFHIRResource;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.model.type.BaseType;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.template.FHIRTemplateReader;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateException;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;
import org.wso2.healthcare.integration.fhir.template.function.TemplateFunctionExecutor;
import org.wso2.healthcare.integration.fhir.template.model.Element;
import org.wso2.healthcare.integration.fhir.template.model.ExtensionData;
import org.wso2.healthcare.integration.fhir.template.model.PayloadType;
import org.wso2.healthcare.integration.fhir.template.model.PropertyPayloadModel;
import org.wso2.healthcare.integration.fhir.template.model.ResourceModel;
import org.wso2.healthcare.integration.fhir.template.model.Target;
import org.wso2.healthcare.integration.fhir.template.model.WriteDataField;
import org.wso2.healthcare.integration.fhir.template.util.MsgCtxUtil;
import org.wso2.healthcare.integration.fhir.template.util.RegistryTemplateLoader;
import org.wso2.healthcare.integration.fhir.utils.FHIRTemplateDataTypeUtils;
import org.wso2.healthcare.integration.fhir.utils.QueryUtils;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Operation to engage a FHIR template in integration flow
 */
public class FHIREngageTemplate extends FHIRCreateOperation implements ManagedLifecycle {

    private static final Log LOG = LogFactory.getLog(FHIREngageTemplate.class);

    private static final String FHIR_PATH_MAP_PROP = "_OH_INTERNAL_FHIR_Path_Map_";
    private static final String FHIR_EXTENSION_MAP_PROP = "_OH_INTERNAL_FHIR_Extensions_Map_";
    private static final String FHIR_CONSTANT_VALUE_MAP_PROP = "_OH_INTERNAL_FHIR_Constant_Value_Map_";

    private final TemplateFunctionExecutor templateFunctionExecutor = new TemplateFunctionExecutor();
    private final RegistryTemplateLoader registryLoaderTask = new RegistryTemplateLoader();

    public FHIREngageTemplate() {
    }

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("FHIREngageTemplate operation execution started.");
        }
        loadTemplateModelsFromRegistry(messageContext);
        MsgCtxUtil.removeNamespacesFromXmlMessage(messageContext);
        String fhirProfile = (String) messageContext.getProperty("_OH_FhirProfile");
        if (fhirProfile == null) {
            LOG.warn("FHIR profile value is not set for template execution.");
            throw new FHIRConnectException("FHIR profile value is not set for template execution.");
        }
        String fhirResourceMethod = (String) messageContext.getProperty("_OH_FHIR_RESOURCE_METHOD");
        if (StringUtils.isNotBlank(fhirResourceMethod) && fhirResourceMethod.equals("READ")) {
            this.executeReadOp(messageContext, fhirConnectorContext, fhirProfile);
        } else if (StringUtils.isNotBlank(fhirResourceMethod) && fhirResourceMethod.equals("WRITE")) {
            this.executeWriteOp(messageContext, fhirConnectorContext);
        } else {
            String httpMethod = (String) ((Axis2MessageContext) messageContext).getAxis2MessageContext().getProperty(
                    "HTTP_METHOD");
            if ("GET".equals(httpMethod)) {
                this.executeReadOp(messageContext, fhirConnectorContext, fhirProfile);
            } else if ("POST".equals(httpMethod)) {
                this.executeWriteOp(messageContext, fhirConnectorContext);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("FHIREngageTemplate operation execution finished.");
        }
    }

    @Override
    public String getOperationName() {
        return "engageRegulatoryTemplate";
    }

    private void executeReadOp(MessageContext messageContext, FHIRConnectorContext fhirContext,
                               String fhirProfile) throws FHIRConnectException {
        Map<String, ResourceModel> resourceModelMap = FHIRServerDataHolder.getInstance().getResourceModelMapForReadOp();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Number of resource templates loaded to the integration runtime: " + resourceModelMap.size());
            for (String s : resourceModelMap.keySet()) {
                LOG.debug("Loaded resource template: " + s);
            }
        }
        try {
            for (Map.Entry<String, ResourceModel> model : resourceModelMap.entrySet()) {
                ResourceModel value = model.getValue();
                if (fhirProfile.equals(value.getProfile())) {
                    SOAPEnvelope originalEnvelope = MessageHelper.cloneSOAPEnvelope(messageContext.getEnvelope());
                    List<MessageContext> messageContexts;
                    if (value.getSourcePayloadPath() != null) {
                        //array element, hence need to build a bundle
                        SynapsePath path;
                        if (value.getSourcePayloadPath().startsWith(FHIRConstants.JSON_EVAL)) {
                            path = new SynapseJsonPath(
                                    value.getSourcePayloadPath().substring(10, value.getSourcePayloadPath().length() - 1));
                        } else {
                            path = new SynapseXPath(value.getSourcePayloadPath());
                        }
                        // split message context into sub contexts based on the main source payload array
                        messageContexts = MsgCtxUtil.getIteratedMessage(messageContext, path);
                        messageContext.setEnvelope(originalEnvelope);
                    } else {
                        messageContexts = new ArrayList<>();
                        messageContexts.add(messageContext);
                    }
                    if (messageContexts != null) {
                        for (MessageContext iteratedMessageCtxt : messageContexts) {
                            LinkedHashMap<String, Object> fhirPathMap = new LinkedHashMap<>();
                            LinkedHashMap<String, Object> fhirExtensionsMap = new LinkedHashMap<>();
                            LinkedHashMap<String, Object> fhirConstantsValueMap = new LinkedHashMap<>();
                            iteratedMessageCtxt.setProperty(FHIR_PATH_MAP_PROP, fhirPathMap);
                            iteratedMessageCtxt.setProperty(FHIR_EXTENSION_MAP_PROP, fhirExtensionsMap);
                            iteratedMessageCtxt.setProperty(FHIR_CONSTANT_VALUE_MAP_PROP, fhirConstantsValueMap);
                            iteratedMessageCtxt.setProperty(FHIRConstants.FHIR_ORIGINAL_MSG_BODY_PROPERTY, originalEnvelope);
                            populateResourceForIteratedMessageCtx(iteratedMessageCtxt, value, fhirContext);
                        }
                    }
                    break;
                }
            }
        } catch (JaxenException e) {
            throw new FHIRConnectException(e, "Error occurred while evaluating parent element from " +
                    "xpath/jsonpath.");
        } catch (TemplateException e) {
            throw new FHIRConnectException(e, "Error occurred while iterating the message segments.");
        } catch (AxisFault axisFault) {
            throw new FHIRConnectException(axisFault, "Error occurred while setting original payload.");
        }
    }

    private void executeWriteOp(MessageContext messageContext, FHIRConnectorContext fhirContext)
            throws FHIRConnectException {
        HolderFHIRResource parsedResource = (HolderFHIRResource) MsgCtxUtil.parseFHIRResourceFromMessageCtx(messageContext);
        if (parsedResource.unwrap() instanceof org.hl7.fhir.r4.model.Bundle) {
            Bundle bundle = new Bundle(null, new HashMap<>(), fhirContext);
            bundle.setFhirBundle((org.hl7.fhir.r4.model.Bundle) parsedResource.unwrap());
            fhirContext.createResource(bundle);
        }
        //todo check adding holderresource create method in fhircontxt
        fhirContext.createResource(parsedResource);
        Map<String, ResourceModel> resourceModelMap = FHIRServerDataHolder.getInstance().getResourceModelMapForWriteOp();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Number of resource templates loaded to the integration runtime: " + resourceModelMap.size());
        }
        Resource targetResource = fhirContext.getTargetResource();
        Bundle containerResource = fhirContext.getContainerResource();
        List<Resource> resourceList = new ArrayList<>();
        if (containerResource != null) {
            List<org.hl7.fhir.r4.model.Bundle.BundleEntryComponent> entry = containerResource.getEntry();
            for (org.hl7.fhir.r4.model.Bundle.BundleEntryComponent bundleEntryComponent : entry) {
                org.hl7.fhir.r4.model.Resource resource = bundleEntryComponent.getResource();
                //removing if bundle full url appended to resource id
                if (resource.getIdElement() != null) {
                    resource.setId(resource.getIdElement().getIdPart());
                }

                HolderFHIRResource holderFHIRResource = new HolderFHIRResource(null, new HashMap<>());
                holderFHIRResource.setHolderResource(resource);
                resourceList.add(holderFHIRResource);
            }
        } else {
            org.hl7.fhir.r4.model.Resource unwrappedResource = targetResource.unwrap();
            //removing resource type appended by default by hapi parser
            //issue: https://github.com/hapifhir/hapi-fhir/issues/947
            if (unwrappedResource.getIdElement() != null) {
                unwrappedResource.setId(unwrappedResource.getIdElement().getIdPart());
            }
            resourceList.add(targetResource);
        }
        for (Resource resource : resourceList) {
            Map<String, PropertyPayloadModel> copyOfPropertyPayloadModelMap =
                    FHIRServerDataHolder.getInstance().getClonedPropertyPayloadModelMap();
            //todo check for possibility to run this in multithreaded way
            for (String propertyName : copyOfPropertyPayloadModelMap.keySet()) {
                Object payload = copyOfPropertyPayloadModelMap.get(propertyName).getPayload();
                //todo check populating payload skeleton of same property for different resources in bundle
                //this will initialize synapse property and populate payload skeleton to synapse property
                MsgCtxUtil.initAndPopulateMsgCtxProperty(messageContext, propertyName, payload);
                //todo check for profile and engage profile specific property name(payload model) and build payload
                PropertyPayloadModel propertyPayloadModel = copyOfPropertyPayloadModelMap.get(propertyName);
                List<WriteDataField> writeDataFields = propertyPayloadModel.getWriteDataFields();
                for (WriteDataField field : writeDataFields) {
                    Object payloadStructure = field.getPayloadStructure();
                    Query query = new Query();
                    query.setFhirPath(field.getFhirPath());
                    query.setSrcResource(resource);
                    List<Base> results = query.evaluate();
                    int position = 0;
                    //todo point this functionality into propertypayload object
                    for (Base result : results) {
                        if (result instanceof PrimitiveType) {
                            if (PayloadType.XML.equals(field.getPayloadType())) {
                                if (position == 0) {
                                    ((OMElement) payloadStructure).setText(((PrimitiveType<?>) result).asStringValue());
                                } else {
                                    OMElement omElement = ((OMElement) payloadStructure).cloneOMElement();
                                    omElement.setText(((PrimitiveType<?>) result).asStringValue());
                                    ((OMElement) payloadStructure).getParent().addChild(omElement);
                                }
                            } else if (PayloadType.JSON.equals(field.getPayloadType())) {
                                try {
                                    Object target;
                                    if (position == 0) {
                                        target = payloadStructure;
                                    } else {
                                        target = field.getParentNode();
                                    }
                                    if (target instanceof JSONObject) {
                                        ((JSONObject) target).put(field.getTargetNode(),
                                                ((PrimitiveType<?>) result).asStringValue());
                                    } else if (target instanceof JSONArray) {
                                        ((JSONArray) target).put(
                                                ((PrimitiveType<?>) result).asStringValue());
                                    }
                                } catch (JSONException e) {
                                    throw new FHIRConnectException("Error occurred while building " +
                                            "json payload.", e);
                                }
                            }
                            position++;
                        }
                    }
                }
            }
        }
        if (messageContext.getProperty(FHIRConstants.FHIR_WRITE_API_JSON_PROPERTY_NAMES) != null) {
            List<String> jsonPropertyNames = (ArrayList<String>) messageContext.getProperty(
                    FHIRConstants.FHIR_WRITE_API_JSON_PROPERTY_NAMES);
            for (String jsonPropertyName : jsonPropertyNames) {
                Object jsonPayload = messageContext.getProperty(jsonPropertyName);
                if (FHIRConstants.FHIR_WRITE_MESSAGE_BODY_PROPERTY.equals(jsonPropertyName)) {
                    try {
                        JsonUtil.getNewJsonPayload((org.apache.axis2.context.MessageContext) messageContext,
                                jsonPayload.toString(), true, true);
                    } catch (AxisFault axisFault) {
                        throw new FHIRConnectException("Error occurred while setting json payload to message body",
                                axisFault);
                    }
                } else {
                    messageContext.setProperty(jsonPropertyName, jsonPayload.toString());
                }
            }
        }

        if (messageContext.getProperty(FHIRConstants.FHIR_WRITE_MESSAGE_BODY_PROPERTY) instanceof OMElement) {
            SOAPEnvelope newEnvelope = MsgCtxUtil.createNewSoapEnvelope(messageContext.getEnvelope());
            if (newEnvelope.getBody() != null) {
                newEnvelope.getBody().addChild((OMElement) messageContext.getProperty(
                        FHIRConstants.FHIR_WRITE_MESSAGE_BODY_PROPERTY));
            }
            //set the new envelop to original message context
            try {
                messageContext.setEnvelope(newEnvelope);
            } catch (AxisFault axisFault) {
                throw new FHIRConnectException("Error occurred while setting xml message to body.", axisFault);
            }
        }
    }

    private void loadTemplateModelsFromRegistry(MessageContext messageContext) {

        if (OHServerCommonDataHolder.getInstance().getRegistry() == null) {
            LOG.info("Trying to load resource templates from the registry in request flow");
            Registry registry = messageContext.getEnvironment().getSynapseConfiguration().getRegistry();

            String keyMappingTemplateFilePath = null;
            String conditionsTemplateFilePath = null;
            String resourceTemplatePath = null;
            HealthcareIntegratorConfig hConfig = HealthcareIntegratorConfig.getInstance();
            if (hConfig != null) {
                keyMappingTemplateFilePath =
                        hConfig.getFHIRServerConfig().getFhirTemplateConfig().getKeyMappingTemplateFilePath();
                conditionsTemplateFilePath =
                        hConfig.getFHIRServerConfig().getFhirTemplateConfig().getConditionsTemplateFilePath();
                resourceTemplatePath =
                        hConfig.getFHIRServerConfig().getFhirTemplateConfig().getResourceTemplatesPath();
            }
            if (registry != null) {
                //load template configurations
                FHIRTemplateReader fhirTemplateReader =
                        new FHIRTemplateReader(resourceTemplatePath, keyMappingTemplateFilePath, conditionsTemplateFilePath);
                fhirTemplateReader.loadTemplates(registry);
                LOG.info("Resource templates successfully loaded from the registry in request flow");
            }
            OHServerCommonDataHolder.getInstance().setRegistry(registry);
        }
    }

    /**
     * This method will create FHIR resource from template configs and set to the fhir context
     *
     * @param messageContext {@link MessageContext} instance
     * @param model          Resource model representation from mapping template
     * @param fhirContext    {@link FHIRConnectorContext} instance
     */
    private void populateResourceForIteratedMessageCtx(MessageContext messageContext, ResourceModel model,
                                                       FHIRConnectorContext fhirContext) {

        try {
            HolderFHIRResource fhirResource = new HolderFHIRResource(null, new HashMap<>());
            fhirResource.setHolderResource(model.getResourceType());
            fhirContext.createResource(fhirResource);
        } catch (FHIRConnectException e) {
            String message = "Error occurred while creating FHIR resource: " + model.getResourceType();
            log.error(message, e);
            throw new TemplateException(message, e);
        }
        Map<String, Object> constFHIRValueMap =
                (Map<String, Object>) messageContext.getProperty(FHIR_CONSTANT_VALUE_MAP_PROP);
        List<Element> elements = model.getElements();
        if (elements != null) {
            for (Element element : elements) {
                try {
                    //results will be represented as list of lists(2 levels) where there are array function execution,
                    // the results will be put into separate inner lists representing array levels
                    List<?> evaluatedResults = templateFunctionExecutor.execute(messageContext, element.getSource(),
                            MsgCtxUtil.evaluateExpression(messageContext, element.getSource()));
                    if (element.getSource().isConstant() && !FHIRConstants.EXTENSION.equals(
                            element.getTarget().getBaseType())) {
                        populateConstantFHIRAttributes(fhirContext, element.getTarget(), evaluatedResults,
                                constFHIRValueMap);
                    } else {
                        int position = 0;
                        for (Object evaluatedResult : evaluatedResults) {
                            populateFHIRAttributes(messageContext, fhirContext, element.getTarget(),
                                    (List<?>) evaluatedResult, position);
                            position++;
                        }
                    }
                } catch (FHIRConnectException e) {
                    String message = "Error occurred while populating template model for source path: "
                            + element.getSource().getExpression();
                    log.error(message, e);
                    throw new TemplateException(message, e);
                } catch (TemplateFunctionException e) {
                    String message = "Error occurred while executing template functions.";
                    log.error(message, e);
                    throw new TemplateException(message, e);
                }
            }
        }
        Map<String, Object> fhirPathMap = (Map<String, Object>) messageContext.getProperty(FHIR_PATH_MAP_PROP);
        Map<String, Object> fhirExtensionsMap =
                (Map<String, Object>) messageContext.getProperty(FHIR_EXTENSION_MAP_PROP);
        setProfileForResource(fhirPathMap, model.getResourceType(), model.getProfile(),
                fhirContext);
        fhirPathMap.putAll(constFHIRValueMap);
        buildFHIRModel(fhirContext, model.getResourceType(), (String) messageContext.getProperty(
                "_OH_Current_FHIR_Object_Id"), fhirPathMap, fhirExtensionsMap);
        // if not get by id operation add the resource to bundle
        if (messageContext.getProperty("uri.var.id") == null) {
            FHIRTemplateDataTypeUtils.addBundleEntry(fhirContext, null);
        }
    }

    /**
     * This method is used to build FHIR object model for calculated fhir paths
     *
     * @param fhirConnectorContext {@link FHIRConnectorContext} instance
     * @param resourceType         FHIR resource type
     * @param targetId             Target object id(optional)
     * @param fhirPathMap          Calculated fhir path map with fhir data elements
     * @param fhirExtensionMap     Calculated fhir path map with fhir extensions elements
     */
    private void buildFHIRModel(FHIRConnectorContext fhirConnectorContext, String resourceType, String targetId,
                                Map<String, Object> fhirPathMap, Map<String, Object> fhirExtensionMap) {

        Resource targetResource;
        if ("Bundle".equals(resourceType)) {
            targetResource = fhirConnectorContext.getContainerResource();
        } else if (targetId == null) {
            targetResource = fhirConnectorContext.getTargetResource();
        } else {
            targetResource = fhirConnectorContext.getResource(targetId);
        }
        //placing fhir attributes to the object model
        if (fhirPathMap != null) {
            for (String fhirPath : fhirPathMap.keySet()) {
                List<DataType> dataElements = (List<DataType>) fhirPathMap.get(fhirPath);
                //fhirpath contains a slice
                if (fhirPath.contains("]:")) {
                    fhirPath = fhirPath.substring(0, fhirPath.indexOf(":")) + fhirPath.substring(fhirPath.indexOf(":" +
                            ".") + 1);
                } else if (fhirPath.endsWith(":")) {
                    fhirPath = fhirPath.substring(0, fhirPath.indexOf(":"));
                }
                Query query = new Query();
                query.setFhirPath(fhirPath);
                query.setSrcResource(targetResource);
                String childElementName = fhirPath.substring(fhirPath.lastIndexOf(".") + 1);
                if (dataElements != null) {
                    int arrayPos = 0;
                    boolean isArrayElement = false;
                    String leafArrayElementPath = null;
                    for (Object dataElement : dataElements) {
                        try {
                            if (arrayPos > 0 && !isArrayElement && StringUtils.isBlank(leafArrayElementPath)) {
                                List<Base> parentElements = QueryUtils.getParentElements(query);
                                for (Base parentElement : parentElements) {
                                    Property childProp = parentElement.getChildByName(childElementName);
                                    if (childProp != null) {
                                        int maxCardinality = childProp.getMaxCardinality();
                                        if (maxCardinality > 1) {
                                            isArrayElement = true;
                                        } else {
                                            leafArrayElementPath = getLeafArrayElement(query);
                                            if (StringUtils.isNotBlank(leafArrayElementPath)) {
                                                query.setFhirPath(leafArrayElementPath + "[" + arrayPos + "]" +
                                                        fhirPath.substring(leafArrayElementPath.length()));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            QueryUtils.evaluateQueryAndPlace(query, new BaseType<>((DataType) dataElement));
                            arrayPos ++;
                        } catch (FHIRConnectException e) {
                            String message = "Error occurred while building FHIR model for FHIR path: " + fhirPath;
                            log.error(message, e);
                            throw new TemplateException(message, e);
                        }
                    }
                }
            }
        }
        //placing fhir extensions to the object model
        if (fhirExtensionMap != null) {
            for (String fhirPath : fhirExtensionMap.keySet()) {
                Map<String, Object> dataElements = (HashMap) fhirExtensionMap.get(fhirPath);
                //fhirpath contains a slice
                if (fhirPath.contains("]:")) {
                    fhirPath = fhirPath.substring(0, fhirPath.indexOf(":")) + fhirPath.substring(
                            fhirPath.indexOf(":.") + 1);
                } else if (fhirPath.endsWith(":")) {
                    fhirPath = fhirPath.substring(0, fhirPath.indexOf(":"));
                }
                Query query = new Query();
                query.setFhirPath(fhirPath);
                query.setSrcResource(targetResource);
                for (Object entry : dataElements.entrySet()) {
                    try {
                        Map.Entry<String, Object> mapEntry = ((Map.Entry) entry);
                        if (mapEntry != null && mapEntry.getValue() instanceof ArrayList) {
                            ArrayList<Extension> extensionsList = (ArrayList<Extension>) mapEntry.getValue();
                            for (Extension extension : extensionsList) {
                                QueryUtils.placeExtension(query, new BaseType<DataType>(
                                        FHIRTemplateDataTypeUtils.getExtensionDataType(extension)));
                            }
                        }
                    } catch (FHIRConnectException e) {
                        String message =
                                "Error occurred while adding extensions to FHIR model for FHIR path: " + fhirPath;
                        log.error(message, e);
                        throw new TemplateException(message, e);
                    }
                }
            }
        }
    }

    /**
     * Creates FHIR data elements for the evaluated results
     *
     * @param messageContext    {@link MessageContext} instance
     * @param fhirContext       {@link FHIRConnectorContext} instance
     * @param target            Target data element details(i.e: fhirpath)
     * @param evaluatedResults  Evaluated results from the source payload
     * @param outerListPosition Data position of the source payload outer list(payload could represented as list of
     *                          list)
     * @throws FHIRConnectException
     */
    private void populateFHIRAttributes(MessageContext messageContext, FHIRConnectorContext fhirContext,
                                        Target target, List<?> evaluatedResults,
                                        int outerListPosition) throws FHIRConnectException {

        if (evaluatedResults != null) {
            Map<String, Object> fhirPathMap = (Map<String, Object>) messageContext.getProperty(FHIR_PATH_MAP_PROP);
            Map<String, Object> fhirExtensionsMap = (Map<String, Object>) messageContext.getProperty(FHIR_EXTENSION_MAP_PROP);
            if (target.getTargetRoot() == null && target.getRootType() == null &&
                    !FHIRConstants.BACKBONE_ELEMENT.equals(target.getBaseType())) {
                List<DataType> pathList = (ArrayList<DataType>) fhirPathMap.get(target.getFhirPath());
                if (pathList == null) {
                    pathList = new ArrayList<>();
                }
                String resultStr;
                int listPosition = 0;
                for (Object result : evaluatedResults) {
                    if (result instanceof OMTextImpl) {
                        if (FHIRConstants.EXTENSION.equals(target.getBaseType())) {
                            populateExtensionMap(target, ((OMTextImpl) result).getText(), fhirContext,
                                    fhirExtensionsMap, listPosition);
                            listPosition++;
                        } else {
                            resultStr = ((OMTextImpl) evaluatedResults.get(0)).getText();
                            pathList.add(FHIRTemplateDataTypeUtils.getDataTypeObject(target.getTargetDataType(),
                                    target.getFhirPath(), null, resultStr,
                                    null, fhirContext));
                        }
                    } else if (result instanceof String) {
                        if (FHIRConstants.EXTENSION.equals(target.getBaseType())) {
                            populateExtensionMap(target, (String) result, fhirContext, fhirExtensionsMap, listPosition);
                            listPosition++;
                        } else {
                            resultStr = (String) evaluatedResults.get(0);
                            pathList.add(FHIRTemplateDataTypeUtils.getDataTypeObject(target.getTargetDataType(),
                                    target.getFhirPath(), null, resultStr, null,
                                    fhirContext));
                        }
                    }
                }
                if (!FHIRConstants.EXTENSION.equals(target.getBaseType()) && pathList.size() > 0) {
                    fhirPathMap.put(target.getFhirPath(), pathList);
                }
            } else {
                String propName = target.getFhirPath();
                if (target.getTargetRoot() != null) {
                    propName = target.getTargetRoot();
                }
                //this will track the inner list results
                int innerListPosition = 0;
                for (Object result : evaluatedResults) {
                    if (result instanceof OMTextImpl || result instanceof String) {
                        String targetFhirPath;
                        String resultStr;
                        if (result instanceof String) {
                            resultStr = (String) result;
                        } else {
                            resultStr = ((OMTextImpl) result).getText();
                        }
                        if (FHIRConstants.EXTENSION.equals(target.getBaseType())) {
                            if (outerListPosition > 0) {
                                populateExtensionMap(target, resultStr, fhirContext, fhirExtensionsMap, outerListPosition);
                            } else {
                                populateExtensionMap(target, resultStr, fhirContext, fhirExtensionsMap,
                                        Math.max(innerListPosition, 0));
                            }
                        } else {
                            boolean isArrayBackBoneElem = false;
                            //if the leaf level field is a primitive array
                            if (target.getFhirPath().endsWith("[]")) {
                                targetFhirPath = target.getFhirPath().substring(0,
                                        target.getFhirPath().length() - 2);
                            } else {
                                targetFhirPath = target.getFhirPath();
                            }
                            String path = propName;
                            if (FHIRConstants.BACKBONE_ELEMENT.equals(target.getBaseType()) && target.getBasePath() != null) {
                                String fhirPathSegment = target.getFhirPath();
                                if (target.getTargetRoot() != null) {
                                    fhirPathSegment = target.getTargetRoot();
                                }
                                if (target.getSliceName() != null) {
                                    isArrayBackBoneElem = true;
                                    path = target.getBasePath() + "[" + outerListPosition + "]" + ":" +
                                            target.getSliceName() + ":" + fhirPathSegment.substring(target.getBasePath().length());
                                    int arrayPos = 0;
                                    for (String tempPath : fhirPathMap.keySet()) {
                                        if (tempPath.contains("]:") && !tempPath.contains(
                                                "]:" + target.getSliceName())) {
                                            arrayPos = Integer.parseInt(tempPath.substring(
                                                    tempPath.indexOf("[") + 1, tempPath.indexOf("]:"))) + 1;
                                        } else if (tempPath.contains("]:") &&
                                                tempPath.contains("]:" + target.getSliceName())) {
                                            arrayPos =  Integer.parseInt(tempPath.substring(
                                                    tempPath.indexOf("[") + 1, tempPath.indexOf("]:")));
                                            break;
                                        }
                                    }
                                    if (arrayPos > 0) {
                                        path = target.getBasePath() + "[" + arrayPos + "]" + ":" + target.getSliceName() +
                                                ":" + fhirPathSegment.substring(target.getBasePath().length());
                                        //TODO Improvement: check feasibility on developer can configure the array
                                        // position from the target fhir path.
                                    }
                                } else {
                                    isArrayBackBoneElem = true;
                                    path = target.getBasePath() + "[" + outerListPosition + "]" +
                                            fhirPathSegment.substring(target.getBasePath().length());
                                }
                            } else if (target.getSliceName() != null) {
                                path += ":" + target.getSliceName() + ":";
                            }
                            List<DataType> pathList = (ArrayList<DataType>) fhirPathMap.get(path);
                            String type = (target.getRootType() != null) ? target.getRootType() : target.getTargetDataType();
                            if (evaluatedResults.size() == 1 && outerListPosition > 0 && !isArrayBackBoneElem) {
                                innerListPosition = outerListPosition;
                            }
                            if (pathList != null && pathList.size() > innerListPosition) {
                                pathList.set(innerListPosition, FHIRTemplateDataTypeUtils.getDataTypeObject(type, targetFhirPath,
                                        target.getTargetRoot(), resultStr, pathList.get(innerListPosition).unwrap(), fhirContext));
                            } else if (pathList == null) {
                                pathList = new ArrayList<>();
                                pathList.add(FHIRTemplateDataTypeUtils.getDataTypeObject(type,
                                        targetFhirPath, target.getTargetRoot(), resultStr, null, fhirContext));
                            } else {
                                pathList.add(FHIRTemplateDataTypeUtils.getDataTypeObject(type,
                                        targetFhirPath, target.getTargetRoot(), resultStr, null, fhirContext));
                            }
                            if (pathList.size() > 0) {
                                fhirPathMap.put(path, pathList);
                            }
                        }
                        innerListPosition++;
                    }
                }
            }
        }
    }

    /**
     * Creates FHIR extension elements for the evaluated results
     *
     * @param target            Target data element details(i.e: fhirpath)
     * @param result            Evaluated results from the source payload
     * @param fhirContext       {@link FHIRConnectorContext} instance
     * @param fhirExtensionsMap Populated extensions map
     * @param position
     * @throws FHIRConnectException
     */
    private void populateExtensionMap(Target target, String result, FHIRConnectorContext fhirContext,
                                      Map<String, Object> fhirExtensionsMap, int position) throws FHIRConnectException {

        ExtensionData extensionData = target.getExtensionData();
        if (extensionData != null) {
            Map<String, Object> extensionsMap = (HashMap<String, Object>) fhirExtensionsMap.get(
                    extensionData.getFhirPathToExtension());
            if (extensionsMap == null) {
                extensionsMap = new HashMap<>();
            }
            if (StringUtils.isNotBlank(extensionData.getParentExtensionUrl())) {
                String extensionDataType = target.getRootType();
                if (StringUtils.isBlank(extensionDataType)) {
                    extensionDataType = target.getTargetDataType();
                }
                //adding multiple parent extensions
                if (extensionsMap.get(extensionData.getParentExtensionUrl()) != null) {
                    if (((ArrayList<Extension>) extensionsMap.get(extensionData.getParentExtensionUrl())).size() > position) {
                        FHIRTemplateDataTypeUtils.getExtensionByValueType(extensionData.getExtensionUrls(),
                                extensionDataType, extensionData.getFhirPathAfterExtension(), target.getTargetRoot(), result,
                                ((ArrayList<Extension>) extensionsMap.get(extensionData.getParentExtensionUrl())).get(position),
                                fhirContext);
                    } else {
                        Extension extensionByValueType = FHIRTemplateDataTypeUtils.getExtensionByValueType(extensionData.getExtensionUrls(),
                                extensionDataType, extensionData.getFhirPathAfterExtension(), target.getTargetRoot(),
                                result, null, fhirContext);
                        ((ArrayList<Extension>) extensionsMap.get(extensionData.getParentExtensionUrl())).add(extensionByValueType);
                    }
                } else {
                    Extension extensionByValueType = FHIRTemplateDataTypeUtils.getExtensionByValueType(extensionData.getExtensionUrls(),
                            extensionDataType, extensionData.getFhirPathAfterExtension(), target.getTargetRoot(), result,
                            null, fhirContext);
                    ArrayList<Extension> extensions = new ArrayList<>();
                    extensions.add(extensionByValueType);
                    extensionsMap.put(extensionData.getParentExtensionUrl(), extensions);
                }
            }
            fhirExtensionsMap.put(extensionData.getFhirPathToExtension(), extensionsMap);
        }
    }

    /**
     * This method is used to populate hard coded values into FHIR resource attribute.
     *
     * @param fhirContext       {@link FHIRConnectorContext} instance
     * @param target            Holds target element details(i.e fhirpath)
     * @param evaluatedResults  Hard coded values
     * @param constFHIRValueMap Map to hold hard coded values against fhir path
     * @throws FHIRConnectException
     */
    private void populateConstantFHIRAttributes(FHIRConnectorContext fhirContext, Target target,
                                                List<?> evaluatedResults,
                                                Map<String, Object> constFHIRValueMap) throws FHIRConnectException {

        List<DataType> pathList = new ArrayList<>();
        for (Object evaluatedResult : evaluatedResults) {
            if (evaluatedResult instanceof List && ((List<?>) evaluatedResult).size() > 0) {
                pathList.add(FHIRTemplateDataTypeUtils.getDataTypeObject(target.getTargetDataType(),
                        target.getFhirPath(), null, (String) ((List<?>) evaluatedResult).get(0), null,
                        fhirContext));
            }
        }
        constFHIRValueMap.put(target.getFhirPath(), pathList);
    }

    private String getLeafArrayElement(Query query) throws FHIRConnectException {

        String queryPath = query.getFhirPath();
        Query parentQuery = new Query();
        parentQuery.setFhirPath(queryPath.substring(0, queryPath.lastIndexOf('.')));
        parentQuery.setSrcResource(query.getSrcResource());
        List<Base> parentElements = QueryUtils.getParentElements(parentQuery);
        String childElementName = parentQuery.getFhirPath().substring(parentQuery.getFhirPath().lastIndexOf(".") + 1);
        if (childElementName.contains("[")) {
            return null;
        }
        for (Base parentElement : parentElements) {
            Property childProp = parentElement.getChildByName(childElementName);
            if (childProp != null) {
                int maxCardinality = childProp.getMaxCardinality();
                if (maxCardinality > 1) {
                    return parentQuery.getFhirPath();
                }
                break;
            }
        }
        return getLeafArrayElement(query);
    }

    /**
     * Sets FHIR profile value if it's not given in the templates
     *
     * @param fhirpathMap      Calculated fhir path map with fhir data elements
     * @param resourceType     FHIR resource type
     * @param profile          FHIR resource profile url
     * @param connectorContext {@link FHIRConnectorContext}
     */
    private void setProfileForResource(Map<String, Object> fhirpathMap, String resourceType, String profile,
                                       FHIRConnectorContext connectorContext) {
        try {
            List<DataType> dataTypeList = new ArrayList<>();
            dataTypeList.add(FHIRTemplateDataTypeUtils.getDataTypeObject(null, resourceType + ".meta.profile",
                    null, profile, null, connectorContext));
            fhirpathMap.put(resourceType + ".meta.profile", dataTypeList);
        } catch (FHIRConnectException e) {
            log.error("Error occurred while setting profile attribute for the resource: " + resourceType, e);
        }
    }

    @Override
    public void init(SynapseEnvironment synapseEnvironment) {
        reloadTemplateRegistryResources();
    }

    @Override
    public void destroy() {
        reloadTemplateRegistryResources();
    }

    //TODO: we need to implement a feature to observe healthcare deployment artifacts
    private void reloadTemplateRegistryResources() {
        //cleaning the model maps for the hot deployment
        FHIRServerDataHolder.getInstance().getResourceModelMapForReadOp().clear();
        FHIRServerDataHolder.getInstance().getResourceModelMapForWriteOp().clear();
        FHIRServerDataHolder.getInstance().getMappingModelMap().clear();

        registryLoaderTask.execute();
    }
}
