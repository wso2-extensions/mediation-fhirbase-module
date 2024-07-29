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

package org.wso2.healthcare.integration.fhir.operations.basic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.MarkdownType;
import org.hl7.fhir.r4.model.OidType;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.UnsignedIntType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.UrlType;
import org.hl7.fhir.r4.model.UuidType;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRCreateAndAddDataTypeOperation;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.model.type.PrimitiveDataType;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.HashMap;

/**
 * Operation to create Primitive Data Types
 */
public class FHIRCreatePrimitiveDataType extends FHIRCreateAndAddDataTypeOperation {

    private static Log log = LogFactory.getLog(FHIRCreatePrimitiveDataType.class);
    private String dataType;

    @Override
    protected DataType executeAndReturn(MessageContext messageContext,
            FHIRConnectorContext fhirConnectorContext, HashMap<String, String> configuredParams, String objectId)
            throws FHIRConnectException {
        DataType dataObject;
        String prefix = "value";
        switch (getDataType()) {
            case "boolean":
                BooleanType booleanType = FHIRDataTypeUtils.getBooleanType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, booleanType);
                break;
            case "integer":
                IntegerType integerType = FHIRDataTypeUtils.getIntegerType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, integerType);
                break;
            case "string":
                StringType stringType = FHIRDataTypeUtils.getStringType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, stringType);
                break;
            case "decimal":
                DecimalType decimalType = FHIRDataTypeUtils.getDecimalType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, decimalType);
                break;
            case "uri":
                UriType uriType = FHIRDataTypeUtils.getUriType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, uriType);
                break;
            case "url":
                UrlType urlType = FHIRDataTypeUtils.getUrlType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, urlType);
                break;
            case "canonical":
                CanonicalType canonicalType =
                        FHIRDataTypeUtils.getCanonicalType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, canonicalType);
                break;
            case "base64Binary":
                Base64BinaryType base64BinaryType =
                        FHIRDataTypeUtils.getBase64BinaryType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, base64BinaryType);
                break;
            case "instant":
                InstantType instantType = FHIRDataTypeUtils.getInstantType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, instantType);
                break;
            case "date":
                DateType dateType = FHIRDataTypeUtils.getDateType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, dateType);
                break;
            case "dateTime":
                DateTimeType dateTimeType = FHIRDataTypeUtils.getDateTimeType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, dateTimeType);
                break;
            case "time":
                TimeType timeType = FHIRDataTypeUtils.getTimeType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, timeType);
                break;
            case "code":
                CodeType codeType = FHIRDataTypeUtils.getCodeType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, codeType);
                break;
            case "oid":
                OidType oidType = FHIRDataTypeUtils.getOidType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, oidType);
                break;
            case "id":
                IdType idType = FHIRDataTypeUtils.getIdType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, idType);
                break;
            case "markdown":
                MarkdownType markdownType = FHIRDataTypeUtils.getMarkdownType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, markdownType);
                break;
            case "unsignedInt":
                UnsignedIntType unsignedIntType =
                        FHIRDataTypeUtils.getUnsignedIntType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, unsignedIntType);
                break;
            case "positiveInt":
                PositiveIntType positiveIntType = FHIRDataTypeUtils
                        .getPositiveIntType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, positiveIntType);
                break;
            case "uuid":
                UuidType uuidType = FHIRDataTypeUtils.getUuidType(prefix, configuredParams, fhirConnectorContext);
                dataObject = new PrimitiveDataType(objectId, uuidType);
                break;
            default:
                throw new FHIRConnectException("Unknown Type : " + getDataType());
        }
        return dataObject;
    }

    @Override
    public String getOperationName() {
        return "createPrimitiveDataType";
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
