package edu.emory.cci.aiw.umls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
			conn = DriverManager.getConnection(this.url, this.user,
			        this.password);
			isInit = true;
		}
	}

	private UMLSDatabaseConnection(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public static UMLSDatabaseConnection getConnection(String url, String user,
	        String password) {
		if (persistentConn == null) {
			persistentConn = newConnection(url, user, password);
		}
		return persistentConn;
	}

	private static UMLSDatabaseConnection newConnection(String url,
	        String user, String password) {
		return new UMLSDatabaseConnection(url, user, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUI(edu.emory.cci.aiw.umls
	 * .CUIQuerySearchUID, edu.emory.cci.aiw.umls.SABValue, boolean)
	 */
	@Override
	public List<ConceptUID> getCUI(CUIQuerySearchUID uid, List<SABValue> sabs,
	        boolean caseSensitive) {
		StringBuilder sql = new StringBuilder(
		        "select distinct(CUI) from MRCONSO where ");
		sql.append(uid.getKeyName());
		sql.append(" = ");

		if (caseSensitive) {
			sql.append("BINARY ");
		}
		sql.append("?");

		if (sabs != null && !sabs.isEmpty()) {
			sql.append(" and ");
			sql.append(singletonOrSetClause(sabs.get(0).getKeyName(), sabs
			        .size()));
		}

		System.out.println(sql);

		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		params.add(uid);
		if (sabs != null) {
			params.addAll(sabs);
		}

		try {
			PreparedStatement query = substParams(sql.toString(), params);
			System.out.println(query);
			ResultSet r = query.executeQuery();
			List<ConceptUID> cuis = new ArrayList<ConceptUID>();
			while (r.next()) {
				cuis.add(ConceptUID.fromString(r.getString(1)));
			}
			return cuis;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (MalformedUMLSUniqueIdentifierException muuie) {
			muuie.printStackTrace();
		}

		return null;
	}

	private Map<String, List<ConceptUID>> getCUIMult(
	        List<CUIQuerySearchUID> uids, List<SABValue> sabs,
	        boolean caseSensitive) {
		StringBuilder sql = new StringBuilder("select distinct(CUI), ? "
		        + "from MRCONSO where");
		sql.append(caseSensitive ? "BINARY " : " ");
		sql.append(singletonOrSetClause(uids.get(0).getKeyName(), uids.size()));

		if (sabs != null && !sabs.isEmpty()) {
			sql.append(" and ");
			sql.append(singletonOrSetClause(sabs.get(0).getKeyName(), sabs
			        .size()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByCUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<String, List<ConceptUID>> getCUIMultByCUI(List<ConceptUID> cuis,
	        List<SABValue> sabs, boolean caseSensitive) {
		List<CUIQuerySearchUID> uids = new ArrayList<CUIQuerySearchUID>();
		for (ConceptUID cui : cuis) {
			uids.add(cui);
		}
		return getCUIMult(uids, sabs, caseSensitive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByAUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<String, List<ConceptUID>> getCUIMultByAUI(List<AtomUID> auis,
	        List<SABValue> sabs, boolean caseSensitive) {
		List<CUIQuerySearchUID> uids = new ArrayList<CUIQuerySearchUID>();
		for (AtomUID aui : auis) {
			uids.add(aui);
		}
		return getCUIMult(uids, sabs, caseSensitive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByLUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<String, List<ConceptUID>> getCUIMultByLUI(List<LexicalUID> luis,
	        List<SABValue> sabs, boolean caseSensitive) {
		List<CUIQuerySearchUID> uids = new ArrayList<CUIQuerySearchUID>();
		for (LexicalUID lui : luis) {
			uids.add(lui);
		}
		return getCUIMult(uids, sabs, caseSensitive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByString(java.util
	 * .List, java.util.List, boolean)
	 */
	@Override
	public Map<String, List<ConceptUID>> getCUIMultByString(
	        List<UMLSQueryStringValue> strings, List<SABValue> sabs,
	        boolean caseSensitive) {
		List<CUIQuerySearchUID> uids = new ArrayList<CUIQuerySearchUID>();
		for (UMLSQueryStringValue string : strings) {
			uids.add(string);
		}
		return getCUIMult(uids, sabs, caseSensitive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultBySUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<String, List<ConceptUID>> getCUIMultBySUI(List<StringUID> suis,
	        List<SABValue> sabs, boolean caseSensitive) {
		List<CUIQuerySearchUID> uids = new ArrayList<CUIQuerySearchUID>();
		for (StringUID sui : suis) {
			uids.add(sui);
		}
		return getCUIMult(uids, sabs, caseSensitive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getAUI(edu.emory.cci.aiw.umls
	 * .AUIQuerySearchUID, edu.emory.cci.aiw.umls.SABValue)
	 */
	@Override
	public List<AtomUID> getAUI(AUIQuerySearchUID uid, SABValue sab) {
		StringBuilder sql = new StringBuilder(
		        "select distinct(AUI) from MRCONSO where ");
		sql.append(uid.getKeyName());
		sql.append(" = ?");

		if (sab != null) {
			sql.append(" and ");
			sql.append(sab.getKeyName());
			sql.append(" = ?");
		}

		System.out.println(sql);

		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		params.add(uid);
		if (sab != null) {
			params.add(sab);
		}

		try {
			PreparedStatement query = substParams(sql.toString(), params);
			System.out.println(query);
			ResultSet r = query.executeQuery();
			List<AtomUID> auis = new ArrayList<AtomUID>();
			while (r.next()) {
				auis.add(AtomUID.fromString(r.getString(1)));
			}
			return auis;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (MalformedUMLSUniqueIdentifierException muuie) {
			muuie.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getSTR(edu.emory.cci.aiw.umls
	 * .STRQuerySearchUID, edu.emory.cci.aiw.umls.SABValue,
	 * edu.emory.cci.aiw.umls.LATValue, edu.emory.cci.aiw.umls.UMLSPreferred)
	 */
	@Override
	public List<UMLSQueryStringValue> getSTR(STRQuerySearchUID uid,
	        SABValue sab, LATValue lat, UMLSPreferred preferred) {
		StringBuilder sql = new StringBuilder(
		        "select distinct(STR) from MRCONSO where ");
		sql.append(uid.getKeyName());
		sql.append(" = ?");

		if (preferred != null && preferred.equals(UMLSPreferred.PREFERRED)) {
			sql.append(" and TS = 'P' and STT = 'PF' and ISPREF= 'Y'");
		}

		if (sab != null) {
			sql.append(" and ");
			sql.append(sab.getKeyName());
			sql.append(" = ?");
		}

		if (lat != null) {
			sql.append(" and ");
			sql.append(lat.getKeyName());
			sql.append(" = ?");
		}

		System.out.println(sql);

		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		params.add(uid);
		if (sab != null) {
			params.add(sab);
		}
		if (lat != null) {
			params.add(lat);
		}

		try {
			PreparedStatement query = substParams(sql.toString(), params);
			System.out.println(query);
			ResultSet r = query.executeQuery();
			List<UMLSQueryStringValue> strings = new ArrayList<UMLSQueryStringValue>();
			while (r.next()) {
				strings.add(UMLSQueryStringValue.fromString(r.getString(1)));
			}
			return strings;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getTUI(edu.emory.cci.aiw.umls
	 * .TUIQuerySearchUID, edu.emory.cci.aiw.umls.SABValue)
	 */
	@Override
	public List<TermUID> getTUI(TUIQuerySearchUID uid, SABValue sab) {
		StringBuilder sql = new StringBuilder(
		        "select distinct(TUI) from MRCONSO a, MRSTY b "
		                + "where a.CUI = b.CUI and ");
		sql.append(uid.getKeyName());
		sql.append(" = ?");

		if (sab != null) {
			sql.append(" and ");
			sql.append(sab.getKeyName());
			sql.append(" = ?");
		}

		System.out.println(sql);

		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		params.add(uid);
		if (sab != null) {
			params.add(sab);
		}

		try {
			PreparedStatement query = substParams(sql.toString(), params);
			System.out.println(query);
			ResultSet r = query.executeQuery();
			List<TermUID> tuis = new ArrayList<TermUID>();
			while (r.next()) {
				tuis.add(TermUID.fromString(r.getString(1)));
			}
			return tuis;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (MalformedUMLSUniqueIdentifierException muuie) {
			muuie.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getSAB(edu.emory.cci.aiw.umls
	 * .SABQuerySearchUID)
	 */
	@Override
	public List<SABValue> getSAB(SABQuerySearchUID uid) {
		StringBuilder sql = new StringBuilder(
		        "select distinct(SAB) from MRCONSO where ");
		sql.append(uid.getKeyName());
		sql.append(" = ?");

		System.out.println(sql);

		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		params.add(uid);

		try {
			PreparedStatement query = substParams(sql.toString(), params);
			System.out.println(query);
			ResultSet r = query.executeQuery();
			List<SABValue> sabs = new ArrayList<SABValue>();
			while (r.next()) {
				sabs.add(SABValue.fromString(r.getString(1)));
			}
			return sabs;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToId(java.lang.String,
	 * edu.emory.cci.aiw.umls.UMLSIdType, edu.emory.cci.aiw.umls.SABValue)
	 */
	@Override
	public Map<String, List<UMLSQuerySearchUID>> mapToId(String phrase,
	        UMLSIdType idType, List<SABValue> sabs) {
		StringBuilder sql = new StringBuilder("select distinct(").append(
		        idType.getIdType()).append("), STR from MRCONSO where STR = ?");

		if (sabs != null && !sabs.isEmpty()) {
			sql.append(" and ");
			sql.append(singletonOrSetClause(sabs.get(0).getKeyName(), sabs
			        .size()));
		}

		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		// capitalize the first letter of the phrase
		params.add(UMLSQueryStringValue.fromString(new StringBuilder(phrase
		        .substring(0, 1).toUpperCase()).append(phrase.substring(1))
		        .toString()));
		if (sabs != null) {
			params.addAll(sabs);
		}

		try {
			ResultSet r = substParams(sql.toString(), params)
			        .executeQuery();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return null;
	}

	private String singletonOrSetClause(String uidKeyName, int setSize) {
		if (setSize > 1) {
			StringBuilder clause = new StringBuilder(uidKeyName + " in (");
			for (int i = 0; i < setSize - 1; i++) {
				clause.append("?, ");
			}
			clause.append("?)");

			return clause.toString();
		} else {
			return uidKeyName + " = ?";
		}
	}

	private PreparedStatement substParams(String sql,
            List<UMLSQuerySearchUID> params) throws SQLException {
    	PreparedStatement query = conn.prepareStatement(sql);
    	for (int i = 0; i < params.size(); i++) {
    		query.setString(1 + i, params.get(i).getValue());
    	}
    
    	return query;
    }

	public void testQuery() throws SQLException {
		PreparedStatement stmt = conn
		        .prepareStatement("SELECT * FROM MRCONSO LIMIT 5");
		ResultSet r = stmt.executeQuery();
		while (r.next()) {
			System.out.println(r.getString(1));
		}
	}

	public static void main(String[] args) throws Exception {
		UMLSDatabaseConnection conn = UMLSDatabaseConnection.getConnection(
		        "jdbc:mysql://aiwdev02.eushc.org:3307/umls_2010AA", "umlsuser",
		        "3SqQgPOh");
		conn.init();
		UMLSQueryStringValue searchStr = UMLSQueryStringValue
		        .fromString("Malignant tumour of prostate");
		List<SABValue> sabs = new ArrayList<SABValue>();
		sabs.add(SABValue.fromString("SNOMEDCT"));
		sabs.add(SABValue.fromString("RXNORM"));
		List<ConceptUID> cuis = conn.getCUI(searchStr, sabs, false);
		for (ConceptUID cui : cuis) {
			System.out.println(cui);
		}

		List<AtomUID> auis = conn.getAUI(searchStr, sabs.get(0));
		for (AtomUID aui : auis) {
			System.out.println(aui);
		}

		List<UMLSQueryStringValue> strings = conn.getSTR(AtomUID
		        .fromString("A3042752"), sabs.get(0), null,
		        UMLSPreferred.NO_PREFERENCE);
		for (UMLSQueryStringValue string : strings) {
			System.out.println(string);
		}

		List<TermUID> tuis = conn.getTUI(searchStr, sabs.get(0));
		for (TermUID tui : tuis) {
			System.out.println(tui);
		}

		List<SABValue> sabResults = conn.getSAB(UMLSQueryStringValue
		        .fromString("prostate"));
		for (SABValue sab : sabResults) {
			System.out.println(sab);
		}

		// sabs.add(SABValue.fromString("RAINMED"));
		// conn.getCUI(ConceptUID.fromString("C1234567"), sabs, false);
	}
}
