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

	public static UMLSQueryStringValue fromString(String str) {
		return new UMLSQueryStringValue(str);
	}
}
