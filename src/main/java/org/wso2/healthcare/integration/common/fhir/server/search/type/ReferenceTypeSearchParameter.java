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

package org.wso2.healthcare.integration.common.fhir.server.search.type;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.OpenHealthcareException;
import org.wso2.healthcare.integration.common.OpenHealthcareFHIRException;
import org.wso2.healthcare.integration.common.fhir.server.AbstractSearchParameter;
import org.wso2.healthcare.integration.common.fhir.server.ResourceAPI;
import org.wso2.healthcare.integration.common.fhir.server.model.FHIRRequestInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.QueryParamInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.ReferenceTypeSearchParameterInfo;
import org.wso2.healthcare.integration.common.fhir.server.model.SearchParameterInfo;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Include parameter settings
 */
public class ReferenceTypeSearchParameter extends AbstractSearchParameter {

    private final ArrayList<String> targetResources = new ArrayList<>();

    public ReferenceTypeSearchParameter() {
        super(Constants.FHIR_DATATYPE_REFERENCE);
    }

    public void addTargetResource(String target) {

        this.targetResources.add(target);
    }

    public ArrayList<String> getTargetResources() {

        return targetResources;
    }

    @Override
    public ArrayList<SearchParameterInfo> preProcess(ResourceAPI resourceAPI, FHIRRequestInfo requestInfo,
                                                     MessageContext messageContext) throws OpenHealthcareFHIRException {
        ArrayList<SearchParameterInfo> searchParameterInfoList = new ArrayList<>();
        QueryParamInfo refQueryParam = requestInfo.getHttpInfo().findQueryParam(getName());
        String refQueryParamValue = refQueryParam.getValue();

        String targetResource;
        String id;
        String[] refValueParts = refQueryParamValue.split("/");
        if (targetResources.size() == 1) {
            // handle single target type
            targetResource = targetResources.get(0);

            if (refValueParts.length == 1) {
                // only id
                id = refValueParts[0];
            } else if (refValueParts.length == 2) {
                // relative reference
                id = refValueParts[1];
            } else {
                // absolute URL
                id = refValueParts[refValueParts.length - 1];
            }
        } else {
            // handle multiple target type
            if (refValueParts.length == 1) {
                // only id hence reject
                String msg = "Search parameter \"" + getName() + "\" of type reference, must specify type of the " +
                        "resource, since it accepts resource types : " + targetResources + ". hence format should be " +
                        "[parameter]=[type]/[id] or [parameter]=[url]";
                throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                        OpenHealthcareFHIRException.IssueType.INVALID,
                        OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER_VALUE, null);
            } else if (refValueParts.length == 2) {
                // relative reference
                targetResource = refValueParts[0];
                id = refValueParts[1];
            } else {
                // absolute URL
                targetResource = refValueParts[refValueParts.length - 2];
                id = refValueParts[refValueParts.length - 1];
            }

            // Verify extracted target resource
            if (!targetResources.contains(targetResource)) {
                String msg = "Search parameter \"" + getName() +
                        "\" of type reference, only accepts target resource types : " + targetResources;
                throw new OpenHealthcareFHIRException(msg, OpenHealthcareFHIRException.Severity.ERROR,
                        OpenHealthcareFHIRException.IssueType.INVALID,
                        OpenHealthcareFHIRException.Details.INVALID_SEARCH_PARAMETER_VALUE, null);
            }
        }

        ReferenceTypeSearchParameterInfo info = new ReferenceTypeSearchParameterInfo(getName());
        info.setTargetResource(targetResource);
        info.setResourceId(id);
        info.setExpression(getExpression());
        searchParameterInfoList.add(info);

        return searchParameterInfoList;
    }

    @Override
    public void populateSettings(OMElement settings) throws OpenHealthcareException {
        super.populateSettings(settings);

        // process target resources
        OMElement targetsElement = settings.getFirstChildWithName(new QName(Constants.SYNAPSE_NS, "targets"));
        if (targetsElement != null) {
            Iterator targetIterator = targetsElement.getChildrenWithName(new QName(Constants.SYNAPSE_NS, "resource"));
            while (targetIterator.hasNext()) {
                OMElement target = (OMElement) targetIterator.next();
                this.addTargetResource(target.getText());
            }
            if (this.targetResources.size() == 0) {
                throw new OpenHealthcareException(
                        "Target \"resource\" (cardinality = 1..*) of the search parameter (type : reference): " +
                                getName() +" is missing");
            }
        } else {
            throw new OpenHealthcareException("\"targets\" (mandatory) of the search parameter is missing");
        }
    }


}
