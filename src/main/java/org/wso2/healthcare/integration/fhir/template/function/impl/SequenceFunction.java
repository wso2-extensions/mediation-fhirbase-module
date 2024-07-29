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

package org.wso2.healthcare.integration.fhir.template.function.impl;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.util.MessageHelper;
import org.wso2.healthcare.integration.fhir.template.exception.TemplateFunctionException;
import org.wso2.healthcare.integration.fhir.template.function.AbstractTemplateFunction;

import java.util.ArrayList;
import java.util.List;


/**
 * This function is used to execute a custom sequence. syntax: sequence(<sequence_name>,<property_name>)
 */
public class SequenceFunction extends AbstractTemplateFunction {

    private String sequenceName;
    private String propertyName;
    private boolean isClonedMessageContext;

    public SequenceFunction() {
        this.setFuncName("sequence");
        this.setFunctionType(Type.POSTPROCESS);
    }

    @Override
    public List evaluate(MessageContext messageContext, List arguments) throws TemplateFunctionException {
        List<Object> evaluatedResults = new ArrayList<>();
        if (StringUtils.isNotBlank(sequenceName) && StringUtils.isNotBlank(propertyName)) {
            SequenceMediator customMediationSeq = (SequenceMediator) messageContext.getSequence(sequenceName);
            MessageContext sequenceMsgCtx = messageContext;
            if (customMediationSeq != null) {
                if (isClonedMessageContext) {
                    try {
                        sequenceMsgCtx = MessageHelper.cloneMessageContext(messageContext);
                    } catch (AxisFault axisFault) {
                        throw new TemplateFunctionException("Error occurred while cloning message context for mediation " +
                                "sequence: " + sequenceName, axisFault);
                    }
                }
                customMediationSeq.mediate(sequenceMsgCtx);
                evaluatedResults.add(sequenceMsgCtx.getProperty(propertyName));
            } else {
                throw new TemplateFunctionException("Mediation sequence: " + sequenceName + " is non-existent");
            }
        }
        return evaluatedResults;
    }

    @Override
    public void setInput(String input) {
        this.setFunctionInput(input);
        if (input.contains(",")) {
            String[] arguments = input.split(",");
            if (arguments.length == 1) {
                setSequenceName(arguments[0].trim());
            } else if (arguments.length == 2) {
                setSequenceName(arguments[0].trim());
                setPropertyName(arguments[1].trim());
            } else if (arguments.length == 3) {
                setSequenceName(arguments[0].trim());
                setPropertyName(arguments[1].trim());
                setClonedMessageContext(Boolean.getBoolean(arguments[2].trim()));
            }
        }
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isClonedMessageContext() {
        return isClonedMessageContext;
    }

    public void setClonedMessageContext(boolean clonedMessageContext) {
        isClonedMessageContext = clonedMessageContext;
    }
}
