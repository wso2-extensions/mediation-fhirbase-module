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

package org.wso2.healthcare.integration.fhir.template;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.registry.Registry;
import org.wso2.healthcare.integration.fhir.internal.FHIRServerDataHolder;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateException;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;
import org.wso2.healthcare.integration.fhir.template.model.Condition;
import org.wso2.healthcare.integration.fhir.template.model.ConditionMapping;
import org.wso2.healthcare.integration.fhir.template.model.ConditionsMappingModel;
import org.wso2.healthcare.integration.fhir.template.model.KeyPairMapping;
import org.wso2.healthcare.integration.fhir.template.model.KeyPairMappingModel;
import org.wso2.healthcare.integration.fhir.template.model.PropertyPayloadModel;
import org.wso2.healthcare.integration.fhir.template.model.ResourceModel;
import org.wso2.healthcare.integration.fhir.template.model.Source;
import org.wso2.healthcare.integration.fhir.template.util.PayloadBuilderUtil;
import org.wso2.carbon.mediation.registry.WSO2Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.ServerType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to read FHIR mapping templates deployed in MI registry.
 */
public class FHIRTemplateReader {

    private static final Log LOG = LogFactory.getLog(FHIRTemplateReader.class);

    private String keyMappingTemplateFilePath = "conf:/healthcare/templates/util/keymappings.yaml";
    private String conditionsTemplateFilePath = "conf:/healthcare/templates/util/conditions.yaml";
    private String resourceTemplatePath = "conf:/healthcare/templates/resources";

    public FHIRTemplateReader(String resourceTemplatePath) {
        if (StringUtils.isNotBlank(resourceTemplatePath)) {
           this.resourceTemplatePath = resourceTemplatePath;
        }
    }

    public FHIRTemplateReader(String resourceTemplatePath, String keyMappingTemplateFilePath,
                              String conditionsTemplateFilePath) {
        if (StringUtils.isNotBlank(resourceTemplatePath)) {
            this.resourceTemplatePath = resourceTemplatePath;
        }
        if (StringUtils.isNotBlank(keyMappingTemplateFilePath)) {
            this.keyMappingTemplateFilePath = keyMappingTemplateFilePath;
        }
        if (StringUtils.isNotBlank(conditionsTemplateFilePath)) {
            this.conditionsTemplateFilePath = conditionsTemplateFilePath;
        }
    }

    /**
     * This method is used to load the FHIR mapping templates into the memory
     *
     * @param registry {@link Registry} instance
     */
    public void loadTemplates(Registry registry) {
        loadConditions(registry);
        loadConfigTemplateFields(registry);
        loadKeyMappings(registry);
    }

