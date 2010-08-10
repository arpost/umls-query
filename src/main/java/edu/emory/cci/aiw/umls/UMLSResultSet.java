package edu.emory.cci.aiw.umls;

import java.sql.ResultSet;

public final class UMLSResultSet {
	private ResultSet results;
	
	private UMLSResultSet(ResultSet results) {
		this.results = results;
	}
	
	static UMLSResultSet fromResultSet(ResultSet results) {
		return new UMLSResultSet(results);
	}
}
