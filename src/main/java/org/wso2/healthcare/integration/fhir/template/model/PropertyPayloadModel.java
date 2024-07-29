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
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.healthcare.integration.fhir.template.util.PayloadBuilderUtil;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds constructed payload information stored in a message ctx property
 */
public class PropertyPayloadModel implements Cloneable {
    private List<WriteDataField> writeDataFields;
    private Object payload;
    private PayloadType payloadType;

    public PropertyPayloadModel(WriteDataField property) {
        this.writeDataFields = new ArrayList<>();
        this.payloadType = property.getPayloadType();
        this.addProperty(property);
        if (PayloadType.XML.equals(payloadType)) {
            payload = OMAbstractFactory.getOMFactory().createOMElement(new QName(property.getPropName()));
        }
    }

    public List<WriteDataField> getWriteDataFields() {
        return writeDataFields;
    }

    public void setWriteDataFields(List<WriteDataField> writeDataFields) {
        this.writeDataFields = writeDataFields;
    }

    public void addProperty(WriteDataField property) {
        this.writeDataFields.add(property);
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object copy = super.clone();
        if (this.payload instanceof OMElement) {
            ((PropertyPayloadModel) copy).setPayload(((OMElement) this.payload).cloneOMElement());
            PayloadBuilderUtil.buildPayloadSkeleton((PropertyPayloadModel) copy);
        } else if (this.payload instanceof JSONObject || this.payload instanceof JSONArray) {
            ((PropertyPayloadModel) copy).setPayload(null);
            PayloadBuilderUtil.buildPayloadSkeleton((PropertyPayloadModel) copy);
        }
        return copy;
    }
}
