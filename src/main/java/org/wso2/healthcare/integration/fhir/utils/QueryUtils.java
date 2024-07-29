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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Property;
import org.hl7.fhir.r4.model.Type;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.core.Query;
import org.wso2.healthcare.integration.fhir.model.type.BaseType;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.ElementType;

import java.util.List;

public class QueryUtils {

    private static Log LOG = LogFactory.getLog(QueryUtils.class);

    /**
     * Util function to evaluate FHIRPath query and place given data object
     *
     * @param query
     * @param dataObject
     * @throws FHIRConnectException
     */
    public static void evaluateQueryAndPlace(Query query, BaseType dataObject) throws FHIRConnectException {
        try {
            //retrieve parent path
            String fhirPath = query.getFhirPath();
            List<Base> parentList = getParentElements(query);
            String childElementName = fhirPath.substring(fhirPath.lastIndexOf(".") + 1);
            String indexStr = null;
            int startIndex = childElementName.indexOf('[');
            int endIndex = childElementName.indexOf(']');
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                indexStr = childElementName.substring(startIndex + 1, endIndex);
                childElementName = childElementName.substring(0, startIndex);
            }
            // If there are multiple parents, then element will be added to all the parent elements. (This is derived
            // from FHIRPath behavior where fhirPath evaluation for child element, without selecting a parent from
            // an array, it will return all the child elements that conform the FHIRPath)
            for (Base parent : parentList) {
                try {
                    placeChildTypeOrElement(parent, childElementName, indexStr, dataObject);
                } catch (FHIRConnectException e) {
                    throw new FHIRConnectException("Error occurred while setting the element to FHIR path: " + fhirPath, e);
                }
            }
        } catch (FHIRException e) {
            throw new FHIRConnectException("Error occurred while populating evaluating and placing data element " +
                    "to FHIR path: " + query.getFhirPath(), e);
        }
    }

    /**
     * Gets parent elements for given fhir attribute
     * @param query
     * @return
     * @throws FHIRConnectException
     */
    public static List<Base> getParentElements(Query query) throws FHIRConnectException {
        String fhirPath = query.getFhirPath();
        List<Base> parentList = query.evaluateForParent();
        if (parentList.isEmpty()) {
            Query parentQuery = new Query();
            parentQuery.setFhirPath(fhirPath.substring(0, fhirPath.lastIndexOf('.')));
            parentQuery.setSrcResource(query.getSrcResource());
            try {
                createObjectTree(parentQuery);
            } catch (FHIRConnectException e) {
                throw new FHIRConnectException("Error occurred while creating object tree for the FHIRPath: "
                        + parentQuery.getFhirPath(), e);
            }
            // reattempt to get parent after creating object tree
            parentList = query.evaluateForParent();
        }
        return parentList;
    }

    public static void placeExtension(Query query, BaseType dataObject) throws FHIRConnectException {

        if (dataObject.getBaseType() instanceof DataType) {
            Type unwrappedType = ((DataType) dataObject.getBaseType()).unwrap();
            if (unwrappedType instanceof Extension) {
                List<Base> result = query.evaluate();
                if (result.size() == 1) {
                    Base resultData = result.get(0);
                    if (resultData instanceof DomainResource) {
                        ((DomainResource) resultData).addExtension((Extension) unwrappedType);
                    } else if (resultData instanceof Element) {
                        ((Element) resultData).addExtension((Extension) unwrappedType);
                    } else {
                        LOG.warn("Unable to add extension to element of type :" + resultData.fhirType());
                    }
                } else {
                    LOG.warn("Couldn't find evaluated results for the fhir path: " + query.getFhirPath());
                }
            }
        }
    }

    private static void createObjectTree(Query query) throws FHIRConnectException {
        List<Base> result = query.evaluate();
        if (result.isEmpty()) {
            //Since the target element not available, need to create it. Hence first need to create retrieve it's
            // parent
            String queryPath = query.getFhirPath();
            //Create query for parent
            Query parentQuery = new Query();
            parentQuery.setFhirPath(queryPath.substring(0, queryPath.lastIndexOf('.')));
            parentQuery.setSrcResource(query.getSrcResource());
            createObjectTree(parentQuery);
            // reattempt to get parent after creating object tree
            result = query.evaluateForParent();
            if (!result.isEmpty()) {
                String childElementName = queryPath.substring(queryPath.lastIndexOf(".") + 1);
                Base parent = result.get(0);
                //Check whether it is an array expression
                if (childElementName.endsWith("]")) {
                    int elementCount = Integer.parseInt(
                            childElementName.substring(childElementName.indexOf('[') + 1, childElementName.lastIndexOf(']')));
                    childElementName = childElementName.substring(0, childElementName.indexOf('['));
                    Property property = parent.getNamedProperty(childElementName);
                    int currentElementCount = property.getValues().size();
                    if (currentElementCount >= property.getMaxCardinality()) {
                        throw new FHIRConnectException("Current element count ("+
                                currentElementCount +") has reached maximum cardinality (" + property.getMaxCardinality() +
                                ") hence cannot add more child elements to : " +  query.getFhirPath());
                    }
                }
                parent.addChild(childElementName);
            } else {
                LOG.warn("Failed to populate parent element in the object tree: " + parentQuery.getFhirPath());
            }
        }
    }

    private static void placeChildTypeOrElement(Base parent, String childElementName, String indexStr, BaseType dataObject)
            throws FHIRConnectException {
        Property childProp = parent.getChildByName(childElementName);
        if (childProp != null) {
            if (dataObject.getBaseType() instanceof DataType) {
                Type unwrappedType = ((DataType) dataObject.getBaseType()).unwrap();
                if (unwrappedType != null) {
                    if (indexStr != null) {
                        try {
                            int index = Integer.parseInt(indexStr) - 1;
                            if (childProp.getValues().size() > index) {
                                parent.getNamedProperty(childElementName).getValues().set(index, unwrappedType);
                            } else {
                                parent.setProperty(childElementName, unwrappedType);
                            }
                        } catch (NumberFormatException e) {
                            throw new FHIRConnectException("Erroneous array index in FHIRPath.", e);
                        }
                    } else {
                        parent.setProperty(childElementName, unwrappedType);
                    }
                }
            } else if (dataObject.getBaseType() instanceof ElementType) {
                Element unwrappedElement = ((ElementType) dataObject.getBaseType()).unwrap();
                if (unwrappedElement != null) {
                    if (indexStr != null) {
                        try {
                            int index = Integer.parseInt(indexStr) - 1;
                            if (childProp.getValues().size() > index) {
                                parent.getNamedProperty(childElementName).getValues().set(index, unwrappedElement);
                            } else {
                                parent.setProperty(childElementName, unwrappedElement);
                            }
                        } catch (NumberFormatException e) {
                            throw new FHIRConnectException("Erroneous array index in FHIRPath.", e);
                        }
                    } else {
                        parent.setProperty(childElementName, unwrappedElement);
                    }
                }
            }
        } else {
            throw new FHIRConnectException("Child with name :" + childElementName + " not found.");
        }
    }

}
