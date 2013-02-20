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
 * Represents a terminology in a UMLS query, composed of a name and a
 * description.
 * 
 * @author Michel Mansour
 * 
 */
public final class SAB extends AbstractUMLSSearchUID {
    private String name;
    private String description;

    private SAB(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }

    @Override
    public String getKeyName() {
        return "SAB";
    }

    /**
     * Creates and returns a <code>SABValue</code> with the given string as the
     * name. The description is left blank.
     * 
     * @param name
     *            the name of the SAB dictionary
     * @return a <code>SABValue</code> with the given name
     */
    public static SAB withName(String name) {
        return new SAB(name, "");
    }

    /**
     * Creates and returns a <code>SABValue</code> with the given name and
     * description.
     * 
     * @param name
     *            the name of the SAB dictionary
     * @param description
     *            the description of the dictionary
     * @return a <code>SABValue</code> with the given name and description
     */
    public static SAB withNameAndDescription(String name, String description) {
        return new SAB(name, description);
    }

    /**
     * Gets the name of the dictionary
     * 
     * @return the name of the dictionary as a <code>String</code>
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the description of the dictionary
     * 
     * @return the description of the dictionary as a <code>String</code>
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SAB) {
            return this.getValue().equals(((SAB) o).getValue());
        }
        return false;
    }
}
