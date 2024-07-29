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

package org.wso2.healthcare.integration.fhir.consentmgt;

import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Property;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds the implementation to process consent decisions passed to the integration layer.
 */
public class DecisionProcessor {

    public static Resource processConsentDecision(String decisionInBinary, Resource resource) throws FHIRConnectException {
        Resource newResource = null;
        Map<String, List<Bundle.BundleEntryComponent>> resourceMap = new HashMap<>();
        if (resource.fhirType().equals("Bundle")) {
            List<Bundle.BundleEntryComponent> entry = ((Bundle) resource).getEntry();
            for (Bundle.BundleEntryComponent bundleEntryComponent : entry) {
                resourceMap.computeIfAbsent(bundleEntryComponent.getResource().getResourceType().toString(),
                        k -> new ArrayList<>());
                resourceMap.get(bundleEntryComponent.getResource().getResourceType().toString())
                        .add(bundleEntryComponent);
            }
        } else {
            newResource = ResourceFactory.createResource(resource.fhirType());
        }
        String decision = new String(new Base64().decode(decisionInBinary));
        JSONObject decisionObj;
        JSONArray responseArr;
        boolean isBundleModified = false;
        try {
            decisionObj = new JSONObject(decision);
            responseArr = decisionObj.getJSONArray("Response");
            for (int i = 0; i < responseArr.length(); i++) {
                JSONObject response = (JSONObject) responseArr.get(i);
                String decisionVal = (String) response.get("Decision");
                JSONArray obligations = (JSONArray) response.get("Obligations");
                for (int j = 0; j < obligations.length(); j++) {
                    JSONObject obligation = (JSONObject) obligations.get(j);
                    JSONArray attributeAssignments = (JSONArray) obligation.get("AttributeAssignment");
                    Map<String, List<String>> resourceAttributeMap = new HashMap<>();
                    for (int k = 0; k < attributeAssignments.length(); k++) {
                        JSONObject attributeAssignment = (JSONObject) attributeAssignments.get(k);
                        String attributeId = (String) attributeAssignment.get("AttributeId");
                        //resource type
                        String resourceType = (String) attributeAssignment.get("DataType");
                        boolean isAllResourceFields = false;
                        if (FHIRConstants.ALL_RESOURCE_FIELDS.equals(attributeId)) {
                            isAllResourceFields = true;
                        }
                        if (!resource.fhirType().equals("Bundle") && resource.fhirType().equals(resourceType)) {
                            Property namedProperty = resource.getNamedProperty(attributeId);
                            if (decisionVal.equals(FHIRConstants.DENY)) {
                                if (!isAllResourceFields && namedProperty != null && namedProperty.hasValues()) {
                                    for (Base value : namedProperty.getValues()) {
                                        newResource.setProperty(attributeId, value);
                                    }
                                }
                            } else if (decisionVal.equals(FHIRConstants.PERMIT)) {
                                // consider all resource fields to be filtered
                                if (isAllResourceFields) {
                                    return ResourceFactory.createResource(resource.fhirType());
                                }
                                //todo remove the denied attributed from the original payload
                                if (namedProperty != null) {
                                    resource.setProperty(attributeId, null);
                                }
                            }
                        } else {
                            resourceAttributeMap.computeIfAbsent(resourceType, k1 -> new ArrayList<>());
                            resourceAttributeMap.get(resourceType).add(attributeId);
                        }
                    }
                    if (resource.fhirType().equals("Bundle")) {
                        for (String resourceType : resourceAttributeMap.keySet()) {
                            List<Bundle.BundleEntryComponent> bundleEntryComponents = resourceMap.get(resourceType);
                            List<String> attributes = resourceAttributeMap.get(resourceType);
                            if (bundleEntryComponents != null) {
                                for (Bundle.BundleEntryComponent bundleEntryComponent : bundleEntryComponents) {
                                    Resource entryComponentResource = bundleEntryComponent.getResource();
                                    Resource newEntryComponentResource = ResourceFactory
                                            .createResource(entryComponentResource.fhirType());
                                    //processing obligations for bundle resource
                                    if (decisionVal.equals(FHIRConstants.DENY)) {
                                        for (String attribute : attributes) {
                                            if (FHIRConstants.ALL_RESOURCE_FIELDS.equals(attribute)) {
                                                break;
                                            }
                                            Property namedProperty = entryComponentResource.getNamedProperty(attribute);
                                            if (namedProperty != null && namedProperty.hasValues()) {
                                                for (Base value : namedProperty.getValues()) {
                                                    newEntryComponentResource.setProperty(attribute, value);
                                                    isBundleModified = true;
                                                }
                                            }

                                        }
                                        if (!newEntryComponentResource.isEmpty()) {
                                            bundleEntryComponent.setResource(newEntryComponentResource);
                                        }
                                    } else if (decisionVal.equals(FHIRConstants.PERMIT)) {
                                        Field[] fields = entryComponentResource.getClass().getDeclaredFields();
                                        if (!attributes.contains(FHIRConstants.ALL_RESOURCE_FIELDS)) {
                                            for (Field field : fields) {
                                                if (!attributes.contains(field.getName())) {
                                                    Property namedProperty = entryComponentResource
                                                            .getNamedProperty(field.getName());
                                                    if (namedProperty != null && namedProperty.hasValues()) {
                                                        for (Base value : namedProperty.getValues()) {
                                                            newEntryComponentResource.setProperty(field.getName(), value);
                                                            isBundleModified = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        bundleEntryComponent.setResource(newEntryComponentResource);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new FHIRConnectException("Error occurred while processing consent decision.", e);
        }
        //default server consent behavior(optin/optout)
        if (responseArr.length() == 0 || newResource == null || newResource.isEmpty()) {
            //returning a empty resource response
            if (resource.fhirType().equals("Bundle") && isBundleModified) {
                return resource;
            }
            return ResourceFactory.createResource(resource.fhirType());
        }
        return newResource;
    }
}
