/*
 * #%L
 * UMLSQuery
 * %%
 * Copyright (C) 2012 Emory University
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

/**
 * A class representing the semantic type of a term in UMLS.
 * 
 * @author Michel Mansour
 */
public final class SemanticType {
    private TermUID tui;
    private String semanticType;

    private SemanticType(TermUID tui, String type) {
        this.tui = tui;
        this.semanticType = type;
    }

    static SemanticType withTUIAndType(TermUID tui, String type) {
        return new SemanticType(tui, type);
    }

    public TermUID getTUI() {
        return this.tui;
    }

    public String getType() {
        return this.semanticType;
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + tui.hashCode();
        result = 31 * result + semanticType.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof SemanticType) {
            SemanticType other = (SemanticType) o;
            return this.tui.equals(other.tui)
                    && this.semanticType.equals(other.semanticType);
        } else {
            return false;
        }
    }
    
    public String toString() {
        return tui + ":" + semanticType;
    }
}
