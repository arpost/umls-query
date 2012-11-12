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
 * An interface representing unique identifiers and other string-valued elements
 * that may be specified in a UMLS search query.
 * 
 * @author Michel Mansour
 * 
 */
public interface UMLSQuerySearchUID {

    /**
     * Gets the name of the unique identifier type as it would appear in a UMLS
     * query.
     * 
     * @return the type of the this unique identifier as a <code>String</code>
     */
    public String getKeyName();

    /**
     * Gets the value of the unique identifier
     * 
     * @return the value of this identifier as a <code>String</code>
     */
    public String getValue();
}
