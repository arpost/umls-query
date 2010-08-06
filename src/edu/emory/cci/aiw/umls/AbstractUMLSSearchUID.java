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
	
	public boolean equals(Object o) {
		if (o instanceof AbstractUMLSSearchUID) {
			return id.equals(((AbstractUMLSSearchUID) o).getValue());
		}
		return false;
	}
	
	public int hashCode() {
		int result = 17;
		result = 31 * result + getValue().hashCode();
		return result;
	}
}
