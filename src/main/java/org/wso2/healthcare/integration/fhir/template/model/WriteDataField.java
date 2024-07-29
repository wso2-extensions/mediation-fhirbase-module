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

package org.wso2.healthcare.integration.fhir.template.model;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.wso2.healthcare.integration.fhir.FHIRConstants;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds information on the related to process a single source field in write template for a fhir attribute
 */
public class WriteDataField {

    private String propName;
    private PayloadType payloadType;
    private Object payloadStructure;
    private Map<String, String> namespaces;
    private String expression;
    private List<String> arrayPaths;
    private List<Segment> segmentList;
    private String fhirPath;
    private String targetNode;
    private Object parentNode;

    public WriteDataField(String expression, String fhirPath, Map<String, String> namespaces,
                          List<String> arrayPaths) {
        this.namespaces = namespaces;
        this.expression = expression;
        this.arrayPaths = arrayPaths;
        this.fhirPath = fhirPath;
        this.segmentList = new ArrayList<>();
        this.initialize(expression, arrayPaths);
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public Object getPayloadStructure() {
        return payloadStructure;
    }

    public void setPayloadStructure(Object payloadStructure) {
        this.payloadStructure = payloadStructure;
    }

    public String getExpression() {
        return expression;
    }

    public List<String> getArrayPaths() {
        return arrayPaths;
    }

    public String getFhirPath() {
        return fhirPath;
    }

    public void setFhirPath(String fhirPath) {
        this.fhirPath = fhirPath;
    }

    public String getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(String targetNode) {
        this.targetNode = targetNode;
    }

    public Object getParentNode() {
        return parentNode;
    }

    public void setParentNode(Object parentNode) {
        this.parentNode = parentNode;
    }

    public List<Segment> getSegmentList() {
        return segmentList;
    }

    private void initialize(String expression, List<String> arrayPaths) {
        if (expression.contains("//") || expression.startsWith("/")) {
            this.setPayloadType(PayloadType.XML);
            String xpath = "";
            if (expression.startsWith("$ctx:")) {
                if (expression.contains("//")) {
                    this.setPropName(expression.substring(expression.indexOf("$ctx:") + 5, expression.indexOf("//")));
                    xpath = expression.substring(expression.indexOf("//") + 2);
                } else if (expression.startsWith("/")) {
                    this.setPropName(arrayPaths.get(0).substring(arrayPaths.get(0).indexOf("$ctx:") + 5,
                            arrayPaths.get(0).indexOf("//")));
                    xpath = expression.substring(expression.indexOf("/") + 1);
                }
            } else if (arrayPaths.size() > 0) {
                for (String arrayPath : arrayPaths) {
                    Segment segment;
                    if (arrayPath.startsWith("$ctx:")) {
                        segment = new Segment(arrayPath.substring(arrayPath.indexOf("//") + 2));
                    } else {
                        segment = new Segment(arrayPath);
                    }
                    if (arrayPath.contains(":")) {
                        String namespace = arrayPath.substring(arrayPath.indexOf(":"));
                        if (namespaces.containsKey(namespace)) {
                            segment.setNamespace(namespace);
                            segment.setNamespaceUri(namespaces.get(namespace));
                        }
                    }
                    segmentList.add(segment);
                }
                if (expression.startsWith("//")) {
                    this.setPropName(expression.substring(expression.indexOf("$ctx:") + 5, expression.indexOf("//")));
                    xpath = expression.substring(expression.indexOf("//") + 2);
                } else if (expression.startsWith("/")) {
                    this.setPropName(arrayPaths.get(0).substring(arrayPaths.get(0).indexOf("$ctx:") + 5,
                            arrayPaths.get(0).indexOf("//")));
                    xpath = expression.substring(expression.indexOf("/") + 1);
                }
            } else if (expression.startsWith("//")) {
                this.setPropName(FHIRConstants.FHIR_WRITE_MESSAGE_BODY_PROPERTY);
                xpath = expression.substring(expression.indexOf("//") + 2);
            }
            String[] segments = xpath.split("/");
            for (String segment : segments) {
                segmentList.add(new Segment(segment));
            }
            boolean isNamespaceAvailable = false;
            for (String namespace : namespaces.keySet()) {
                if (segmentList.get(0).getSegmentName().startsWith(namespace + ":")) {
                    payloadStructure = OMAbstractFactory.getOMFactory().createOMElement(
                            new QName(namespaces.get(namespace),
                                    segmentList.get(0).getSegmentName().substring(segmentList.get(0).getSegmentName().indexOf(":") + 1), namespace));
                    isNamespaceAvailable = true;
                }
            }
            if (!isNamespaceAvailable) {
                payloadStructure = OMAbstractFactory.getOMFactory().createOMElement(
                        new QName(segmentList.get(0).getSegmentName()));
            }
        } else if (expression.contains("$.") || expression.contains("$")) {
            this.setPayloadType(PayloadType.JSON);
            String jsonPath = "";
            if (expression.startsWith("$ctx:")) {
                if (expression.contains("$.")) {
                    this.setPropName(expression.substring(expression.indexOf("$ctx:") + 5, expression.indexOf("$.")));
                    jsonPath = expression.substring(expression.indexOf("$.") + 2);
                } else if (expression.contains("$")) {
                    this.setPropName(expression.substring(expression.indexOf("$ctx:") + 5, expression.indexOf("$")));
                    jsonPath = expression.substring(expression.indexOf("$") + 1);
                }
            } else if (arrayPaths.size() > 0) {
                for (String arrayPath : arrayPaths) {
                    Segment segment;
                    if (arrayPath.startsWith("$ctx:")) {
                        segment = new Segment(arrayPath.substring(arrayPath.indexOf("$.") + 2));
                    } else {
                        segment = new Segment(arrayPath);
                    }
                    segmentList.add(segment);
                }
                if (expression.contains("$.")) {
                    this.setPropName(expression.substring(expression.indexOf("$ctx:") + 5, expression.indexOf("$.")));
                    jsonPath = expression.substring(expression.indexOf("$.") + 2);
                } else if (expression.contains("$")) {
                    this.setPropName(expression.substring(expression.indexOf("$ctx:") + 5, expression.indexOf("$")));
                    jsonPath = expression.substring(expression.indexOf("$") + 1);
                }
            } else if (expression.startsWith("$")) {
                this.setPropName(FHIRConstants.FHIR_WRITE_MESSAGE_BODY_PROPERTY);
                jsonPath = expression.substring(expression.indexOf("$") + 1);
            } else if (expression.startsWith("$.")) {
                this.setPropName(FHIRConstants.FHIR_WRITE_MESSAGE_BODY_PROPERTY);
                jsonPath = expression.substring(expression.indexOf("$.") + 2);
            }
            String[] segments = jsonPath.split("\\.");
            for (String segment : segments) {
                segmentList.add(new Segment(segment));
            }
            OMElement current = (OMElement) payloadStructure;
            int pos = 0;
            for (Segment segment : segmentList) {
                if (pos != 0 && pos != segmentList.size() - 1) {
                    current = populateOMElement(segment.getSegmentName(), current);
                }
                pos++;
            }
        }
    }

    private OMElement populateOMElement(String nodeName, OMElement omElement) {
        for (String namespace : namespaces.keySet()) {
            if (nodeName.startsWith(namespace + ":")) {
                return OMAbstractFactory.getOMFactory().createOMElement(new QName(namespaces.get(namespace),
                        nodeName.substring(nodeName.indexOf(":") + 1), namespace), omElement);
            }
        }
        return OMAbstractFactory.getOMFactory().createOMElement(new QName(nodeName), omElement);
    }

}
