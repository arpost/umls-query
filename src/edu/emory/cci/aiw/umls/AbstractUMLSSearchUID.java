package edu.emory.cci.aiw.umls;

public abstract class AbstractUMLSSearchUID implements UMLSQuerySearchUID {
	private final String id;
	
	AbstractUMLSSearchUID(String id) {
		this.id = id;
	}

	public abstract String getKeyName();
	
	public String getValue() {
		return id;
	}
	
	public String toString() {
		return id;
	}
}
