package edu.emory.cci.aiw.umls;

public final class SABValue extends AbstractUMLSSearchUID {
	private String sab;
	
	private SABValue(String sab) {
		super(sab);
		this.sab = sab;
	}
	
	public String getKeyName() {
		return "SAB";
	}
	
	public static SABValue fromString(String sab) {
		return new SABValue(sab);
	}
	
	public String getSABValue() {
		return this.sab;
	}
	
	public String toString() {
		return getSABValue();
	}
	
	public boolean equals(Object o) {
		if (o instanceof SABValue) {
			return this.getValue().equals(((SABValue) o).getValue());
		}
		return false;
	}
}