    /**
     * This method is used to load FHIR resource templates
     *
     * @param registry {@link Registry} instance
     */
    private void loadConfigTemplateFields(Registry registry) {
        Map<String, ResourceModel> resourceModelMapForReadOp = FHIRServerDataHolder.getInstance().getResourceModelMapForReadOp();
        Map<String, ResourceModel> resourceModelMapForWriteOp = FHIRServerDataHolder.getInstance().getResourceModelMapForWriteOp();
        if (registry != null) {
            String[] fileNames = null;
            if (OHServerCommonDataHolder.getInstance().getServerType().equals(ServerType.MI)) {
                OMNode resourceDetails = registry.lookup(resourceTemplatePath);
                String fileNamesStr = ((OMText) resourceDetails).getText();
                //get registry collection files
                fileNames = fileNamesStr.split("\\n");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("File names string: " + fileNamesStr);
                    LOG.debug("Available template files:" + Arrays.toString(fileNames));
                }
            } else if (OHServerCommonDataHolder.getInstance().getServerType().equals(ServerType.EI)) {
                Resource resourceDir = ((WSO2Registry) registry).getResource(resourceTemplatePath);
                try {
                    Object content = resourceDir.getContent();
                    if (content != null) {
                        fileNames = (String[]) content;
                    }
                } catch (Exception e) {
                    LOG.error("Error occurred while fetching resources from EI registry.", e);
                }
            }
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    //fixing issue with the windows systems when getting list of reg file names.
                    if (fileName.endsWith("\r")) {
                        fileName = fileName.substring(0, fileName.length() -1);
                    }
                    if (fileName.endsWith(".yaml")) {
                        OMNode templateResource;
                        if (OHServerCommonDataHolder.getInstance().getServerType().equals(ServerType.EI)) {
                            templateResource = registry.lookup(resourceTemplatePath.substring(0,
                                    resourceTemplatePath.indexOf(":") + 1) + fileName);
                        } else {
                            templateResource = registry.lookup(resourceTemplatePath + "/" + fileName);
                        }
                        if (templateResource != null) {
                            try {
                                InputStream inputStream = ((DataHandler)
                                        ((OMText) templateResource).getDataHandler()).getInputStream();
                                Map<String, Object> configObjectMap = getConfigObjectMap(inputStream);
                                String resourceType = (String) configObjectMap.get("resourceType");
                                String profile = (String) configObjectMap.get("profile");
                                LinkedHashMap sourcePayloadPathElement = (LinkedHashMap) configObjectMap.get(
                                        "sourcePayloadRoot");
                                LinkedHashMap namespacesElement = (LinkedHashMap) configObjectMap.get(
                                        "namespaces");
                                if (StringUtils.isBlank(resourceType) || StringUtils.isBlank(profile)) {
                                    LOG.error("resourceType and profile elements must be present in the template:" +
                                            fileName);
                                    return;
                                }
                                String sourcePayloadPath = (String) sourcePayloadPathElement.get("read");
                                List elements = ((List) configObjectMap.get("elements"));
                                if (elements != null && elements.size() > 0) {
                                    ResourceModel resourceModelForReadOp = new ResourceModel();
                                    ResourceModel resourceModelForWriteOp = new ResourceModel();
                                    resourceModelForReadOp.setResourceType(resourceType);
                                    resourceModelForWriteOp.setResourceType(resourceType);
                                    resourceModelForReadOp.setProfile(profile);
                                    resourceModelForWriteOp.setProfile(profile);
                                    resourceModelForReadOp.setSourcePayloadPath(sourcePayloadPath);
                                    if (namespacesElement != null) {
                                        for (Object element : namespacesElement.keySet()) {
                                            if (!resourceModelForReadOp.getNamespaces().containsKey((String) element)) {
                                                resourceModelForReadOp.getNamespaces().put((String) element,
                                                        (String) namespacesElement.get(element));
                                            }
                                            if (!resourceModelForWriteOp.getNamespaces().containsKey((String) element)) {
                                                resourceModelForWriteOp.getNamespaces().put((String) element,
                                                        (String) namespacesElement.get(element));
                                            }
                                        }
                                    }
                                    for (Object element : elements) {
                                        LinkedHashMap sourceElement = (LinkedHashMap) ((LinkedHashMap) element).get(
                                                "source");
                                        LinkedHashMap fhirMappingConfigs = (LinkedHashMap) ((LinkedHashMap) element).get(
                                                "fhir");
                                        LinkedHashMap metaDataMap = (LinkedHashMap) fhirMappingConfigs.get("meta");
                                        String target = (String) fhirMappingConfigs.get("attribute");
                                        String targetType = (String) fhirMappingConfigs.get("dataType");
                                        String targetRoot = null;
                                        String rootType = null;
                                        String baseType = null;
                                        String basePath = null;
                                        String sliceName = null;
                                        if (metaDataMap != null) {
                                            rootType = (String) metaDataMap.get("rootType");
                                            baseType = (String) metaDataMap.get("baseType");
                                            basePath = (String) metaDataMap.get("basePath");
                                            sliceName = (String) metaDataMap.get("sliceName");
                                            if (metaDataMap.get("root") != null) {
                                                targetRoot = (String) metaDataMap.get("root");
                                            }
                                        }
                                        if (sourceElement != null) {
                                            String readOperationExpression = (String) sourceElement.get("read");
                                            String writeOperationExpression = (String) sourceElement.get("write");
                                            if (StringUtils.isNotBlank(readOperationExpression)) {
                                                resourceModelForReadOp.addElement(readOperationExpression, target,
                                                        targetRoot,
                                                        baseType, basePath, rootType, targetType, sliceName,
                                                        Source.OperationType.READ);
                                            }
                                            if (StringUtils.isNotBlank(writeOperationExpression)) {
                                                resourceModelForWriteOp.addElement(writeOperationExpression,
                                                        target, targetRoot,
                                                        baseType, basePath, rootType, targetType, sliceName,
                                                        Source.OperationType.WRITE);
                                            }
                                        }
                                    }
                                    resourceModelMapForReadOp.putIfAbsent(profile, resourceModelForReadOp);
                                    resourceModelMapForWriteOp.putIfAbsent(profile, resourceModelForWriteOp);
                                    Map<String, PropertyPayloadModel> propertyPayloadModelMap =
                                            FHIRServerDataHolder.getInstance().getPropertyPayloadModelMap();
                                    for (String propName : propertyPayloadModelMap.keySet()) {
                                        PayloadBuilderUtil.buildPayloadSkeleton(propertyPayloadModelMap.get(propName));
                                    }
                                }
                            } catch (IOException e) {
                                LOG.error("Error occurred while reading the yaml: " + fileName, e);
                            } catch (TemplateFunctionException e) {
                                LOG.error("Error occurred while parsing template functions the template yaml: " + fileName, e);
                            } catch (TemplateException e) {
                                LOG.error("Error occurred while parsing template yaml: " + fileName, e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is used to load FHIR key value mapping templates into memory
     *
     * @param registry {@link Registry} instance
     */
    private void loadKeyMappings(Registry registry) {
        Map<String, KeyPairMappingModel> mappingModelMap = FHIRServerDataHolder.getInstance().getMappingModelMap();
        OMNode templateResource = registry.lookup(keyMappingTemplateFilePath);
        if (templateResource != null) {
            try {
                InputStream inputStream = ((DataHandler)
                        ((OMText) templateResource).getDataHandler()).getInputStream();
                Map<String, Object> configObjectMap = getConfigObjectMap(inputStream);
                String type = (String) configObjectMap.get("type");
                List mappings = (ArrayList) configObjectMap.get("mappings");
                for (Object mapping : mappings) {
                    if (mapping instanceof LinkedHashMap) {
                        KeyPairMappingModel mappingModel = new KeyPairMappingModel();
                        mappingModel.setMappingType(type);
                        KeyPairMapping keyPairMapping = new KeyPairMapping();
                        mappingModel.setKeyPairMapping(keyPairMapping);
                        String name = (String) ((LinkedHashMap) mapping).get("name");
                        String defaultKey = (String) ((LinkedHashMap) mapping).get("defaultKey");
                        keyPairMapping.setName(name);
                        keyPairMapping.setDefaultMapping(defaultKey);
                        List elements = (ArrayList) ((LinkedHashMap) mapping).get("elements");
                        if (elements != null) {
                            for (Object element : elements) {
                                if (element instanceof LinkedHashMap) {
                                    LinkedHashMap hashMap = (LinkedHashMap) element;
                                    for (Object key : hashMap.keySet()) {
                                        List values = (ArrayList) hashMap.get(key);
                                        for (Object value : values) {
                                            keyPairMapping.getMappings().put((String) value, (String) key);
                                        }
                                    }
                                }
                            }
                        }
                        mappingModelMap.put(name, mappingModel);
                    }
                }
            } catch (IOException e) {
                LOG.error("Error occurred while reading the yaml file: " + keyMappingTemplateFilePath, e);
            } catch (TemplateException e) {
                LOG.error("Error occurred while parsing template yaml: " + keyMappingTemplateFilePath, e);
            }
        }
    }

    /**
     * This method is used to load FHIR conditions template values into memory
     *
     * @param registry {@link Registry} instance
     */
    private void loadConditions(Registry registry) {
        ConditionsMappingModel conditionsMappingModel = FHIRServerDataHolder.getInstance().getConditionsMappingModel();
        OMNode templateResource = registry.lookup(conditionsTemplateFilePath);
        if (templateResource != null) {
            InputStream inputStream;
            try {
                inputStream = ((DataHandler)
                        ((OMText) templateResource).getDataHandler()).getInputStream();
                Map<String, Object> configObjectMap = getConfigObjectMap(inputStream);
                String type = (String) configObjectMap.get("type");
                conditionsMappingModel.setType(type);
                List mappings = (ArrayList) configObjectMap.get("mappings");
                for (Object mapping : mappings) {
                    if (mapping instanceof LinkedHashMap) {
                        ConditionMapping conditionMapping = new ConditionMapping();
                        String conditionMappingName = (String) ((LinkedHashMap) mapping).get("name");
                        LinkedHashMap rootLevelDefaultCase = (LinkedHashMap) ((LinkedHashMap) mapping).get("default");
                        LinkedHashMap rootLevelDefaultReturnValueMap = null;
                        if (rootLevelDefaultCase != null && rootLevelDefaultCase.get("returnValues") != null) {
                            rootLevelDefaultReturnValueMap = (LinkedHashMap) rootLevelDefaultCase.get("returnValues");
                        }
                        List conditions = (ArrayList) ((LinkedHashMap) mapping).get("conditions");
                        if (conditions != null) {
                            for (Object conditionObj : conditions) {
                                Condition condition = new Condition();
                                if (conditionObj instanceof LinkedHashMap) {
                                    LinkedHashMap conditionMap = (LinkedHashMap) conditionObj;
                                    String expression = (String) conditionMap.get("expression");
                                    String value = String.valueOf(conditionMap.get("value"));
                                    LinkedHashMap returnValueMap = (LinkedHashMap) ((LinkedHashMap) conditionObj)
                                            .get("returnValues");
                                    LinkedHashMap defaultCase = (LinkedHashMap) ((LinkedHashMap) conditionObj)
                                            .get("default");
                                    if (returnValueMap != null) {
                                        for (Object key : returnValueMap.keySet()) {
                                            String returnValue = (String) returnValueMap.get(key);
                                            if (StringUtils.isNotBlank(returnValue)) {
                                                Source source = new Source(returnValue, null,
                                                        Source.OperationType.READ, null);
                                                condition.addReturnValue((String) key, source);
                                            }
                                        }
                                    }
                                    if (rootLevelDefaultReturnValueMap != null) {
                                        for (Object key : rootLevelDefaultReturnValueMap.keySet()) {
                                            String returnValue = (String) rootLevelDefaultReturnValueMap.get(key);
                                            if (StringUtils.isNotBlank(returnValue)) {
                                                Source source = new Source(returnValue, null,
                                                        Source.OperationType.READ, null);
                                                condition.addDefaultReturnValue((String) key, source);
                                            }
                                        }
                                    }
                                    LinkedHashMap defaultReturnValueMap = null;
                                    if (defaultCase != null && defaultCase.get("returnValues") != null) {
                                        defaultReturnValueMap = (LinkedHashMap) defaultCase.get("returnValues");
                                    }
                                    condition.setExpression(expression);
                                    condition.setValue(value);
                                    populateNestedConditions((ArrayList) conditionMap.get("conditions"), condition,
                                            defaultReturnValueMap);
                                    conditionMapping.getConditions().add(condition);
                                }
                            }
                        }
                        conditionMapping.setName(conditionMappingName);
                        conditionsMappingModel.getConditionMappingMap().put(conditionMappingName, conditionMapping);
                    }
                }
            } catch (IOException e) {
                LOG.error("Error occurred while reading the yaml file: " + conditionsTemplateFilePath, e);
            } catch (TemplateException | TemplateFunctionException e) {
                LOG.error("Error occurred while parsing template yaml: " + conditionsTemplateFilePath, e);
            }
        }
    }

    private void populateNestedConditions(ArrayList conditionList, Condition parent,
                                          LinkedHashMap defaultReturnValueMap) throws TemplateFunctionException {
        if (conditionList != null) {
            for (Object conditionObj : conditionList) {
                if (conditionObj instanceof LinkedHashMap) {
                    Condition condition = new Condition();
                    String expression = (String) ((LinkedHashMap)conditionObj).get("expression");
                    String value = String.valueOf(((LinkedHashMap)conditionObj).get("value"));
                    ArrayList childConditionList = (ArrayList) ((LinkedHashMap)conditionObj).get("conditions");
                    condition.setExpression(expression);
                    condition.setValue(value);
                    LinkedHashMap returnValueMap = (LinkedHashMap) ((LinkedHashMap) conditionObj)
                            .get("returnValues");
                    LinkedHashMap defaultCase = (LinkedHashMap) ((LinkedHashMap) conditionObj).get("default");
                    if (returnValueMap != null) {
                        for (Object key : returnValueMap.keySet()) {
                            String returnValue = (String) returnValueMap.get(key);
                            if (StringUtils.isNotBlank(returnValue)) {
                                Source source = new Source(returnValue, null,
                                        Source.OperationType.READ, null);
                                condition.addReturnValue((String) key, source);
                            }
                        }
                    }
                    if (defaultReturnValueMap != null) {
                        for (Object key : defaultReturnValueMap.keySet()) {
                            String returnValue = (String) defaultReturnValueMap.get(key);
                            if (StringUtils.isNotBlank(returnValue)) {
                                Source source = new Source(returnValue, null,
                                        Source.OperationType.READ, null);
                                condition.addDefaultReturnValue((String) key, source);
                            }
                        }
                    }
                    LinkedHashMap defaultReturnValueMapNestedConditions = null;
                    if (defaultCase != null && defaultCase.get("returnValues") != null) {
                        defaultReturnValueMapNestedConditions = (LinkedHashMap) defaultCase.get(
                                "returnValues");
                    }
                    parent.getNestedConditions().add(condition);
                    populateNestedConditions(childConditionList, condition, defaultReturnValueMapNestedConditions);
                }
            }
        }
    }

    /**
     * This is used to read yaml file given the file stream
     *
     * @param stream Input file stream
     * @return Map consisting with parsed fields
     */
    public static Map<String, Object> getConfigObjectMap(InputStream stream) throws TemplateException {
        Yaml yaml = new Yaml();
        try {
            return yaml.load(stream);
        } catch (ScannerException e) {
            throw new TemplateException("Error occurred while creating object map for yaml", e);
        }
    }

}
