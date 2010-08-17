package edu.emory.cci.aiw.umls;

/**
 * An enum describing all of the options for the "preferred" parameter in a UMLS
 * getSTR query.
 * 
 * @author Michel Mansour
 * 
 */
public enum UMLSPreferred {
    /**
     * Signifies neither preferred nor not preferred. Equivalent to leaving the
     * preference parameter out of the query.
     */
    NO_PREFERENCE, 
    
    /**
     * Equivalent to setting the preference parameter to 'no'.
     */
    NOT_PREFERRED, 
    
    /**
     * Equivalent to setting the preference parameter to 'yes'
     */
    PREFERRED;
}
