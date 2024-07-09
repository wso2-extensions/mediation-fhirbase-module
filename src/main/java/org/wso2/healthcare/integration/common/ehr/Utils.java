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
package org.wso2.healthcare.integration.common.ehr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;

public class Utils {

    private static final Log log = LogFactory.getLog(Utils.class);

    /**
     * @param messageContext Message context to set the information to.
     * @param e The exception thrown.
     * @param errorCode The error code.
     * @param message The error message.
     * @throws org.wso2.healthcare.integration.common.ehr.EHRConnectException In case of an error occurs while saving the error details to the MessageContext.
     */
    public static void handleError(MessageContext messageContext, Throwable e, int errorCode, String message)
            throws org.wso2.healthcare.integration.common.ehr.EHRConnectException {

        setErrorResponse(messageContext, e, errorCode, message);
        handleException(message, e);
    }

    public static void setErrorResponse(MessageContext messageContext, Throwable e, int errorCode, String msg) {

        messageContext.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, msg);
        messageContext.setProperty(SynapseConstants.ERROR_DETAIL, e.getMessage());
        messageContext.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
        ((Axis2MessageContext) messageContext).getAxis2MessageContext().setProperty(Constants.HTTP_SC, errorCode);
    }

    public static void handleException(String message, Throwable throwable) throws org.wso2.healthcare.integration.common.ehr.EHRConnectException {

        log.error(message, throwable);
        throw new EHRConnectException(throwable, message);
    }
}
