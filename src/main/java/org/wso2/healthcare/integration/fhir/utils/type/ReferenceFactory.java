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

package org.wso2.healthcare.integration.fhir.utils.type;

import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Type;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRDataTypeUtils;
import org.wso2.healthcare.integration.fhir.utils.TypeFactory;

import java.util.Map;

/**
 * Note: Generated Code
 * Impl class of type factory implementations used to create Reference FHIR object
 */
public class ReferenceFactory extends TypeFactory {

    public ReferenceFactory() {
    }

    public boolean isInstance(Object object) {
        return object instanceof Reference;
    }

    public boolean isComplexType(){
        return true;
    }

    public Type createObject(String path,
                                      Map<String, String> params,
                                      FHIRConnectorContext context) throws FHIRConnectException {

        return FHIRDataTypeUtils.getReference(path, params, context);
    }
}
