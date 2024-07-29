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

package org.wso2.healthcare.integration.common.fhir;

/**
 * Contains FHIR prefixes defined for parameter types of number, date, and quantity
 * http://hl7.org/fhir/r4/search.html#prefix
 */
public enum FHIRPrefix {

    EQ("eq", "=0", "equal"),
    NE("ne", "!=0", "not equal"),
    GT("gt", ">0", "greater than"),
    LT("lt", "<0", "less than"),
    GE("ge", ">=0", "greater or equal"),
    LE("le", "<=0", "less or equal"),
    SA("sa", "sa", "starts after"),
    EB("eb", "eb", "ends before"),
    AP("ap", "ap", "approximately");

    private final String prefix;
    private final String operation;
    private final String description;

    FHIRPrefix(String prefix, String operation, String description) {

        this.prefix = prefix;
        this.operation = operation;
        this.description = description;
    }

    public String getPrefix() {

        return prefix;
    }

    public String getOperation() {

        return operation;
    }

    public String getDescription() {

        return description;
    }

    @Override
    public String toString() {

        return prefix;
    }
}
