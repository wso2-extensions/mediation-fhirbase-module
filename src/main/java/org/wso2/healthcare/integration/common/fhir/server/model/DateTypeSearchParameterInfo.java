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

package org.wso2.healthcare.integration.common.fhir.server.model;

import org.apache.axiom.om.OMElement;
import org.wso2.healthcare.integration.common.Constants;

import javax.xml.namespace.QName;
import java.time.LocalDateTime;

/**
 * Holds information about Date type search parameters
 */
public class DateTypeSearchParameterInfo  extends SearchParameterInfo {

    private final String name;
    private String originalValue = null;
    private String prefix = "eq";
    private String date = null;
    private LocalDateTime localDateTime;

    public DateTypeSearchParameterInfo(String name) {
        super(Constants.FHIR_DATATYPE_DATE);
        this.name = name;
    }

    @Override
    public OMElement serialize() {

        OMElement element = super.serialize();
        element.addChild(createSimpleElement("value", originalValue));
        element.addChild(createSimpleElement("prefix", prefix));
        element.addChild(createSimpleElement("dateTime", date));

        OMElement decodedDTElement = getOMFactory().createOMElement(new QName("decodedDateTime"));

        OMElement dateElement = getOMFactory().createOMElement(new QName("date"));
        dateElement.addChild(createSimpleElement("year", String.valueOf(localDateTime.getYear())));
        dateElement.addChild(createSimpleElement("month", String.valueOf(localDateTime.getMonthValue())));
        dateElement.addChild(createSimpleElement("day", String.valueOf(localDateTime.getDayOfMonth())));
        decodedDTElement.addChild(dateElement);

        OMElement timeElement = getOMFactory().createOMElement(new QName("time"));
        timeElement.addChild(createSimpleElement("hour", String.valueOf(localDateTime.getHour())));
        timeElement.addChild(createSimpleElement("minute", String.valueOf(localDateTime.getMinute())));
        timeElement.addChild(createSimpleElement("second", String.valueOf(localDateTime.getSecond())));
        decodedDTElement.addChild(timeElement);

        element.addChild(decodedDTElement);
        return element;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    protected String getRootElementName() {

        return getName();
    }

    public String getOriginalValue() {

        return originalValue;
    }

    public void setOriginalValue(String originalValue) {

        this.originalValue = originalValue;
    }

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public LocalDateTime getLocalDateTime() {

        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {

        this.localDateTime = localDateTime;
    }
}
