package edu.emory.cci.aiw.umls;

public class UMLSQueryStringValue extends AbstractUMLSSearchUID implements
        CUIQuerySearchUID, AUIQuerySearchUID, STRQuerySearchUID,
        TUIQuerySearchUID, SABQuerySearchUID {

    private UMLSQueryStringValue(String str) {
        super(str);
    }

    public String getKeyName() {
        return "STR";
    }

    public boolean equals(Object o) {
        if (o instanceof UMLSQueryStringValue) {
            return this.getValue()
                    .equals(((UMLSQueryStringValue) o).getValue());
        }
        return false;
    }

    public static UMLSQueryStringValue fromString(String str) {
        return new UMLSQueryStringValue(str);
    }
}
