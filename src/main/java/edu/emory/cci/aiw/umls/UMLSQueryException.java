package edu.emory.cci.aiw.umls;

/**
 * Base class for UMLS query API checked exceptions
 * 
 * @author Michel Mansour
 *
 */

public class UMLSQueryException extends Exception {

    public UMLSQueryException() {
	
    }

    public UMLSQueryException(String message) {
	super(message);
    }

    public UMLSQueryException(Throwable cause) {
	super(cause);
    }

    public UMLSQueryException(String message, Throwable cause) {
	super(message, cause);
    }
}
