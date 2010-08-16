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
