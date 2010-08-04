package edu.emory.cci.aiw.umls;

public final class UMLSIdType {
	private final String idType;
	
	public static final UMLSIdType CUI_IDTYPE = new UMLSIdType("CUI");
	public static final UMLSIdType AUI_IDTYPE = new UMLSIdType("AUI");
	public static final UMLSIdType LUI_IDTYPE = new UMLSIdType("LUI");
	public static final UMLSIdType SUI_IDTYPE = new UMLSIdType("SUI");
	
	private UMLSIdType(String idType) {
		this.idType = idType;
	}
	
	public String getIdType() {
		return this.idType;
	}
}
