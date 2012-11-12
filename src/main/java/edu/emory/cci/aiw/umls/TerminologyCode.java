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
 * The code for a concept in a particular terminology
 * 
 * @author Michel Mansour
 * 
 */
public final class TerminologyCode {
    private final String code;
    private final SAB sab;

    private TerminologyCode(String code, SAB sab) {
        this.code = code;
        this.sab = sab;
    }

    /**
     * Creates and returns a <code>TerminologyCode</code> with the given code
     * and terminology (SAB).
     * 
     * @param code the code
     * @param sab the sab the code comes from
     * @return a <code>TerminologyCode</code> with the given code and SAB
     */
    public static TerminologyCode fromStringAndSAB(String code, SAB sab) {
        return new TerminologyCode(code, sab);
    }
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the sab
     */
    public SAB getSab() {
        return sab;
    }
    
    public String toString() {
        return code + " in " + sab.getName();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof TerminologyCode) {
            TerminologyCode other = (TerminologyCode) o;
            return this.code.equals(other.code) && this.sab.equals(other.sab);
        }
        return false;
    }
}
