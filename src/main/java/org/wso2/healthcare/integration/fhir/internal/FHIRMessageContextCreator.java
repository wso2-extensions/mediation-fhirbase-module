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

package org.wso2.healthcare.integration.fhir.internal;

import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.fhir.FHIRHealthcareMessageContext;
import org.wso2.healthcare.integration.fhir.config.FHIRConnectorContext;
import org.wso2.healthcare.integration.fhir.utils.FHIRConnectorUtils;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.context.AbstractMessageContextCreator;
import org.wso2.healthcare.integration.common.context.HealthcareMessageContext;
import org.wso2.healthcare.integration.common.context.MessageContextType;

/**
 * Message context creator implementation which create FHIR message context
 */
public class FHIRMessageContextCreator extends AbstractMessageContextCreator {

    public FHIRMessageContextCreator() {

        super(MessageContextType.FHIR);
    }

    @Override
    public HealthcareMessageContext create(MessageContext messageContext) throws OpenHealthcareException {
        // getting fhir configuration
        FHIRHealthcareMessageContext msgCtx = new FHIRHealthcareMessageContext();
        FHIRConnectorContext connectorCtx = FHIRConnectorUtils.getFHIRConnectorContext(messageContext);
        msgCtx.setFhirConnectorContext(connectorCtx);
        connectorCtx.setFHIRHealthcareMessageContext(msgCtx);

        return msgCtx;
    }
}
