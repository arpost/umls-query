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
