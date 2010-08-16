package edu.emory.cci.aiw.umls;

/**
 * Checked exception for malformed UMLS unique identifiers
 * 
 * @author Michel Mansour
 * 
 */

public final class MalformedUMLSUniqueIdentifierException extends
        UMLSQueryException {

    public MalformedUMLSUniqueIdentifierException() {

    }

    public MalformedUMLSUniqueIdentifierException(String message) {
        super(message);
    }

    public MalformedUMLSUniqueIdentifierException(Throwable cause) {
        super(cause);
    }

    public MalformedUMLSUniqueIdentifierException(String message,
            Throwable cause) {
        super(message, cause);
    }

}
