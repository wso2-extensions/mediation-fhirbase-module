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

/**
 * Implementation of the bundle operation
 */
package org.wso2.healthcare.integration.fhir.operations.basic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConstants;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.core.FHIRQueryOperation;
import org.wso2.healthcare.integration.fhir.core.Query;
import org.wso2.healthcare.integration.fhir.model.type.DataType;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Implementation of addElement operation
 */
public class FHIRAddExtension extends FHIRQueryOperation {

    private static Log LOG = LogFactory.getLog(FHIRAddExtension.class);
    @Override
    public String getOperationName() {
        return "addExtension";
    }

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext context,
                           HashMap<String, String> configuredParams, Query query) throws FHIRConnectException {

        Extension extension;
        String sourceElementName = configuredParams.get(FHIRConstants.FHIR_PARAM_SOURCE_OBJECT_ID);
        if (sourceElementName == null || sourceElementName.trim().isEmpty()) {
            //Create extension from parameters
            extension = FHIRDataTypeUtils.getExtension("", configuredParams, context);
        } else {
            DataType dataObj = context.getDataObject(sourceElementName.trim());
            if (dataObj == null || !(dataObj.unwrap() instanceof Extension)) {
                throw new FHIRConnectException("Unable to locate Extension source object with name : " + sourceElementName);
            }
            extension = (Extension) dataObj.unwrap();
        }

        List<Base> result = query.evaluate();
        if (result.size() == 1) {
            Base resultData = result.get(0);
            if (resultData instanceof DomainResource) {
                ((DomainResource) resultData).addExtension(extension);
            } else if (resultData instanceof Element) {
                ((Element) resultData).addExtension(extension);
            } else {
                LOG.warn("Unable to add extension to element of type :" + resultData.fhirType());
            }
        } else {
            //TODO log a warning or throw
        }

    }
}
