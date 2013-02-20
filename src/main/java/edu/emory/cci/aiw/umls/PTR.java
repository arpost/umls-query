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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the parental path from a specified UID to the root AUI. The path
 * can be processed as a dot-delimited string or as a list of
 * <code>AtomUID</code>s.
 * 
 * In both cases, the immediate parent of the UID under consideration is the
 * right-most element, and the root AUI is the left-most element. An example of
 * the string representation is:
 * <p>
 * <code>A3684559.A3886745.A3456474.A3456963.A3459284.A3292887.A16354045</code>
 * <p>
 * In this example, <code>A16354045</code> is the direct parent of the AUI under
 * consideration; its direct parent is <code>A3292887</code>; and so on; the
 * root is <code>A3684559</code>.
 * 
 * @author Michel Mansour
 * 
 */
public final class PTR implements Comparable<PTR> {
    private final List<AtomUID> ptr;
    private final String ptrStr;
    private final ParentsQuerySearchUID child;

    PTR(String ptr, ParentsQuerySearchUID child)
            throws MalformedUMLSUniqueIdentifierException {
        this.ptr = strToList(ptr);
        this.ptrStr = ptr;
        this.child = child;
    }

    private List<AtomUID> strToList(String ptr)
            throws MalformedUMLSUniqueIdentifierException {
        List<AtomUID> temp = new ArrayList<AtomUID>();
        String[] ps = ptr.split("\\.");
        for (int i = 0; i < ps.length; i++) {
            try {
                temp.add(AtomUID.fromString(ps[i]));
            } catch (MalformedUMLSUniqueIdentifierException muuie) {
                throw new MalformedUMLSUniqueIdentifierException(
                        "Malformed PTR string: " + ptr);
            }
        }
        return temp;
    }

    /**
     * Gets the child UID whose parents are represented in this instance
     * 
     * @return a <code>ParentsQuerySearchUID</code>
     */
    public ParentsQuerySearchUID getChild() {
        return child;
    }

    /**
     * Returns a string representation of the AUI path to the root. The string
     * consists of AUIs separated by '.'. The immediate parent of the AUI under
     * consideration is the right most AUI. The AUI to its left is its parent,
     * and so on.
     * 
     * Here is an example PTR string:
     * <code>A3684559.A3886745.A3456474.A3456963.A3459284.A3292887</code>
     * 
     * @return a <code>String</code> in the format specified above.
     */
    public String toString() {
        return ptrStr;
    }

    /**
     * Returns a list of representation of the AUI path from the UID under
     * consideration to the root. The last element in the list is the immediate
     * parents of the UID, and the first element is the root AUI.
     * 
     * @return a <code>List</code> of <code>AtomUID</code>.
     */
    public List<AtomUID> asList() {
        return ptr;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PTR) {
            return this.ptrStr.equals(((PTR) o).ptrStr);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.ptrStr.hashCode();
    }

    /**
     * Compares the <code>String</code> representations of the <code>PTR</code>s.
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(PTR ptr) {
        return this.ptrStr.compareTo(ptr.ptrStr);
    }
}
