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

package org.wso2.healthcare.integration.common.test.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang3.StringUtils;
import org.jaxen.JaxenException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XML related utilities
 */
public class XMLTestUtils {

    /**
     * Utility function to transform XML string to an OMElement
     *
     * @param xmlString
     * @return
     * @throws XMLStreamException
     */
    public static OMElement stringToOMElement(String xmlString) throws XMLStreamException {
        XMLStreamReader xmlReader = StAXUtils.createXMLStreamReader(new StringReader(xmlString));
        StAXBuilder builder = new StAXOMBuilder(xmlReader);
        return builder.getDocumentElement();
    }

    /**
     * Utility function to transform XML input stream to an OMElement
     *
     * @param inputStream
     * @return
     * @throws XMLStreamException
     */
    public static OMElement toOMElement(InputStream inputStream) throws XMLStreamException {
        XMLStreamReader xmlReader = StAXUtils.createXMLStreamReader(new InputStreamReader(inputStream));
        StAXBuilder builder = new StAXOMBuilder(xmlReader);
        return builder.getDocumentElement();
    }

    public static List<OMNode> evaluateXPath(OMElement element, String xPath, HashMap<String, String> namespaces) throws JaxenException {
        AXIOMXPath axiomxPath = new AXIOMXPath(xPath);

        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            axiomxPath.addNamespace(entry.getKey(), entry.getValue());
        }

        return (List<OMNode>) axiomxPath.selectNodes(element);
    }

    /**
     * Function to compare two OMElements
     *
     * @param element1
     * @param element2
     * @return true if the content of given OMElements are equal
     */
    public static boolean compareOMElement(OMNode element1, OMNode element2) {

        if (element1 == null || element2 == null) {
            return false;
        }

        if (element1.getType() == OMNode.ELEMENT_NODE && element2.getType() == OMNode.ELEMENT_NODE &&
                StringUtils.equals(((OMElement) element1).getLocalName(), ((OMElement)element2).getLocalName())) {
            Iterator iterator1 = ((OMElement) element1).getChildren() ;
            Iterator iterator2 = ((OMElement) element2).getChildren();

            if (!iterator1.hasNext() && !iterator2.hasNext()) {
                // both elements does not have child elements
                return true;
            }
            while (iterator1.hasNext() && iterator2.hasNext()) {
                Object obj1 = iterator1.next();
                Object obj2 = iterator2.next();

                // Ignore whitespaces
                while (obj1 instanceof OMText && ((OMText) obj1).getText().trim().isEmpty() && iterator1.hasNext()) {
                    obj1 = iterator1.next();
                }
                while (obj2 instanceof OMText && ((OMText) obj2).getText().trim().isEmpty() && iterator2.hasNext()) {
                    obj2 = iterator2.next();
                }

                boolean result = compareOMElement((OMNode) obj1, (OMNode) obj2);
                if (!result) {
                    return false;
                }
            }
            return true;
        } else if (element1.getType() == OMNode.TEXT_NODE && element2.getType() == OMNode.TEXT_NODE) {
            return StringUtils.equals(((OMText)element1).getText(), ((OMText)element2).getText());
        } else {
            return false;
        }
    }
}
