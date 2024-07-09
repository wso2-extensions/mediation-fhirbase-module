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
package org.wso2.healthcare.integration.fhir.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRHealthcareMessageContext;
import org.wso2.healthcare.integration.fhir.model.Bundle;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.ElementType;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class FHIRConnectorContext {

    private static final Log log = LogFactory.getLog(FHIRConnectorContext.class);

    private String baseUrl = null; // Base URL of the FHIR server
    private String incomingRequestUrl = null; //request url received by the WSO2 server for the exposed FHIR server
    private String clientAcceptMediaType = null; //Holds the Accept header from the client to honor it when serializing
    private Map<String, Resource> resourceMap = new ConcurrentHashMap<>(4);
    private Map<String, DataType> dataTypeObjectMap = new ConcurrentHashMap<>(4);
    private Map<String, ElementType> elementTypeObjectMap = new ConcurrentHashMap<>(4);
    private IBaseResource validationOperationOutcome;
    //holds if there's a consent decision sent from gateway
    private String consentDecision;
    private FHIRHealthcareMessageContext fhirHealthcareMessageContext;

    /**
     * targetResource holds the main resource which will be built within the mediation flow.
     * This will only should populated in createResource operations and will be overridden by successive
     * createResource operations
     */
    private Resource targetResource = null;

    /**
     * containerResource holds created bundle resource.
     * There will be only one Bundle in the mediation flow per message
     */
    private Bundle containerResource = null;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if ((baseUrl.charAt(baseUrl.length() - 1) == '/')) {
            this.baseUrl = baseUrl;
        } else {
            this.baseUrl = baseUrl + "/";
        }
    }

    public String getIncomingRequestUrl() {
        return incomingRequestUrl;
    }

    public void setIncomingRequestUrl(String incomingRequestUrl) {
        this.incomingRequestUrl = incomingRequestUrl;
    }

    public String getClientAcceptMediaType() {
        return clientAcceptMediaType;
    }

    public void setClientAcceptMediaType(String clientAcceptMediaType) {
        if (clientAcceptMediaType != null) this.clientAcceptMediaType = clientAcceptMediaType;
    }

    public Resource getTargetResource() {
        return targetResource;
    }

    public Bundle getContainerResource() {
        return containerResource;
    }

    public void createResource(Resource resource) throws FHIRConnectException {
        if (!(resource instanceof Bundle)) {
            // If it is a duplicate, addResource function will throw error, hence no need to check
            targetResource = resource;
        } else {
            containerResource = (Bundle) resource;
            resourceMap.put("_BUNDLE_",resource);
        }
        addResource(resource);
    }

    public void createContainerResource(Resource resource) {
        if (resource instanceof Bundle) {
            containerResource = (Bundle) resource;
        }
    }

    public void addResource(Resource resource) throws FHIRConnectException {
        if (resource == null) {
            throw new FHIRConnectException("Resource is null");
        }
        if (resource.getObjectId() == null) {
            throw new FHIRConnectException("Resource doesn't have internal object id.");
        }
        if (!resourceMap.containsKey(resource.getObjectId())) {
            resourceMap.put(resource.getObjectId(), resource);
        } else {
            throw new FHIRConnectException("Duplicate resource with the internal object id: " + resource.getObjectId());
        }
    }

    public Resource getResource(String internalObjId) {
        return resourceMap.get(internalObjId);
    }

    public Map<String, Resource> getResourceMap() {
        return resourceMap;
    }

    public void updateResource(String internalObjId, Resource resource) {
        if (resourceMap.containsKey(internalObjId)) {
            resourceMap.put(internalObjId, resource);
        } else {
            FHIRConnectorUtils.handleException("No patient exists with internal object id: " + internalObjId);
        }
    }

    public void addDataObject(DataType dataObject) throws FHIRConnectException {
        if (dataObject == null) {
            throw new FHIRConnectException("Data Object is null");
        }
        if (dataObject.getObjectId() == null) {
            throw new FHIRConnectException("Data object doesn't have internal object id.");
        }
        if (!dataTypeObjectMap.containsKey(dataObject.getObjectId())) {
            dataTypeObjectMap.put(dataObject.getObjectId(), dataObject);
        } else {
            throw new FHIRConnectException(
                    "Duplicate data object with the internal object id: " + dataObject.getObjectId());
        }
    }

    public void addElementObject(ElementType elementTypeObject) throws FHIRConnectException {
        if (elementTypeObject == null) {
            throw new FHIRConnectException("Element Object is null");
        }
        if (elementTypeObject.getObjectId() == null) {
            throw new FHIRConnectException("Element object doesn't have internal object id.");
        }
        if (!elementTypeObjectMap.containsKey(elementTypeObject.getObjectId())) {
            elementTypeObjectMap.put(elementTypeObject.getObjectId(), elementTypeObject);
        } else {
            throw new FHIRConnectException(
                    "Duplicate element object with the internal object id: " + elementTypeObject.getObjectId());
        }
    }

    public DataType getDataObject(String objectId) {
        return dataTypeObjectMap.get(objectId);
    }

    public DataType removeDataObject(String objectId) {
        return dataTypeObjectMap.remove(objectId);
    }

    public ElementType getElementObject(String objectId) {
        return elementTypeObjectMap.get(objectId);
    }

    public ElementType removeElementObject(String objectId) {
        return elementTypeObjectMap.remove(objectId);
    }

    public IBaseResource getValidationOperationOutcome() {
        return validationOperationOutcome;
    }

    public void setValidationOperationOutcome(IBaseResource validationOperationOutcome) {
        this.validationOperationOutcome = validationOperationOutcome;
    }

    public String getConsentDecision() {
        return consentDecision;
    }

    public void setConsentDecision(String consentDecision) {
        this.consentDecision = consentDecision;
    }

    public FHIRHealthcareMessageContext getFHIRHealthcareMessageContext() {

        return fhirHealthcareMessageContext;
    }

    public void setFHIRHealthcareMessageContext(FHIRHealthcareMessageContext fhirHealthcareMessageContext) {

        this.fhirHealthcareMessageContext = fhirHealthcareMessageContext;
    }
}
