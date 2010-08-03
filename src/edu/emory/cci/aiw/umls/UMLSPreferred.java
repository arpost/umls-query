package edu.emory.cci.aiw.umls;

public final class UMLSPreferred {
	
	private boolean enabled = false;
	private boolean preference = false;
	
	public static final UMLSPreferred NO_PREFERENCE = new UMLSPreferred(false, false);
	public static final UMLSPreferred NOT_PREFERRED = new UMLSPreferred(true, false);
	public static final UMLSPreferred PREFERRED     = new UMLSPreferred(true, true);
	
	private UMLSPreferred(boolean enabled, boolean preference) {
		this.enabled = enabled;
		this.preference = preference;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof UMLSPreferred) {
			return this.enabled == ((UMLSPreferred) obj).enabled && 
					this.preference == ((UMLSPreferred) obj).preference;
		}
		return false;
	}
}
