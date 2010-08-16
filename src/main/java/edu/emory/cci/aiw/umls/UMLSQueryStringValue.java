package edu.emory.cci.aiw.umls;

/**
 * Represents a string value for use in a UMLS query. This is a thin wrapper
 * around {@link java.lang.String} that implements the
 * {@link UMLSQuerySearchUID} interface so it may be used in UMLS queries. It
 * also implements several other interfaces for use in queries of those types.
 * 
 * @author Michel Mansour
 * 
 */
public class UMLSQueryStringValue extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID {

    private UMLSQueryStringValue(String str) {
        super(str);
    }

    @Override
    public String getKeyName() {
        return "STR";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UMLSQueryStringValue) {
            return this.getValue()
                    .equals(((UMLSQueryStringValue) o).getValue());
        }
        return false;
    }

    /**
     * Creates and returns a new <code>UMLSQueryStringValue</code> with the
     * given argument as the value.
     * 
     * @param str
     *            the string to use as the value
     * @return a <code>UMLSQueryStringValue</code> whose value is the given
     *         string.
     */
    public static UMLSQueryStringValue fromString(String str) {
        return new UMLSQueryStringValue(str);
    }
}
