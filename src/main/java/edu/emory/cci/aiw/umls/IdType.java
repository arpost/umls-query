package edu.emory.cci.aiw.umls;

final class IdType {

	private final String idType;

	public String getIdType() {
		return this.idType;
	}

	public static final IdType CUI_IDTYPE = new IdType(
	        ConceptUID.EMPTY_CUI.getKeyName());
	public static final IdType AUI_IDTYPE = new IdType(
	        AtomUID.EMPTY_AUI.getKeyName());
	public static final IdType LUI_IDTYPE = new IdType(
	        LexicalUID.EMPTY_LUI.getKeyName());
	public static final IdType SUI_IDTYPE = new IdType(
	        StringUID.EMPTY_SUI.getKeyName());

	private IdType(String idType) {
		this.idType = idType;
	}
	
	public static IdType fromString(String type) {
		if (type.equals("CUI")) {
			return CUI_IDTYPE;
		} else {
			return null;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof IdType) {
			return this.idType.equals(((IdType) o).getIdType());
		}
		return false;
	}
}
