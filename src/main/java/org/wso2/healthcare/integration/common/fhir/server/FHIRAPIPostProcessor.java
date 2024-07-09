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

package org.wso2.healthcare.integration.common.fhir.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.fhir.FHIRUtils;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;

import java.util.Iterator;

/**
 * This class is responsible to
 * to be used by the API implementations
 */
public class FHIRAPIPostProcessor {

    private static final Log LOG = LogFactory.getLog(FHIRAPIPostProcessor.class);

    public void process(MessageContext messageContext) throws OpenHealthcareFHIRException {

        HealthcareIntegratorEnvironment integratorEnvironment =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        HealthcareIntegratorConfig config = integratorEnvironment.getHealthcareIntegratorConfig();

        // check whether preprocessor is enable or not
        // if preprocessor is disabled, we consider this whole feature is disabled
        if (!config.getFHIRServerConfig().getFhirPreprocessorConfig().isEnable()) return;

        FHIRRequestInfo requestInfo = FHIRUtils.getFHIRRequestInfo(messageContext);
        org.wso2.healthcare.integration.common.fhir.server.AbstractFHIRMessageContext fhirMsgCtx = FHIRUtils.getFHIRMessageContext(messageContext);

        if (requestInfo == null || fhirMsgCtx == null) {
            OpenHealthcareFHIRException exception = new OpenHealthcareFHIRException("Internal Server Error",
                    OpenHealthcareFHIRException.Severity.ERROR, OpenHealthcareFHIRException.IssueType.PROCESSING,
                    OpenHealthcareFHIRException.Details.INTERNAL_SERVER_ERROR, null);
            LOG.error(Constants.OH_PROP_REQUEST_INFO_OBJ + " property or " +
                    Constants.OH_INTERNAL_FHIR_MESSAGE_CONTEXT + " property is missing", exception);
            throw exception;
        }
        FHIRAPIStore fhirapiStore = integratorEnvironment.getFHIRAPIStore();

        // process search parameters
        processSearchParameters(messageContext, fhirapiStore, requestInfo, fhirMsgCtx);
    }


    private void processSearchParameters(MessageContext messageContext,
                                         FHIRAPIStore fhirapiStore,
                                         FHIRRequestInfo requestInfo, AbstractFHIRMessageContext fhirMsgCtx) throws OpenHealthcareFHIRException {

        Iterator<SearchParameterInfo> parameterInfoIterator = requestInfo.getFhirInfo().getAllSearchParameterInfo();
        while (parameterInfoIterator.hasNext()) {
            SearchParameterInfo parameterInfo = parameterInfoIterator.next();
            SearchParameter searchParameter =
                    fhirapiStore.findSearchParameter(parameterInfo.getName(),
                            requestInfo.getFhirInfo().getResource(), requestInfo.getFhirInfo().getProfile());
            if (searchParameter != null) {
                searchParameter.postProcess(parameterInfo, fhirMsgCtx, requestInfo, messageContext);
            }
        }
    }
}
