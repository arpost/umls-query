/*
 * #%L
 * UMLSQuery
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.emory.cci.aiw.umls;

final class IdType {

    private final String idType;

    public String getIdType() {
        return this.idType;
    }

    public static final IdType CUI_IDTYPE = new IdType(ConceptUID.EMPTY_CUI
            .getKeyName());
    public static final IdType AUI_IDTYPE = new IdType(AtomUID.EMPTY_AUI
            .getKeyName());
    public static final IdType LUI_IDTYPE = new IdType(LexicalUID.EMPTY_LUI
            .getKeyName());
    public static final IdType SUI_IDTYPE = new IdType(StringUID.EMPTY_SUI
            .getKeyName());

    private IdType(String idType) {
        this.idType = idType;
    }

    public static IdType fromString(String type) {
        if (type.equals("CUI")) {
            return CUI_IDTYPE;
        } else {
            return null;
        }
    }

    public boolean equals(Object o) {
        if (o instanceof IdType) {
            return this.idType.equals(((IdType) o).getIdType());
        }
        return false;
    }
}
