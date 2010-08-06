package edu.emory.cci.aiw.umls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.arp.javautil.arrays.Arrays;

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

	private ResultSet getCUIMult(List<? extends CUIQuerySearchUID> uids,
	        List<SABValue> sabs, boolean caseSensitive) {
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
			return substParams(sql.toString(), params).executeQuery();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
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
	public Map<ConceptUID, List<ConceptUID>> getCUIMultByCUI(
	        List<ConceptUID> cuis, List<SABValue> sabs, boolean caseSensitive) {
		Map<ConceptUID, List<ConceptUID>> result = new HashMap<ConceptUID, List<ConceptUID>>();

		ResultSet rs = getCUIMult(cuis, sabs, caseSensitive);
		try {
			while (rs.next()) {
				ConceptUID cui = ConceptUID.fromString(rs.getString(1));
				ConceptUID byCui = ConceptUID.fromString(rs.getString(2));

				if (!result.containsKey(byCui)) {
					result.put(byCui, new ArrayList<ConceptUID>());
				}
				result.get(byCui).add(cui);
			}
			return result;
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByAUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<AtomUID, List<ConceptUID>> getCUIMultByAUI(List<AtomUID> auis,
	        List<SABValue> sabs, boolean caseSensitive) {
		Map<AtomUID, List<ConceptUID>> result = new HashMap<AtomUID, List<ConceptUID>>();

		ResultSet rs = getCUIMult(auis, sabs, caseSensitive);
		try {
			while (rs.next()) {
				ConceptUID cui = ConceptUID.fromString(rs.getString(1));
				AtomUID byAui = AtomUID.fromString(rs.getString(2));

				if (!result.containsKey(byAui)) {
					result.put(byAui, new ArrayList<ConceptUID>());
				}
				result.get(byAui).add(cui);
			}
			return result;
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByLUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<LexicalUID, List<ConceptUID>> getCUIMultByLUI(
	        List<LexicalUID> luis, List<SABValue> sabs, boolean caseSensitive) {
		Map<LexicalUID, List<ConceptUID>> result = new HashMap<LexicalUID, List<ConceptUID>>();

		ResultSet rs = getCUIMult(luis, sabs, caseSensitive);
		try {
			while (rs.next()) {
				ConceptUID cui = ConceptUID.fromString(rs.getString(1));
				LexicalUID byLui = LexicalUID.fromString(rs.getString(2));

				if (!result.containsKey(byLui)) {
					result.put(byLui, new ArrayList<ConceptUID>());
				}
				result.get(byLui).add(cui);
			}
			return result;
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultByString(java.util
	 * .List, java.util.List, boolean)
	 */
	@Override
	public Map<UMLSQueryStringValue, List<ConceptUID>> getCUIMultByString(
	        List<UMLSQueryStringValue> strings, List<SABValue> sabs,
	        boolean caseSensitive) {
		Map<UMLSQueryStringValue, List<ConceptUID>> result = new HashMap<UMLSQueryStringValue, List<ConceptUID>>();

		ResultSet rs = getCUIMult(strings, sabs, caseSensitive);
		try {
			while (rs.next()) {
				ConceptUID cui = ConceptUID.fromString(rs.getString(1));
				UMLSQueryStringValue byString = UMLSQueryStringValue
				        .fromString(rs.getString(2));

				if (!result.containsKey(byString)) {
					result.put(byString, new ArrayList<ConceptUID>());
				}
				result.get(byString).add(cui);
			}
			return result;
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCUIMultBySUI(java.util.List,
	 * java.util.List, boolean)
	 */
	@Override
	public Map<StringUID, List<ConceptUID>> getCUIMultBySUI(
	        List<StringUID> suis, List<SABValue> sabs, boolean caseSensitive) {
		Map<StringUID, List<ConceptUID>> result = new HashMap<StringUID, List<ConceptUID>>();

		ResultSet rs = getCUIMult(suis, sabs, caseSensitive);
		try {
			while (rs.next()) {
				ConceptUID cui = ConceptUID.fromString(rs.getString(1));
				StringUID bySui = StringUID.fromString(rs.getString(2));

				if (!result.containsKey(bySui)) {
					result.put(bySui, new ArrayList<ConceptUID>());
				}
				result.get(bySui).add(cui);
			}
			return result;
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

	private ResultSet mapToId(String phrase, IdType idType, List<SABValue> sabs)
	        throws SQLException {

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

		return substParams(sql.toString(), params).executeQuery();
	}

	private Map<String, List<String>> matches(String phrase, ResultSet rs)
	        throws SQLException {

		Map<String, List<String>> matches = new HashMap<String, List<String>>();

		while (rs.next()) {
			String uid = rs.getString(1);
			String str = rs.getString(2);

			if (matches.containsKey(phrase)) {
				boolean seen = false;
				for (String s : matches.get(phrase)) {
					if (s.equals(str)) {
						seen = true;
					}
				}
				if (!seen) {
					matches.get(phrase).add(uid);
					matches.get(phrase).add(str);
				}
			} else {
				matches.put(phrase, new ArrayList<String>());
				matches.get(phrase).add(uid);
				matches.get(phrase).add(str);
			}
		}
		return matches;
	}

	private List<String[]> permutations(String[] strings) {
		List<String[]> result = new ArrayList<String[]>();
		PermutationGenerator pg = new PermutationGenerator(strings.length);
		while (pg.hasMore()) {
			String[] words = new String[strings.length];
			int[] indices = pg.getNext();
			for (int i = 0; i < indices.length; i++) {
				words[i] = strings[indices[i]];
			}
			result.add(words);
		}

		return result;
	}

	private List<String[]> allLengthPermutations(String[] strings) {
		List<String[]> result = new ArrayList<String[]>();

		for (String[] p : permutations(strings)) {
			String[] r = java.util.Arrays.copyOf(p, p.length);
			while (r.length > 0) {
				result.add(r);
				r = java.util.Arrays.copyOf(r, r.length - 1);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToAUI(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public Map<String, MapToIdResult<AtomUID>> mapToAUI(String phrase,
	        List<SABValue> sab) {
		Map<String, MapToIdResult<AtomUID>> result = new HashMap<String, MapToIdResult<AtomUID>>();
		try {
			Map<String, List<String>> matches = matches(phrase, mapToId(phrase,
			        IdType.AUI_IDTYPE, sab));
			if (matches.containsKey(phrase)) {
				for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
					result.put(entry.getKey(), MapToIdResult
					        .<AtomUID> fromUidAndStr(AtomUID.fromString(entry
					                .getValue().get(0)), UMLSQueryStringValue
					                .fromString(entry.getValue().get(1))));
				}
				return result;
			} else {
				String[] words = phrase.split("\\s");
				for (String[] p : allLengthPermutations(words)) {
					if (p.length == 1 && p[0].length() < 4) {
						continue;
					}
					String permutedString = Arrays.join(p, " ");
					matches = (matches(permutedString, mapToId(permutedString,
					        IdType.AUI_IDTYPE, sab)));
					for (Map.Entry<String, List<String>> entry : matches
					        .entrySet()) {
						result.put(entry.getKey(), MapToIdResult
						        .<AtomUID> fromUidAndStr(AtomUID
						                .fromString(entry.getValue().get(0)),
						                UMLSQueryStringValue.fromString(entry
						                        .getValue().get(1))));
					}
				}
				return result;
			}
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
	 * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToCUI(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public Map<String, MapToIdResult<ConceptUID>> mapToCUI(String phrase,
	        List<SABValue> sab) {
		Map<String, MapToIdResult<ConceptUID>> result = new HashMap<String, MapToIdResult<ConceptUID>>();
		try {
			Map<String, List<String>> matches = matches(phrase, mapToId(phrase,
			        IdType.CUI_IDTYPE, sab));
			if (matches.containsKey(phrase)) {
				for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
					result.put(entry.getKey(), MapToIdResult
					        .<ConceptUID> fromUidAndStr(ConceptUID
					                .fromString(entry.getValue().get(0)),
					                UMLSQueryStringValue.fromString(entry
					                        .getValue().get(1))));
				}
				return result;
			} else {
				String[] words = phrase.split("\\s");
				for (String[] p : allLengthPermutations(words)) {
					if (p.length == 1 && p[0].length() < 4) {
						continue;
					}
					String permutedString = Arrays.join(p, " ");
					matches = (matches(permutedString, mapToId(permutedString,
					        IdType.CUI_IDTYPE, sab)));
					for (Map.Entry<String, List<String>> entry : matches
					        .entrySet()) {
						result.put(entry.getKey(), MapToIdResult
						        .<ConceptUID> fromUidAndStr(ConceptUID
						                .fromString(entry.getValue().get(0)),
						                UMLSQueryStringValue.fromString(entry
						                        .getValue().get(1))));
					}
				}
				return result;
			}
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
	 * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToLUI(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public Map<String, MapToIdResult<LexicalUID>> mapToLUI(String phrase,
	        List<SABValue> sab) {
		Map<String, MapToIdResult<LexicalUID>> result = new HashMap<String, MapToIdResult<LexicalUID>>();
		try {
			Map<String, List<String>> matches = matches(phrase, mapToId(phrase,
			        IdType.LUI_IDTYPE, sab));
			if (matches.containsKey(phrase)) {
				for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
					result.put(entry.getKey(), MapToIdResult
					        .<LexicalUID> fromUidAndStr(LexicalUID
					                .fromString(entry.getValue().get(0)),
					                UMLSQueryStringValue.fromString(entry
					                        .getValue().get(1))));
				}
				return result;
			} else {
				String[] words = phrase.split("\\s");
				for (String[] p : allLengthPermutations(words)) {
					if (p.length == 1 && p[0].length() < 4) {
						continue;
					}
					String permutedString = Arrays.join(p, " ");
					matches = (matches(permutedString, mapToId(permutedString,
					        IdType.LUI_IDTYPE, sab)));
					for (Map.Entry<String, List<String>> entry : matches
					        .entrySet()) {
						result.put(entry.getKey(), MapToIdResult
						        .<LexicalUID> fromUidAndStr(LexicalUID
						                .fromString(entry.getValue().get(0)),
						                UMLSQueryStringValue.fromString(entry
						                        .getValue().get(1))));
					}
				}
				return result;
			}
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
	 * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToSUI(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public Map<String, MapToIdResult<StringUID>> mapToSUI(String phrase,
	        List<SABValue> sab) {
		Map<String, MapToIdResult<StringUID>> result = new HashMap<String, MapToIdResult<StringUID>>();
		try {
			Map<String, List<String>> matches = matches(phrase, mapToId(phrase,
			        IdType.CUI_IDTYPE, sab));
			if (matches.containsKey(phrase)) {
				for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
					result.put(entry.getKey(), MapToIdResult
					        .<StringUID> fromUidAndStr(StringUID
					                .fromString(entry.getValue().get(0)),
					                UMLSQueryStringValue.fromString(entry
					                        .getValue().get(1))));
				}
				return result;
			} else {
				String[] words = phrase.split("\\s");
				for (String[] p : allLengthPermutations(words)) {
					if (p.length == 1 && p[0].length() < 4) {
						continue;
					}
					String permutedString = Arrays.join(p, " ");
					matches = (matches(permutedString, mapToId(permutedString,
					        IdType.CUI_IDTYPE, sab)));
					for (Map.Entry<String, List<String>> entry : matches
					        .entrySet()) {
						result.put(entry.getKey(), MapToIdResult
						        .<StringUID> fromUidAndStr(StringUID
						                .fromString(entry.getValue().get(0)),
						                UMLSQueryStringValue.fromString(entry
						                        .getValue().get(1))));
					}
				}
				return result;
			}
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getParents(edu.emory.cci.aiw
	 * .umls.ParentsQuerySearchUID, java.lang.String,
	 * edu.emory.cci.aiw.umls.SABValue)
	 */
	@Override
	public Map<List<AtomUID>, AtomUID> getParents(ParentsQuerySearchUID uid,
	        String rela, SABValue sab) {
		Map<List<AtomUID>, AtomUID> result = new HashMap<List<AtomUID>, AtomUID>();
		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

		StringBuilder sql = new StringBuilder(
		        "select distinct(PTR), PAUI from MRHIER where ");
		sql.append(uid.getKeyName());
		sql.append(" = ?");
		params.add(uid);
		if (sab != null) {
			sql.append(" and SAB = ?");
			params.add(sab);
		}
		if (rela != null && !rela.equals("")) {
			sql.append(" and RELA = ?");
			params.add(UMLSQueryStringValue.fromString(rela));
		}

		try {
			ResultSet rs = substParams(sql.toString(), params).executeQuery();
			while (rs.next()) {
				String auiPath = rs.getString(1);
				AtomUID aui = AtomUID.fromString(rs.getString(2));
				result.put(auiPathAsList(auiPath), aui);
			}
			return result;
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getParentsMultByAUI(java.util
	 * .List, java.lang.String, edu.emory.cci.aiw.umls.SABValue)
	 */
	@Override
	public Map<AtomUID, Map<List<AtomUID>, AtomUID>> getParentsMultByAUI(
	        List<AtomUID> auis, String rela, SABValue sab) {

		Map<AtomUID, Map<List<AtomUID>, AtomUID>> result = new HashMap<AtomUID, Map<List<AtomUID>, AtomUID>>();
		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		StringBuilder sql = new StringBuilder("select distinct(PTR), PAUI, ");
		sql.append(auis.get(0).getKeyName());
		sql.append(" from MRHIER where");
		sql.append(singletonOrSetClause(auis.get(0).getKeyName(), auis.size()));
		params.addAll(auis);

		if (sab != null) {
			sql.append(" and SAB = ?");
			params.add(sab);
		}
		if (rela != null && !rela.equals("")) {
			sql.append(" and RELA = ?");
			params.add(UMLSQueryStringValue.fromString(rela));
		}

		try {
			ResultSet rs = substParams(sql.toString(), params).executeQuery();
			while (rs.next()) {
				String auiPath = rs.getString(1);
				AtomUID paui = AtomUID.fromString(rs.getString(2));
				AtomUID byAui = AtomUID.fromString(rs.getString(3));
				result.put(byAui, new HashMap<List<AtomUID>, AtomUID>());
				result.get(byAui).put(auiPathAsList(auiPath), paui);
			}
			return result;
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
	 * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getParentsMultByCUI(java.util
	 * .List, java.lang.String, edu.emory.cci.aiw.umls.SABValue)
	 */
	@Override
	public Map<ConceptUID, Map<List<AtomUID>, AtomUID>> getParentsMultByCUI(
	        List<ConceptUID> cuis, String rela, SABValue sab) {
		
		Map<ConceptUID, Map<List<AtomUID>, AtomUID>> result = new HashMap<ConceptUID, Map<List<AtomUID>, AtomUID>>();
		List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
		StringBuilder sql = new StringBuilder("select distinct(PTR), PAUI, ");
		sql.append(cuis.get(0).getKeyName());
		sql.append(" from MRHIER where");
		sql.append(singletonOrSetClause(cuis.get(0).getKeyName(), cuis.size()));
		params.addAll(cuis);

		if (sab != null) {
			sql.append(" and SAB = ?");
			params.add(sab);
		}
		if (rela != null && !rela.equals("")) {
			sql.append(" and RELA = ?");
			params.add(UMLSQueryStringValue.fromString(rela));
		}

		try {
			ResultSet rs = substParams(sql.toString(), params).executeQuery();
			while (rs.next()) {
				String auiPath = rs.getString(1);
				AtomUID paui = AtomUID.fromString(rs.getString(2));
				ConceptUID byCui = ConceptUID.fromString(rs.getString(3));
				result.put(byCui, new HashMap<List<AtomUID>, AtomUID>());
				result.get(byCui).put(auiPathAsList(auiPath), paui);
			}
			return result;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (MalformedUMLSUniqueIdentifierException muuie) {
			muuie.printStackTrace();
		}
		
		return null;
	}

	private List<AtomUID> auiPathAsList(String auiPath)
	        throws MalformedUMLSUniqueIdentifierException {
		List<AtomUID> auis = new ArrayList<AtomUID>();

		for (String s : auiPath.split("\\.")) {
			auis.add(AtomUID.fromString(s));
		}
		return auis;
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

		Map<String, MapToIdResult<ConceptUID>> mapResults = conn.mapToCUI(
		        "intraductal carcinoma of prostate", sabs);
		for (Map.Entry<String, MapToIdResult<ConceptUID>> entry : mapResults
		        .entrySet()) {
			System.out.println("\t" + entry.getKey() + "\t" + entry.getValue());
		}

		Map<List<AtomUID>, AtomUID> parents = conn.getParents(AtomUID
		        .fromString("A3004525"), "isa", null);
		for (Map.Entry<List<AtomUID>, AtomUID> entry : parents.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
