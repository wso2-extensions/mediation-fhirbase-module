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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateException;
import org.wso2.healthcare.integration.fhir.template.model.PayloadType;
import org.wso2.healthcare.integration.fhir.template.model.PropertyPayloadModel;
import org.wso2.healthcare.integration.fhir.template.model.Segment;
import org.wso2.healthcare.integration.fhir.template.model.WriteDataField;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class used for payload building for FHIR write templates.
 */
public class PayloadBuilderUtil {

    /**
     * Builds payload structure defined for a specific property of the message context in the write templates.
     *
     * @param propertyPayloadModel holds payload information for a specific property
     */
    public static void buildPayloadSkeleton(PropertyPayloadModel propertyPayloadModel) {
        if (PayloadType.XML.equals(propertyPayloadModel.getPayloadType())) {
            for (WriteDataField property : propertyPayloadModel.getWriteDataFields()) {
                List<Segment> segmentList = property.getSegmentList();
                Iterator childrenWithLocalName =
                        ((OMElement) propertyPayloadModel.getPayload()).getChildrenWithLocalName(
                                segmentList.get(0).getSegmentName());
                OMElement root;
                if (childrenWithLocalName.hasNext()) {
                    root = (OMElement) childrenWithLocalName.next();
                } else {
                    if (segmentList.get(0).hasNamespace()) {
                        root = OMAbstractFactory.getOMFactory().createOMElement(new QName(segmentList.get(0).getNamespaceUri(),
                                segmentList.get(0).getSegmentName(), segmentList.get(0).getNamespace()));
                    } else {
                        root = OMAbstractFactory.getOMFactory().createOMElement(new QName(segmentList.get(0).getSegmentName()));
                    }
                    ((OMElement) propertyPayloadModel.getPayload()).addChild(root);
                }
                if (segmentList.size() > 1) {
                    OMElement omElement = PayloadBuilderUtil.buildOMElementTree(root, segmentList, 1);
                    if (omElement != null) {
                        property.setPayloadStructure(omElement);
                    }
                }
            }
        } else if (PayloadType.JSON.equals(propertyPayloadModel.getPayloadType())) {
            for (WriteDataField property : propertyPayloadModel.getWriteDataFields()) {
                List<Segment> segmentList = property.getSegmentList();
                try {
                    if (propertyPayloadModel.getPayload() == null) {
                        propertyPayloadModel.setPayload(new JSONObject().put(segmentList.get(0).getSegmentName(),
                                new JSONArray()));
                    }
                    if (segmentList.size() > 1) {
                        property.setTargetNode(segmentList.get(segmentList.size() - 1).getSegmentName());
                        property.setPayloadStructure(PayloadBuilderUtil.buildJSONObjectTree(propertyPayloadModel.getPayload(),
                                property, segmentList, segmentList.get(0), 0));
                    }
                } catch (JSONException e) {
                    throw new TemplateException("Error occurred while parsing json paths provided in write " +
                            "FHIR templates.", e);
                }
            }
        }
    }

    /**
     * Builds OM element tree for set of xpath segments provided
     *
     * @param root     root xml element
     * @param nodeList xpath segments
     * @param position current position of the xpath segments
     * @return constructed OMElement
     */
    public static OMElement buildOMElementTree(OMElement root, List<Segment> nodeList, int position) {
        if (nodeList.size() <= position) {
            return root;
        }
        Iterator childrenWithLocalName = root.getChildrenWithLocalName(nodeList.get(position).getSegmentName());
        OMElement next;
        if (childrenWithLocalName.hasNext()) {
            next = (OMElement) childrenWithLocalName.next();
        } else {
            if (nodeList.get(position).hasNamespace()) {
                next = OMAbstractFactory.getOMFactory().createOMElement(new QName(nodeList.get(position).getNamespaceUri(),
                                nodeList.get(position).getSegmentName(),nodeList.get(position).getNamespace()));
            } else {
                next = OMAbstractFactory.getOMFactory().createOMElement(new QName(nodeList.get(position).getSegmentName()));
            }
            root.addChild(next);
        }
        position ++;
        return buildOMElementTree(next, nodeList, position);
    }

    /**
     * Builds JSON element tree for set of jsonpath segments provided
     *
     * @param root     root json element
     * @param property {@link WriteDataField} object
     * @param nodeList jsonpath segments
     * @param prevNode previous json element
     * @param position current position of json path segments
     * @return constructed json object
     * @throws JSONException
     */
    public static Object buildJSONObjectTree(Object root, WriteDataField property, List<Segment> nodeList,
                                          Segment prevNode,
                                       int position) throws JSONException {
        if (nodeList.size() <= position) {
            return root;
        }
        Object child = null;
        if (root instanceof JSONObject) {
            child = ((JSONObject) root).get(nodeList.get(position).getSegmentName());
        } else if (root instanceof JSONArray) {
            if (((JSONArray) root).length() > 0) {
                child = ((JSONArray) root).get(((JSONArray) root).length() - 1);
            }
        }
        if (child == null) {
            child = new JSONObject().put(nodeList.get(position).getSegmentName(), new JSONObject());
            if (root instanceof JSONObject) {
                ((JSONObject) root).put(prevNode.getSegmentName(), child);
            } else if (root instanceof JSONArray) {
                ((JSONArray) root).put(child);
            }
        }
        position++;
        if (position == nodeList.size()) {
            property.setParentNode(root);
        }
        return buildJSONObjectTree(child, property, nodeList, nodeList.get(position - 1), position);
    }

}
