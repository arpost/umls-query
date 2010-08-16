package edu.emory.cci.aiw.umls;

import java.util.ArrayList;
import java.util.List;

public final class PTR implements Comparable<PTR> {
    private final List<AtomUID> ptr;
    private final String ptrStr;

    PTR(String ptr) throws MalformedUMLSUniqueIdentifierException {
        this.ptr = strToList(ptr);
        this.ptrStr = ptr;
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

    public String toString() {
        return ptrStr;
    }

    public List<AtomUID> asList() {
        return ptr;
    }

    public boolean equals(Object o) {
        if (o instanceof PTR) {
            return this.ptrStr.equals(((PTR) o).ptrStr);
        }
        return false;
    }

    public int hashCode() {
        return this.ptrStr.hashCode();
    }

    public int compareTo(PTR ptr) {
        return this.ptrStr.compareTo(ptr.ptrStr);
    }
}
