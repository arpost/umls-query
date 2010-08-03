package edu.emory.cci.aiw.umls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UMLSDatabaseConnection implements UMLSQueryExecutor {

	private Connection conn;
	
	private final String url;
	private final String user;
	private final String password;
	
	private static boolean isInit = false;
	private static UMLSDatabaseConnection persistentConn;
	
	@Override
	public void finish() throws Exception {
		conn.close();
		isInit = false;
	}

	@Override
	public void init() throws Exception {
		if (!isInit) {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(this.url, this.user, this.password);
			isInit = true;
		}
	}
	
	private UMLSDatabaseConnection(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public static UMLSDatabaseConnection getConnection(String url, String user, String password) {
		if (persistentConn == null) {
			persistentConn = newConnection(url, user, password);	
		} 
		return persistentConn;
	}
	
	private static UMLSDatabaseConnection newConnection(String url, String user, String password) {
		return new UMLSDatabaseConnection(url, user, password);
	}

	/* (non-Javadoc)
     * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUI(edu.emory.cci.aiw.umls.CUIQuerySearchUID, edu.emory.cci.aiw.umls.SABValue, boolean)
     */
    @Override
    public List<ConceptUID> getCUI(CUIQuerySearchUID uid, List<SABValue> sabs,
            boolean caseSensitive) {
    	StringBuilder sql = new StringBuilder("SELECT DISTINCT(CUI) FROM MRCONSO " +
    			"WHERE "); 
    	sql.append(uid.getKeyName());
    	sql.append(" = ");
    			
    	if (caseSensitive) {
    		sql.append("BINARY ");
    	}
    	sql.append("?");	
    	
    	if (sabs != null && !sabs.isEmpty()) {
    		sql.append(" AND ");
    		sql.append(singletonOrSetClause(sabs.get(0).getKeyName(), 
    										sabs.size()));
    	}
    	
    	System.out.println(sql);
    	
    	List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
    	params.add(uid);
    	if (sabs != null) {
    		params.addAll(sabs);
    	}
    	
    	try {
    		PreparedStatement query = substParams(sql.toString(), params);
    		UMLSResultSet.fromResultSet(query.executeQuery());
    	} catch (SQLException sqle) {
    		
    	}
    	
	    return null;
    }
    
    /* (non-Javadoc)
     * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMult(java.util.List, java.util.List, boolean)
     */
    @Override
    public Map<String, List<ConceptUID>> getCUIMult(
            List<CUIQuerySearchUID> uids, List<SABValue> sabs,
            boolean caseSensitive) {
    	StringBuilder sql = new StringBuilder("SELECT DISTINCT(CUI), ? " +
    											"FROM MRCONSO WHERE ");
    	sql.append(caseSensitive ? "BINARY " : " ");
    	sql.append(singletonOrSetClause(uids.get(0).getKeyName(), uids.size()));
    	
    	if (sabs != null && !sabs.isEmpty()) {
    		sql.append(" AND ");
    		sql.append(singletonOrSetClause(sabs.get(0).getKeyName(), 
    										sabs.size()));
    	}
    	
    	List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
    	params.addAll(uids);
    	if (sabs != null) {
    		params.addAll(sabs);
    	}
    	
    	try {
    		PreparedStatement query = substParams(sql.toString(), params);
    		UMLSResultSet.fromResultSet(query.executeQuery());
    	} catch (SQLException sqle) {
    		
    	}
    	
    	return null;
    }
    
    private PreparedStatement substParams(
    		String sql, List<UMLSQuerySearchUID> params) 
    	throws SQLException {
    	PreparedStatement query = conn.prepareStatement(sql);
    	for (int i = 0; i < params.size(); i++) {
    		query.setString(1 + i, params.get(i).getValue());
    	}
    	
    	return query;
    }
    
	private String singletonOrSetClause(String uidKeyName, int setSize) {
    	if (setSize > 1) {
    		StringBuilder clause = new StringBuilder(uidKeyName + " IN (");
    		for (int i = 0; i < setSize - 1; i++) {
    			clause.append("?, ");
    		}
    		clause.append("?)");
    		
    		return clause.toString();
    	} else {
    		return uidKeyName + " = ?";
    	}
    }

	public static void main(String[] args) throws Exception {
		UMLSDatabaseConnection conn = UMLSDatabaseConnection.
										getConnection("", "", "");
		List<SABValue> sabs = new ArrayList<SABValue>();
		sabs.add(SABValue.fromString("SNOMED"));
		sabs.add(SABValue.fromString("RAINMED"));
		conn.getCUI(ConceptUID.fromString("C1234567"), sabs, false);
	}
}

