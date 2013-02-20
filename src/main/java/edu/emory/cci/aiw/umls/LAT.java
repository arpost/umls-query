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
 * Represents a LAT in various UMLS queries. LAT values must be exactly 3
 * characters long.
 * 
 * @author Michel Mansour
 * 
 */
public final class LAT extends AbstractUMLSSearchUID {
    private char lat1;
    private char lat2;
    private char lat3;

    private LAT(char lat1, char lat2, char lat3) {
        super(new StringBuilder().append(lat1).append(lat2).append(lat3)
                .toString());
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.lat3 = lat3;
    }

    private LAT(String latStr) {
        this(latStr.charAt(0), latStr.charAt(1), latStr.charAt(2));
    }

    @Override
    public String getKeyName() {
        return "LAT";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LAT) {
            return this.getValue().equals(((LAT) o).getValue());
        }
        return false;
    }

    /**
     * Creates and returns a new <code>LATValue</code> with the given string as
     * the value. If the string argument is not exactly 3 characters, a
     * <code>MalformedUMLSUniqueIdentifierException</code> is thrown.
     * 
     * @param latStr
     *            the string representing the LAT to be created
     * @return a <code>LATValue</code> whose value is the given string
     * @throws MalformedUMLSUniqueIdentifierException
     *             if the string argument does not have length 3
     */
    public static LAT fromString(String latStr)
            throws MalformedUMLSUniqueIdentifierException {
        if (latStr.length() != 3) {
            throw new MalformedUMLSUniqueIdentifierException(
                    "LAT values must be exactly 3 characters");
        } else {
            return new LAT(latStr);
        }
    }

    @Override
    public String toString() {
        return new StringBuilder().append(lat1).append(lat2).append(lat3)
                .toString();
    }
}
