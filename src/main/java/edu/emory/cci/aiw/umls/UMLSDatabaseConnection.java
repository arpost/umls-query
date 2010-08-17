package edu.emory.cci.aiw.umls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;

/**
 * A database-baked implementation of the {@link UMLSQueryExecutor} interface.
 * An instance is obtained by calling the static method {@link #getConnection} with the
 * parameters for accessing the database. Additionally, the caller must pass in
 * the database API type. Once an instance has been obtained, any of the queries
 * defined in the {@link UMLSQueryExecutor} interface may be executed.
 * 
 * @author Michel Mansour
 * 
 */
public class UMLSDatabaseConnection implements UMLSQueryExecutor {

    private Connection conn;

    private final DatabaseAPI api;
    private final String url;
    private final String user;
    private final String password;

    private static void log(Level level, String msg) {
        UMLSUtil.logger().log(level, msg);
    }

    private UMLSDatabaseConnection(DatabaseAPI api, String url, String user,
            String password) {
        this.api = api;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Returns a <code>UMLSDatabaseConnection</code> for querying a UMLS
     * database. Callers must specify the location and access information for
     * the database, as well as the database API type.
     * 
     * @param api
     *            the Java database API to use. An instance of the
     *            {@link DatabaseAPI} enum, which provides the
     *            {@link java.sql.DriverMananger} and
     *            {@link javax.sql.DataSource} methods.
     * @param url
     *            the location of the database
     * @param user
     *            the username to access the database
     * @param password
     *            the password that goes with the username to access the
     *            database
     * @return a <code>UMLSDatabaseConnection</code> accessed by the specified
     *         parameters
     */
    public static UMLSDatabaseConnection getConnection(DatabaseAPI api,
            String url, String user, String password) {
        return new UMLSDatabaseConnection(api, url, user, password);
    }

    private void setupConn() throws UMLSQueryException {
        log(Level.INFO, "Attempting to establish database connection...");
        try {
            conn = api.newConnectionSpecInstance(url, user, password)
                    .getOrCreate();
            log(Level.INFO, "Connection established with " + url);
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (InvalidConnectionSpecArguments icsa) {
            throw new UMLSQueryException(icsa);
        }
    }

    private void tearDownConn() throws UMLSQueryException {
        log(Level.INFO, "Attempting to disconnect from the database...");
        try {
            conn.close();
            log(Level.INFO, "Disconnected from database " + url);
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        }
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
            boolean caseSensitive) throws UMLSQueryException {
        try {
            setupConn();
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

            log(Level.FINE, sql.toString());

            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(uid);
            if (sabs != null) {
                params.addAll(sabs);
            }

            ResultSet r = executeAndLogQuery(substParams(sql.toString(), params));
            List<ConceptUID> cuis = new ArrayList<ConceptUID>();
            while (r.next()) {
                cuis.add(ConceptUID.fromString(r.getString(1)));
            }
            return cuis;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    private ResultSet getCUIMult(List<? extends CUIQuerySearchUID> uids,
            List<SABValue> sabs, boolean caseSensitive) throws SQLException {
        StringBuilder sql = new StringBuilder("select distinct(CUI), ");
        sql.append(uids.get(0).getKeyName());
        sql.append(" from MRCONSO where");
        sql.append(caseSensitive ? "BINARY " : " ");
        sql.append(singletonOrSetClause(uids.get(0).getKeyName(), uids.size()));

        if (sabs != null && !sabs.isEmpty()) {
            sql.append(" and ");
            sql.append(singletonOrSetClause(sabs.get(0).getKeyName(), sabs
                    .size()));
        }

        log(Level.FINE, sql.toString());

        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
        params.addAll(uids);
        if (sabs != null) {
            params.addAll(sabs);
        }
        return executeAndLogQuery(substParams(sql.toString(), params));
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
            List<ConceptUID> cuis, List<SABValue> sabs, boolean caseSensitive)
            throws UMLSQueryException {
        Map<ConceptUID, List<ConceptUID>> result = new HashMap<ConceptUID, List<ConceptUID>>();

        try {
            setupConn();
            ResultSet rs = getCUIMult(cuis, sabs, caseSensitive);
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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
            List<SABValue> sabs, boolean caseSensitive)
            throws UMLSQueryException {
        Map<AtomUID, List<ConceptUID>> result = new HashMap<AtomUID, List<ConceptUID>>();

        try {
            setupConn();
            ResultSet rs = getCUIMult(auis, sabs, caseSensitive);
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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
            List<LexicalUID> luis, List<SABValue> sabs, boolean caseSensitive)
            throws UMLSQueryException {
        Map<LexicalUID, List<ConceptUID>> result = new HashMap<LexicalUID, List<ConceptUID>>();

        try {
            setupConn();
            ResultSet rs = getCUIMult(luis, sabs, caseSensitive);
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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
            boolean caseSensitive) throws UMLSQueryException {
        Map<UMLSQueryStringValue, List<ConceptUID>> result = new HashMap<UMLSQueryStringValue, List<ConceptUID>>();

        try {
            setupConn();
            ResultSet rs = getCUIMult(strings, sabs, caseSensitive);
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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
            List<StringUID> suis, List<SABValue> sabs, boolean caseSensitive)
            throws UMLSQueryException {
        Map<StringUID, List<ConceptUID>> result = new HashMap<StringUID, List<ConceptUID>>();

        try {
            setupConn();
            ResultSet rs = getCUIMult(suis, sabs, caseSensitive);
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getAUI(edu.emory.cci.aiw.umls
     * .AUIQuerySearchUID, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public List<AtomUID> getAUI(AUIQuerySearchUID uid, SABValue sab)
            throws UMLSQueryException {
        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(AUI) from MRCONSO where ");
            sql.append(uid.getKeyName());
            sql.append(" = ?");

            if (sab != null) {
                sql.append(" and ");
                sql.append(sab.getKeyName());
                sql.append(" = ?");
            }

            log(Level.FINE, sql.toString());

            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(uid);
            if (sab != null) {
                params.add(sab);
            }

            ResultSet r = executeAndLogQuery(substParams(sql.toString(), params));
            List<AtomUID> auis = new ArrayList<AtomUID>();
            while (r.next()) {
                auis.add(AtomUID.fromString(r.getString(1)));
            }
            return auis;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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
            SABValue sab, LATValue lat, UMLSPreferred preferred)
            throws UMLSQueryException {
        try {
            setupConn();
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

            log(Level.FINE, sql.toString());

            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(uid);
            if (sab != null) {
                params.add(sab);
            }
            if (lat != null) {
                params.add(lat);
            }

            ResultSet r = executeAndLogQuery(substParams(sql.toString(), params));
            List<UMLSQueryStringValue> strings = new ArrayList<UMLSQueryStringValue>();
            while (r.next()) {
                strings.add(UMLSQueryStringValue.fromString(r.getString(1)));
            }
            return strings;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getTUI(edu.emory.cci.aiw.umls
     * .TUIQuerySearchUID, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public List<TermUID> getTUI(TUIQuerySearchUID uid, SABValue sab)
            throws UMLSQueryException {
        try {
            setupConn();

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

            log(Level.FINE, sql.toString());

            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(uid);
            if (sab != null) {
                params.add(sab);
            }

            ResultSet r = executeAndLogQuery(substParams(sql.toString(), params));
            List<TermUID> tuis = new ArrayList<TermUID>();
            while (r.next()) {
                tuis.add(TermUID.fromString(r.getString(1)));
            }
            return tuis;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getSAB(edu.emory.cci.aiw.umls
     * .SABQuerySearchUID)
     */
    @Override
    public List<SABValue> getSAB(SABQuerySearchUID uid)
            throws UMLSQueryException {
        try {
            setupConn();

            StringBuilder sql = new StringBuilder(
                    "select distinct(SAB) from MRCONSO where ");
            sql.append(uid.getKeyName());
            sql.append(" = ?");

            log(Level.FINE, sql.toString());

            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(uid);

            ResultSet r = executeAndLogQuery(substParams(sql.toString(), params));
            List<SABValue> sabs = new ArrayList<SABValue>();
            while (r.next()) {
                sabs.add(SABValue.withName(r.getString(1)));
            }
            return sabs;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } finally {
            tearDownConn();
        }
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

        log(Level.FINE, sql.toString());

        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
        // capitalize the first letter of the phrase
        params.add(UMLSQueryStringValue.fromString(new StringBuilder(phrase
                .substring(0, 1).toUpperCase()).append(phrase.substring(1))
                .toString()));
        if (sabs != null) {
            params.addAll(sabs);
        }

        return executeAndLogQuery(substParams(sql.toString(), params));
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
            List<SABValue> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<AtomUID>> result = new HashMap<String, MapToIdResult<AtomUID>>();
        try {
            setupConn();
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToCUI(java.lang.String,
     * java.util.List)
     */
    @Override
    public Map<String, MapToIdResult<ConceptUID>> mapToCUI(String phrase,
            List<SABValue> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<ConceptUID>> result = new HashMap<String, MapToIdResult<ConceptUID>>();
        try {
            setupConn();
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToLUI(java.lang.String,
     * java.util.List)
     */
    @Override
    public Map<String, MapToIdResult<LexicalUID>> mapToLUI(String phrase,
            List<SABValue> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<LexicalUID>> result = new HashMap<String, MapToIdResult<LexicalUID>>();
        try {
            setupConn();
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.emory.cci.aiw.umls.UMLSQueryExecutor#mapToSUI(java.lang.String,
     * java.util.List)
     */
    @Override
    public Map<String, MapToIdResult<StringUID>> mapToSUI(String phrase,
            List<SABValue> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<StringUID>> result = new HashMap<String, MapToIdResult<StringUID>>();
        try {
            setupConn();
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
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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
    public Map<PTR, AtomUID> getParents(ParentsQuerySearchUID uid, String rela,
            SABValue sab) throws UMLSQueryException {
        Map<PTR, AtomUID> result = new HashMap<PTR, AtomUID>();
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();
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

            log(Level.FINE, sql.toString());

            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            while (rs.next()) {
                PTR ptr = new PTR(rs.getString(1), uid);
                AtomUID aui = AtomUID.fromString(rs.getString(2));
                result.put(ptr, aui);
            }
            return result;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getParentsMultByAUI(java.util
     * .List, java.lang.String, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public Map<AtomUID, Map<PTR, AtomUID>> getParentsMultByAUI(
            List<AtomUID> auis, String rela, SABValue sab)
            throws UMLSQueryException {

        Map<AtomUID, Map<PTR, AtomUID>> result = new HashMap<AtomUID, Map<PTR, AtomUID>>();
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(PTR), PAUI, ");
            sql.append(auis.get(0).getKeyName());
            sql.append(" from MRHIER where");
            sql.append(singletonOrSetClause(auis.get(0).getKeyName(), auis
                    .size()));
            params.addAll(auis);

            if (sab != null) {
                sql.append(" and SAB = ?");
                params.add(sab);
            }
            if (rela != null && !rela.equals("")) {
                sql.append(" and RELA = ?");
                params.add(UMLSQueryStringValue.fromString(rela));
            }

            log(Level.FINE, sql.toString());

            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            while (rs.next()) {
                AtomUID paui = AtomUID.fromString(rs.getString(2));
                AtomUID byAui = AtomUID.fromString(rs.getString(3));
                PTR ptr = new PTR(rs.getString(1), byAui);
                result.put(byAui, new HashMap<PTR, AtomUID>());
                result.get(byAui).put(ptr, paui);
            }
            return result;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getParentsMultByCUI(java.util
     * .List, java.lang.String, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public Map<ConceptUID, Map<PTR, AtomUID>> getParentsMultByCUI(
            List<ConceptUID> cuis, String rela, SABValue sab)
            throws UMLSQueryException {

        Map<ConceptUID, Map<PTR, AtomUID>> result = new HashMap<ConceptUID, Map<PTR, AtomUID>>();
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();

            StringBuilder sql = new StringBuilder(
                    "select distinct(PTR), PAUI, ");
            sql.append(cuis.get(0).getKeyName());
            sql.append(" from MRHIER where");
            sql.append(singletonOrSetClause(cuis.get(0).getKeyName(), cuis
                    .size()));
            params.addAll(cuis);

            if (sab != null) {
                sql.append(" and SAB = ?");
                params.add(sab);
            }
            if (rela != null && !rela.equals("")) {
                sql.append(" and RELA = ?");
                params.add(UMLSQueryStringValue.fromString(rela));
            }

            log(Level.FINE, sql.toString());

            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            while (rs.next()) {
                AtomUID paui = AtomUID.fromString(rs.getString(2));
                ConceptUID byCui = ConceptUID.fromString(rs.getString(3));
                PTR ptr = new PTR(rs.getString(1), byCui);
                result.put(byCui, new HashMap<PTR, AtomUID>());
                result.get(byCui).put(ptr, paui);
            }
            return result;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    private static class ParentListComparator implements Comparator<PTR> {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(PTR o1, PTR o2) {
            Comparator<AtomUID> c = new UMLSUIDComparator<AtomUID>();
            return o1.compareTo(o2);
        }

    }

    public <T extends ParentsQuerySearchUID> CommonParent<T> getCommonParent(
            T uid1, T uid2, String rela, SABValue sab)
            throws UMLSQueryException {
        Comparator<PTR> c = new ParentListComparator();

        List<PTR> aui1Parents = new ArrayList<PTR>();
        aui1Parents.addAll(getParents(uid1, rela, sab).keySet());
        Collections.sort(aui1Parents, c);

        List<PTR> aui2Parents = new ArrayList<PTR>();
        aui2Parents.addAll(getParents(uid2, rela, sab).keySet());
        Collections.sort(aui2Parents, c);

        for (PTR p : aui1Parents) {
            for (PTR k : aui2Parents) {
                for (int i = p.asList().size() - 1; i >= 0; i--) {
                    for (int j = k.asList().size() - 1; j >= 0; j--) {
                        if (p.asList().get(i).equals(k.asList().get(j))) {
                            return new CommonParent<T>(p.asList().get(i), uid1,
                                    uid2, p.asList().size() - i - 1, k.asList()
                                            .size()
                                            - j - 1);
                        }
                    }
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getChildren(edu.emory.cci.aiw
     * .umls.ConceptUID, java.lang.String, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public List<ConceptUID> getChildren(ConceptUID cui, String rela,
            SABValue sab) throws UMLSQueryException {
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(m2.CUI) from MRHIER, MRCONSO as m1, MRCONSO as m2 where MRHIER.PAUI = m1.AUI and m1.CUI = ?");
            params.add(cui);
            sql.append(" and MRHIER.AUI = m2.AUI");
            if (sab != null) {
                sql.append(" and MRHIER.SAB = ?");
                params.add(sab);
            }
            if (rela != null) {
                sql.append(" and MRHIER.RELA = ?");
                params.add(UMLSQueryStringValue.fromString(rela));
            }

            List<ConceptUID> children = new ArrayList<ConceptUID>();
            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            while (rs.next()) {
                children.add(ConceptUID.fromString(rs.getString(1)));
            }
            return children;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getChilrdren(edu.emory.cci.aiw
     * .umls.AtomUID, java.lang.String, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public List<AtomUID> getChildren(AtomUID aui, String rela, SABValue sab)
            throws UMLSQueryException {
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(AUI) from MRHIER where PAUI = ?");
            params.add(aui);
            if (sab != null) {
                sql.append(" and SAB = ?");
                params.add(sab);
            }
            if (rela != null) {
                sql.append(" and RELA = ?");
                params.add(UMLSQueryStringValue.fromString(rela));
            }

            List<AtomUID> children = new ArrayList<AtomUID>();
            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            while (rs.next()) {
                children.add(AtomUID.fromString(rs.getString(1)));
            }
            return children;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCommonChild(edu.emory.cci
     * .aiw.umls.AtomUID, edu.emory.cci.aiw.umls.AtomUID, java.lang.String,
     * edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public AtomUID getCommonChild(AtomUID aui1, AtomUID aui2, String rela,
            SABValue sab) throws UMLSQueryException {
        List<AtomUID> c1 = getChildren(aui1, rela, sab);
        List<AtomUID> c2 = getChildren(aui2, rela, sab);

        Comparator<AtomUID> cmp = new UMLSUIDComparator<AtomUID>();
        Collections.sort(c1, cmp);
        Collections.sort(c2, cmp);

        for (AtomUID a : c1) {
            for (AtomUID b : c2) {
                if (a.equals(b)) {
                    return a;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getCommonChild(edu.emory.cci
     * .aiw.umls.ConceptUID, edu.emory.cci.aiw.umls.ConceptUID,
     * java.lang.String, edu.emory.cci.aiw.umls.SABValue)
     */
    @Override
    public ConceptUID getCommonChild(ConceptUID cui1, ConceptUID cui2,
            String rela, SABValue sab) throws UMLSQueryException {
        List<ConceptUID> c1 = getChildren(cui1, rela, sab);
        List<ConceptUID> c2 = getChildren(cui2, rela, sab);

        Comparator<ConceptUID> cmp = new UMLSUIDComparator<ConceptUID>();
        Collections.sort(c1, cmp);
        Collections.sort(c2, cmp);

        for (ConceptUID a : c1) {
            for (ConceptUID b : c2) {
                if (a.equals(b)) {
                    return a;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getAvailableSAB(java.lang.String
     * )
     */
    @Override
    public Set<SABValue> getAvailableSAB(String description)
            throws UMLSQueryException {

        try {
            setupConn();
            StringBuilder sql = new StringBuilder("select RSAB, SON from MRSAB");
            if (description != null) {
                sql.append(" where UPPER(SON) like UPPER(?)");
            }

            PreparedStatement query = conn.prepareStatement(sql.toString());
            if (description != null) {
                query.setString(1, "%" + description + "%");
            }
            ResultSet rs = executeAndLogQuery(query);
            Set<SABValue> result = new HashSet<SABValue>();
            while (rs.next()) {
                SABValue sab = SABValue.withNameAndDescription(rs.getString(1),
                        rs.getString(2));
                result.add(sab);
            }
            return result;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } finally {
            tearDownConn();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getDistBF(edu.emory.cci.aiw.
     * umls.ConceptUID, edu.emory.cci.aiw.umls.ConceptUID, java.lang.String,
     * edu.emory.cci.aiw.umls.SABValue, int)
     */
    @Override
    public int getDistBF(ConceptUID cui1, ConceptUID cui2, String rela,
            SABValue sab, int maxR) throws UMLSQueryException {
        Queue<ConceptUID> cuiQue = new LinkedList<ConceptUID>();
        Set<ConceptUID> visited = new HashSet<ConceptUID>();
        Map<Integer, Integer> radiusIdx = new HashMap<Integer, Integer>();
        int queIdx = 0;
        int r = 0;
        radiusIdx.put(r, 0);

        if (maxR <= 0) {
            maxR = 3;
        }

        try {
            setupConn();
            cuiQue.add(cui1);
            visited.add(cui1);

            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            StringBuilder sql = new StringBuilder(
                    "select distinct(CUI2) from MRREL where CUI1 = ? and (rel='PAR' or rel='CHD')");
            params.add(ConceptUID.EMPTY_CUI);
            if (sab != null) {
                sql.append(" and SAB = ?");
                params.add(sab);
            }
            if (rela != null && !rela.equals("")) {
                sql.append(" and RELA = ?");
                params.add(UMLSQueryStringValue.fromString(rela));
            }

            while (!cuiQue.isEmpty()) {
                ConceptUID node = cuiQue.remove();
                params.set(0, node);
                if (node.equals(cui2)) {
                    return r;
                }

                List<ConceptUID> adjNodes = new ArrayList<ConceptUID>();

                ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                        params));
                while (rs.next()) {
                    ConceptUID c2 = ConceptUID.fromString(rs.getString(1));
                    if (!visited.contains(c2)) {
                        adjNodes.add(c2);
                    }
                }

                if (!radiusIdx.containsKey(r + 1)) {
                    radiusIdx.put(r + 1, queIdx + cuiQue.size());
                }
                radiusIdx.put(r + 1, adjNodes.size());

                if (queIdx == radiusIdx.get(r)) {
                    r++;
                }
                queIdx++;

                for (ConceptUID c : adjNodes) {
                    visited.add(c);
                    cuiQue.add(c);
                }
                if (r > maxR) {
                    return r;
                }
            }
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }

        log(Level.FINEST, "Returning -1");
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getNeighbors(edu.emory.cci.aiw
     * .umls.NeighborQuerySearchUID, java.lang.String,
     * edu.emory.cci.aiw.umls.SABValue, java.lang.String)
     */
    @Override
    public List<ConceptUID> getNeighbors(NeighborQuerySearchUID ui,
            String rela, SABValue sab, String rel) throws UMLSQueryException {
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select  distinct(CUI2) from MRREL where "
                            + ui.getKeyName() + " = ?");
            params.add(ui);
            if (sab != null) {
                sql.append(" and SAB = ?");
                params.add(sab);
            }
            if (rela != null && !rela.equals("")) {
                sql.append(" and RELA = ?");
                params.add(UMLSQueryStringValue.fromString(rela));
            }
            if (rel != null && !rel.equals("")) {
                sql.append(" and REL = ?");
                params.add(UMLSQueryStringValue.fromString(rel));
            }

            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            List<ConceptUID> result = new ArrayList<ConceptUID>();
            while (rs.next()) {
                ConceptUID c2 = ConceptUID.fromString(rs.getString(1));
                if (!c2.equals(ui)) {
                    result.add(c2);
                }
            }
            return result;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (MalformedUMLSUniqueIdentifierException muuie) {
            throw new UMLSQueryException(muuie);
        } finally {
            tearDownConn();
        }
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

    private ResultSet executeAndLogQuery(PreparedStatement query)
            throws SQLException {
        log(Level.INFO, "Executing query: " + query);
        return query.executeQuery();
    }

    public void testQuery() throws SQLException {
        PreparedStatement stmt = conn
                .prepareStatement("SELECT * FROM MRCONSO LIMIT 5");
        ResultSet r = stmt.executeQuery();
        while (r.next()) {
            System.out.println(r.getString(1));
        }
    }
}
