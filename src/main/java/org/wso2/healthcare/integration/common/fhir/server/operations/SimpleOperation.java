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

package org.wso2.healthcare.integration.common.fhir.server.operations;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext;
import org.wso2.healthcare.integration.common.fhir.server.AbstractOperation;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.HTTPInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.OperationInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SimpleOperationInfo;

/**
 * Simple default implementation of an operation
 */
public class SimpleOperation extends AbstractOperation {

    public SimpleOperation(String name) {
        super(name);
    }

    @Override
    public OperationInfo preProcess(ResourceAPI resourceAPI, HTTPInfo httpInfo, MessageContext messageContext) {
        return new SimpleOperationInfo(getName(), true);
    }

    @Override
    public void postProcess(OperationInfo operationInfo, AbstractFHIRMessageContext fhirMessageContext,
                            FHIRRequestInfo requestInfo, MessageContext messageContext) {
        // nothing to do here
    }
}
