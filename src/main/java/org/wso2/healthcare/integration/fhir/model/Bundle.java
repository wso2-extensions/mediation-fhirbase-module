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

// Generated on 18-Thu, 06, 2020 18:09:13+0530

package org.wso2.healthcare.integration.fhir.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Signature;
import org.hl7.fhir.r4.model.UnsignedIntType;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRHealthcareMessageContext;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils.handleException;

public class Bundle extends org.wso2.healthcare.integration.fhir.model.Resource {

    private static final Log LOG = LogFactory.getLog(Bundle.class);
    private org.hl7.fhir.r4.model.Bundle fhirBundle;
    /**
     * Track resources added to the bundle
     * Map<ResourceType, Map<ResourceID,BooleanTracker>
     *     BooleanTracker is not used
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> resourceTracker;
    private String operationPrefix;

    public Bundle(String parentPrefix, Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        super(parentPrefix, connectorInputParameters);
        this.fhirBundle = new org.hl7.fhir.r4.model.Bundle();
        this.resourceTracker = new ConcurrentHashMap<>();
        this.operationPrefix = "";
        if (parentPrefix != null) {
            this.operationPrefix = parentPrefix;
        }
        this.init(connectorInputParameters, connectorContext);
    }

    @Override
    public Resource unwrap() {
        return fhirBundle;
    }

    protected void init(Map<String, String> parameters, FHIRConnectorContext connectorContext) throws FHIRConnectException {
        super.init(parameters, connectorContext);
        this.setIdentifier(parameters, connectorContext);
        this.setType(parameters, connectorContext);
        this.setTimestamp(parameters, connectorContext);
        this.setTotal(parameters, connectorContext);
        this.addLink(parameters, connectorContext);
        this.addEntry(parameters, connectorContext);
        this.setSignature(parameters, connectorContext);
    }

    @Override
    public void beforeSerialize(FHIRConnectorContext connectorContext) {
        super.beforeSerialize(connectorContext);
        org.hl7.fhir.r4.model.Bundle.BundleLinkComponent nextLink = fhirBundle.getLink("next");
        org.hl7.fhir.r4.model.Bundle.BundleLinkComponent prevLink = fhirBundle.getLink("previous");
        org.hl7.fhir.r4.model.Bundle.BundleLinkComponent selfLink = fhirBundle.getLink("self");

        if (connectorContext.getBaseUrl() != null && nextLink == null && prevLink == null && selfLink == null
                && connectorContext.getIncomingRequestUrl() != null) {
            populatePaginationLinks(connectorContext);
        }
    }

    public Resource getBaseResource() {
        return this.fhirBundle.castToResource(fhirBundle);
    }

    public void setFhirBundle(org.hl7.fhir.r4.model.Bundle fhirBundle) {
        this.fhirBundle = fhirBundle;
    }

    public org.hl7.fhir.r4.model.Bundle getFhirBundle() {
        return fhirBundle;
    }

    public void setIdentifier(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirBundle.setIdentifier(FHIRDataTypeUtils.getIdentifier(operationPrefix + "identifier.",
                connectorInputParameters, connectorContext));
    }

    public Identifier getIdentifier() {
        return this.fhirBundle.getIdentifier();
    }

    public void setType(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        try {
            fhirBundle.setType(org.hl7.fhir.r4.model.Bundle.BundleType
                    .fromCode(connectorInputParameters.get(operationPrefix + "type")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the type field";
            FHIRConnectorUtils.handleException(msg, e);
        }
    }

    public org.hl7.fhir.r4.model.Bundle.BundleType getType() {
        return this.fhirBundle.getType();
    }

    public void setTimestamp(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirBundle.setTimestamp(FHIRDataTypeUtils.getInstant(operationPrefix + "timestamp",
                connectorInputParameters, connectorContext));
    }

    public Date getTimestamp() {
        return this.fhirBundle.getTimestamp();
    }

    public void setTotal(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        Integer total = FHIRDataTypeUtils.getUnsignedInt(operationPrefix + "total", connectorInputParameters,
                connectorContext);
        if (total != null) {
            this.fhirBundle.setTotal(total);
        }
    }

    public UnsignedIntType getTotal() {
        return this.fhirBundle.getTotalElement();
    }

    public void addLink(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        fhirBundle.addLink(getBundleLink(connectorInputParameters, connectorContext));
    }

    public void setLink(List<org.hl7.fhir.r4.model.Bundle.BundleLinkComponent> linkList) {
        this.fhirBundle.setLink(linkList);
    }

    public List<org.hl7.fhir.r4.model.Bundle.BundleLinkComponent> getLink() {
        return this.fhirBundle.getLink();
    }

    private org.hl7.fhir.r4.model.Bundle.BundleLinkComponent getBundleLink(Map<String, String> connectorInputParameters,
            FHIRConnectorContext connectorContext) throws FHIRConnectException {
        org.hl7.fhir.r4.model.Bundle.BundleLinkComponent linkComponent = new org.hl7.fhir.r4.model.Bundle.BundleLinkComponent();
        String backboneElementPrefix = "link.";
        if (connectorInputParameters.containsKey("contentReferenceAttrPrefix")) {
            backboneElementPrefix = connectorInputParameters.get("contentReferenceAttrPrefix");
        }
        String operationPrefix = this.operationPrefix + backboneElementPrefix;

        linkComponent.setRelation(FHIRDataTypeUtils
                .getString(operationPrefix + "relation.", connectorInputParameters,
                        connectorContext));

        linkComponent.setUrl(FHIRDataTypeUtils
                .getUri(operationPrefix + "url.", connectorInputParameters, connectorContext));
        return (linkComponent.isEmpty() ? null : linkComponent);
    }

    private org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent getBundleEntrySearch(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent entrySearchComponent = new org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent();

        try {
            entrySearchComponent.setMode(org.hl7.fhir.r4.model.Bundle.SearchEntryMode
                    .fromCode(connectorInputParameters.get(operationPrefix + "entry.search.mode")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the entry.search.mode field";
            FHIRConnectorUtils.handleException(msg, e);
        }

        entrySearchComponent.setScore(FHIRDataTypeUtils.getDecimal(operationPrefix + "entry.search.score.",
                connectorInputParameters, connectorContext));
        return (entrySearchComponent.isEmpty() ? null : entrySearchComponent);
    }

    private org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent getBundleEntryRequest(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent entryRequestComponent = new org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent();

        try {
            entryRequestComponent.setMethod(org.hl7.fhir.r4.model.Bundle.HTTPVerb
                    .fromCode(connectorInputParameters.get(operationPrefix + "entry.request.method")));
        } catch (FHIRException e) {
            String msg = "Error occurred while setting the entry.request.method field";
            FHIRConnectorUtils.handleException(msg, e);
        }

        entryRequestComponent.setUrl(FHIRDataTypeUtils.getUri(operationPrefix + "entry.request.url.",
                connectorInputParameters, connectorContext));

        entryRequestComponent.setIfNoneMatch(FHIRDataTypeUtils.getString(operationPrefix + "entry.request.ifNoneMatch.",
                connectorInputParameters, connectorContext));

        entryRequestComponent.setIfModifiedSince(FHIRDataTypeUtils.getInstant(
                operationPrefix + "entry.request.ifModifiedSince.", connectorInputParameters, connectorContext));

        entryRequestComponent.setIfMatch(FHIRDataTypeUtils.getString(operationPrefix + "entry.request.ifMatch.",
                connectorInputParameters, connectorContext));

        entryRequestComponent.setIfNoneExist(FHIRDataTypeUtils.getString(operationPrefix + "entry.request.ifNoneExist.",
                connectorInputParameters, connectorContext));
        return (entryRequestComponent.isEmpty() ? null : entryRequestComponent);
    }

    private org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent getBundleEntryResponse(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent entryResponseComponent = new org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent();

        entryResponseComponent.setStatus(FHIRDataTypeUtils.getString(operationPrefix + "entry.response.status.",
                connectorInputParameters, connectorContext));

        entryResponseComponent.setLocation(FHIRDataTypeUtils.getUri(operationPrefix + "entry.response.location.",
                connectorInputParameters, connectorContext));

        entryResponseComponent.setEtag(FHIRDataTypeUtils.getString(operationPrefix + "entry.response.etag.",
                connectorInputParameters, connectorContext));

        entryResponseComponent.setLastModified(FHIRDataTypeUtils.getInstant(
                operationPrefix + "entry.response.lastModified.", connectorInputParameters, connectorContext));

        /* Commented manually since not supported yet
        entryResponseComponent.setOutcome(FHIRDataTypeUtils.getResource(operationPrefix + "entry.response.outcome.",
                connectorInputParameters, connectorContext));*/
        return (entryResponseComponent.isEmpty() ? null : entryResponseComponent);
    }

    public void addEntry(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry = getBundleEntry(connectorInputParameters, connectorContext);
        addBundleEntryComponent(entry, connectorContext);
    }

    /**
     * Add given resource as a bundle entry
     *
     * @param resource
     * @param connectorContext
     */
    public void addResourceAsBundleEntry(org.wso2.healthcare.integration.fhir.model.Resource resource,
                                         FHIRConnectorContext connectorContext) {
        if (resource == null) return;// cannot add resource if resource is null
        org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry = new org.hl7.fhir.r4.model.Bundle.BundleEntryComponent();
        entry.setResource(resource.unwrap());
        addBundleEntryComponent(entry, connectorContext);
    }

    public void addBundleEntryComponent(org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry,
                                        FHIRConnectorContext connectorContext) {
        if (entry != null) {
            // Check whether the resource is already in the bundle
            Resource entryResource = entry.getResource();
            String resourceType = entryResource.getResourceType().name();
            // if the resource does not contain ID, we does not check for duplications
            if (StringUtils.isNotEmpty(entryResource.getId())) {
                if (containsInTracker(resourceType, entryResource.getId())) {
                    // If the resource in same type with same id is already in the Bundle, it's a duplicate
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Duplicate resource found. Will not be added to the Bundle. Resource : " +
                                FHIRConnectorUtils.resourceBasicDetailsToString(entryResource));
                    }
                    return;
                } else {
                    // Add record
                    addToTracker(entryResource);
                }
            }
            fhirBundle.addEntry(entry);
            fhirBundle.setTotal(fhirBundle.getTotal() + 1);

            // Update resource tracker
            FHIRHealthcareMessageContext healthcareMsgCtx = connectorContext.getFHIRHealthcareMessageContext();
            if (healthcareMsgCtx != null) {
                Resource resource = entry.getResource();
                if (resource != null) {
                    healthcareMsgCtx.getResourceTracker().addResource(resource);
                }
            }

            if (StringUtils.isEmpty(entry.getFullUrl()) &&
                    StringUtils.isNotEmpty(entryResource.getId()) && StringUtils.isNotEmpty(connectorContext.getBaseUrl())) {
                String fullUrl = connectorContext.getBaseUrl() + resourceType + "/" + entryResource.getId();
                entry.setFullUrl(fullUrl);
            }
        }
    }

    public void setEntry(List<org.hl7.fhir.r4.model.Bundle.BundleEntryComponent> entryList) {
        this.fhirBundle.setEntry(entryList);
    }

    public List<org.hl7.fhir.r4.model.Bundle.BundleEntryComponent> getEntry() {
        return this.fhirBundle.getEntry();
    }

    private org.hl7.fhir.r4.model.Bundle.BundleEntryComponent getBundleEntry(
            Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entryComponent = new org.hl7.fhir.r4.model.Bundle.BundleEntryComponent();

        connectorInputParameters.put("contentReferenceAttrPrefix", "entry.link.");
        entryComponent.addLink(getBundleLink(connectorInputParameters, connectorContext));

        entryComponent.setFullUrl(FHIRDataTypeUtils.getUri(operationPrefix + "entry.fullUrl.", connectorInputParameters,
                connectorContext));

        entryComponent.setResource(FHIRDataTypeUtils.getResource(operationPrefix + "entry.resource",
                connectorInputParameters, connectorContext));

        entryComponent.setSearch(getBundleEntrySearch(connectorInputParameters, connectorContext));

        entryComponent.setRequest(getBundleEntryRequest(connectorInputParameters, connectorContext));

        entryComponent.setResponse(getBundleEntryResponse(connectorInputParameters, connectorContext));
        return (entryComponent.isEmpty() ? null : entryComponent);
    }

    public void setSignature(Map<String, String> connectorInputParameters, FHIRConnectorContext connectorContext)
            throws FHIRConnectException {
        this.fhirBundle.setSignature(FHIRDataTypeUtils.getSignature(operationPrefix + "signature.",
                connectorInputParameters, connectorContext));
    }

    public Signature getSignature() {
        return this.fhirBundle.getSignature();
    }

    /**
     * Special added operation for UX
     * @param params
     * @param resource
     * @throws FHIRConnectException
     */
    public void addResource(Map<String, String> params, org.wso2.healthcare.integration.fhir.model.Resource resource) throws FHIRConnectException {
        if (resource == null) {
            throw new FHIRConnectException("Null Resource cannot be added to Bundle");
        }
        org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry = fhirBundle.addEntry();
        entry.setResource(resource.unwrap());
    }

    /**
     * Add resource record to tracker
     *
     * @param resource
     */
    public void addToTracker(Resource resource) {
        String resourceType = resource.getResourceType().name();
        ConcurrentHashMap<String, Boolean> resourceMap =
                resourceTracker.computeIfAbsent(resourceType, k -> new ConcurrentHashMap<>());
        resourceMap.put(resource.getId(), Boolean.TRUE);
    }

    /**
     * Check whether the resource record is available in the tracker
     *
     * @param resourceType
     * @param id
     * @return
     */
    public boolean containsInTracker(String resourceType, String id) {
        ConcurrentHashMap<String, Boolean> resourceMap = resourceTracker.get(resourceType);
        if (resourceMap != null && id != null) {
            return resourceMap.get(id) != null;
        }
        return false;
    }

    private void populatePaginationLinks(FHIRConnectorContext connectorContext) {
        //decode request url
        String incomingUrl = connectorContext.getIncomingRequestUrl();
        String queryParamStr = null;
        String resourceContext;

        int qMarkIndex = incomingUrl.indexOf('?');
        if (qMarkIndex >= 0) {
            queryParamStr = incomingUrl.substring(qMarkIndex + 1);
            resourceContext = incomingUrl.substring(0, qMarkIndex);
        } else {
            resourceContext = incomingUrl;
        }

        // Requester may put multiple '/' at the end
        while (resourceContext.endsWith("/")) {
            resourceContext = resourceContext.substring(0, (resourceContext.length() - 1));
        }
        String resourceName = resourceContext.substring(resourceContext.lastIndexOf('/') + 1);
        String searchBaseURL = connectorContext.getBaseUrl() + resourceName;

        int currentOffset = 0;
        int currentCount = 10;
        StringBuilder commonUrl = new StringBuilder().append('?');
        if (queryParamStr != null) {
            String[] params = queryParamStr.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    if ("_offset".equals(keyValue[0])) {
                        currentOffset = Integer.parseInt(keyValue[1]);
                    } else if ("_count".equals(keyValue[0])) {
                        currentCount = Integer.parseInt(keyValue[1]);
                    } else {
                        if (commonUrl.length() > 1) {
                            commonUrl.append('&');
                        }
                        commonUrl.append(param);
                    }
                }
            }
        }
        if (commonUrl.length() > 1) {
            commonUrl.append('&');
        }
        String commonUrlStr = commonUrl.toString();
        String next = commonUrlStr.concat("_offset=" + (currentOffset + currentCount) + "&_count=" + currentCount);
        String self = commonUrlStr.concat("_offset=" + currentOffset + "&_count=" + currentCount);

        int entryCount = 0;
        for (org.hl7.fhir.r4.model.Bundle.BundleEntryComponent entry : fhirBundle.getEntry()) {
            Resource resource = entry.getResource();
            if (resource != null && resourceName.equals(resource.getResourceType().name())) {
                ++ entryCount;
            }
        }

        org.hl7.fhir.r4.model.Bundle.BundleLinkComponent selfLinkComponent =
                new org.hl7.fhir.r4.model.Bundle.BundleLinkComponent().setRelation("self").setUrl(searchBaseURL + self);
        fhirBundle.addLink(selfLinkComponent);

        if (entryCount >= currentCount) {
            org.hl7.fhir.r4.model.Bundle.BundleLinkComponent nextLinkComponent =
                    new org.hl7.fhir.r4.model.Bundle.BundleLinkComponent().setRelation("next").setUrl(searchBaseURL + next);
            fhirBundle.addLink(nextLinkComponent);
        }

        if (currentOffset >= currentCount) {
            String previous = commonUrlStr.concat("_offset=" + (currentOffset - currentCount) + "&_count=" + currentCount);
            org.hl7.fhir.r4.model.Bundle.BundleLinkComponent previousLinkComponent =
                    new org.hl7.fhir.r4.model.Bundle.BundleLinkComponent().setRelation("previous").setUrl(searchBaseURL + previous);
            fhirBundle.addLink(previousLinkComponent);
        }
    }
}