package edu.emory.cci.aiw.umls;

/**
 * Serves as a base class for the various unique identifiers and other
 * string-valued elements that may be specified in a UMLS search query.
 * 
 * @author Michel Mansour
 * 
 */
public abstract class AbstractUMLSSearchUID implements UMLSQuerySearchUID {
    private final String id;

    AbstractUMLSSearchUID(String id) {
        this.id = id;
    }

    @Override
    public abstract String getKeyName();

    @Override
    public String getValue() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getValue().hashCode();
        return result;
    }

    @Override
    public abstract boolean equals(Object o);
}
