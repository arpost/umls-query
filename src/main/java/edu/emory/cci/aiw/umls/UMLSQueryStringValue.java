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
 * Represents a string value for use in a UMLS query. This is a thin wrapper
 * around {@link java.lang.String} that implements the
 * {@link UMLSQuerySearchUID} interface so it may be used in UMLS queries. It
 * also implements several other interfaces for use in queries of those types.
 * 
 * @author Michel Mansour
 * 
 */
public class UMLSQueryStringValue extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID {

    private UMLSQueryStringValue(String str) {
        super(str);
    }

    @Override
    public String getKeyName() {
        return "STR";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UMLSQueryStringValue) {
            return this.getValue()
                    .equals(((UMLSQueryStringValue) o).getValue());
        }
        return false;
    }

    /**
     * Creates and returns a new <code>UMLSQueryStringValue</code> with the
     * given argument as the value.
     * 
     * @param str
     *            the string to use as the value
     * @return a <code>UMLSQueryStringValue</code> whose value is the given
     *         string.
     */
    public static UMLSQueryStringValue fromString(String str) {
        return new UMLSQueryStringValue(str);
    }
}
