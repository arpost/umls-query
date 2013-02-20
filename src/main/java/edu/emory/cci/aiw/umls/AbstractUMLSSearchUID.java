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

/**
 * Serves as a base class for the various unique identifiers and other
 * string-valued elements that may be specified in a UMLS search query.
 * 
 * @author Michel Mansour
 * 
 */
public abstract class AbstractUMLSSearchUID implements UMLSQuerySearchUID {
    private final String id;

    AbstractUMLSSearchUID(String id) {
        this.id = id;
    }

    @Override
    public abstract String getKeyName();

    @Override
    public String getValue() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getValue().hashCode();
        return result;
    }

    @Override
    public abstract boolean equals(Object o);
}
