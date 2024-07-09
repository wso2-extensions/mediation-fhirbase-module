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

package org.wso2.healthcare.integration.common.fhir.server.search.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.FHIRPrefix;
import org.wso2.healthcare.integration.common.fhir.server.AbstractCommonSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.DateTypeSearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.QueryParamInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * _lastUpdated search parameter
 */
public class LastUpdatedSearchParameter extends AbstractCommonSearchParameter {

    private static final Log LOG = LogFactory.getLog(LastUpdatedSearchParameter.class);

    /**
     * date time formatter map : key is the length of date time string
     */
    private static final Map<Integer, DateTimeFormatter> dateTimeFormatterMap = new HashMap<>();

    static {
        /**
         * Populate date time formatter list
         * ==================================
         * Rules from FHIR Documentation:
         *
         * The date parameter format is yyyy-mm-ddThh:mm:ss[Z|(+|-)hh:mm] (the standard XML format).
         * Technically, this is any of the date, dateTime, and instant data types;
         *      e.g. Any degree of precision can be provided, but it SHALL be populated from the left
         *      (e.g. can't specify a month without a year),
         *      except that the minutes SHALL be present if an hour is present,
         *      and you SHOULD provide a time zone if the time part is present.
         *
         * Note: Time can consist of hours and minutes with no seconds,
         *      unlike the XML Schema dateTime type. Some user agents may escape the : characters in the URL, and
         *      servers SHALL handle this correctly.
         */
        DateTimeFormatter yearDTF =
                new DateTimeFormatterBuilder()
                        .appendPattern("yyyy")
                        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter();
        dateTimeFormatterMap.put(4, yearDTF);


        DateTimeFormatter yearToMonthDTF =
                new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM")
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter();
        dateTimeFormatterMap.put(7, yearToMonthDTF);

        dateTimeFormatterMap.put(10, DateTimeFormatter.ISO_LOCAL_DATE);
        dateTimeFormatterMap.put(16, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dateTimeFormatterMap.put(19, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dateTimeFormatterMap.put(25, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public LastUpdatedSearchParameter() {

        super(Constants.FHIR_SEARCH_PARAM_LAST_UPDATED);
    }

    @Override
    public String getDefaultValue() {

        return null;
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI,
                                                     FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {
        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();

        QueryParamInfo paramInfo = requestInfo.getHttpInfo().findQueryParam(Constants.FHIR_SEARCH_PARAM_LAST_UPDATED);
        if (paramInfo != null) {
            FHIRPrefix prefix = extractPrefix(paramInfo.getValue());
            String dateTimeStr = paramInfo.getValue();
            if (prefix != null) {
                dateTimeStr = dateTimeStr.substring(2);
            } else {
                prefix = FHIRPrefix.EQ;
            }
            // perform date format validation
            DateTimeFormatter formatter = dateTimeFormatterMap.get(dateTimeStr.length());
            if (formatter != null) {
                LocalDateTime localDateTime = validateAndParseDateTime(formatter, dateTimeStr);
                if (localDateTime != null) {
                    DateTypeSearchParameterInfo lastUpdatedParamInfo = new DateTypeSearchParameterInfo(getName());
                    lastUpdatedParamInfo.setOriginalValue(paramInfo.getValue());
                    lastUpdatedParamInfo.setPrefix(prefix.getPrefix());
                    lastUpdatedParamInfo.setDate(dateTimeStr);
                    lastUpdatedParamInfo.setLocalDateTime(localDateTime);
                    searchParameterInfoList.add(lastUpdatedParamInfo);

                    // populate properties for backward compatibility
                    messageContext.setProperty(Constants.FHIR_SYNAPSE_PROP_LAST_UPDATED, dateTimeStr);
                    messageContext.setProperty(Constants.FHIR_SYNAPSE_PROP_LAST_UPDATED_OP, prefix.getOperation());
                } else {
                    String msg = "Invalid date/time format: \"" + dateTimeStr + "\" for _lastUpdated search parameter";
                    throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                            OpenHealthcareFHIRException.IssueType.PROCESSING,
                            OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER_VALUE, null);
                }
            } else {
                String msg = "Invalid date/time format: \"" + dateTimeStr + "\" for _lastUpdated search parameter";
                throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                        OpenHealthcareFHIRException.IssueType.PROCESSING,
                        OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER_VALUE, null);
            }
        }
        return searchParameterInfoList;
    }

    @Override
    public void postProcess(SearchParameterInfo searchParameterInfo,
                            AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) throws OpenHealthcareFHIRException {
        // nothing to perform here
    }

    private FHIRPrefix extractPrefix(String value) {
        String prefix = value.substring(0, 2);
        for (FHIRPrefix fhirPrefix : FHIRPrefix.values()) {
            if (fhirPrefix.getPrefix().equals(prefix)) return fhirPrefix;
        }
        return null;
    }

    /**
     * Function to validate and parse provided date time string against provided formatter
     *
     * @param formatter
     * @param dateTime
     * @return LocalDate instance if valid, and return null if invalid
     */
    private LocalDateTime validateAndParseDateTime(DateTimeFormatter formatter, String dateTime) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Date time is valid against pattern : " + formatter);
            }
            return localDateTime;
        } catch (DateTimeParseException e) {
            if (LOG.isDebugEnabled()) {
                LOG.info("Date time : " + dateTime + " is not invalid against pattern : " + formatter, e);
            }
            return null;
        }
    }
}
