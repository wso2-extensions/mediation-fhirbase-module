/*
 *  Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com).
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.healthcare.integration.fhir.operations;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRConnectException;
import org.wso2.healthcare.integration.fhir.FHIRConnectorBase;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.model.Resource;
import org.wso2.healthcare.integration.fhir.template.util.MsgCtxUtil;

import java.util.HashMap;

public class FHIRParsePayload extends FHIRConnectorBase {

    @Override
    protected void execute(MessageContext messageContext, FHIRConnectorContext fhirConnectorContext,
                           HashMap<String, String> configuredParams) throws FHIRConnectException {
        Resource resource = MsgCtxUtil.parseFHIRResourceFromMessageCtx(messageContext);
        fhirConnectorContext.createResource(resource);
    }

    @Override
    public String getOperationName() {
        return "parsePayload";
    }
}
