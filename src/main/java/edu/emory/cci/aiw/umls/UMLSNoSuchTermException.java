package edu.emory.cci.aiw.umls;

public final class UMLSNoSuchTermException extends UMLSQueryException {
    
    public UMLSNoSuchTermException() {

    }

    public UMLSNoSuchTermException(String message) {
        super(message);
    }

    public UMLSNoSuchTermException(Throwable cause) {
        super(cause);
    }

    public UMLSNoSuchTermException(String message, Throwable cause) {
        super(message, cause);
    }
}
