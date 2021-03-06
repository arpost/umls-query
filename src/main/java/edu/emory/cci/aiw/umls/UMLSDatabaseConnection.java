/*
 * #%L
 * UMLSQuery
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
import org.apache.commons.lang3.StringUtils;

import org.arp.javautil.sql.DatabaseAPI;
import org.arp.javautil.sql.InvalidConnectionSpecArguments;

/**
 * A database-baked implementation of the {@link UMLSQueryExecutor} interface.
 * An instance is obtained by calling the static method {@link #getConnection}
 * with the parameters for accessing the database. Additionally, the caller must
 * pass in the database API type. Once an instance has been obtained, any of the
 * queries defined in the {@link UMLSQueryExecutor} interface may be executed.
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
     * Returns a
     * <code>UMLSDatabaseConnection</code> for querying a UMLS database. Callers
     * must specify the location and access information for the database, as
     * well as the database API type.
     *
     * @param api the Java database API to use. An instance of the
     * {@link DatabaseAPI} enum, which provides the
     * {@link java.sql.DriverManager} and {@link javax.sql.DataSource} methods.
     * @param url the location of the database
     * @param user the username to access the database
     * @param password the password that goes with the username to access the
     * database
     * @return a <code>UMLSDatabaseConnection</code> accessed by the specified
     * parameters
     */
    public static UMLSDatabaseConnection getConnection(DatabaseAPI api,
            String url, String user, String password) {
        return new UMLSDatabaseConnection(api, url, user, password);
    }

    private void setupConn() throws UMLSQueryException {
        log(Level.FINE, "Attempting to establish database connection...");
        try {
            conn = api.newConnectionSpecInstance(url, user, password)
                    .getOrCreate();
            log(Level.FINE, "Connection established with " + url);
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } catch (InvalidConnectionSpecArguments icsa) {
            throw new UMLSQueryException(icsa);
        }
    }

    private void tearDownConn() throws UMLSQueryException {
        if (conn != null) {
            log(Level.FINE, "Attempting to disconnect from the database...");
            try {
                conn.close();
                log(Level.FINE, "Disconnected from database " + url);
            } catch (SQLException sqle) {
                throw new UMLSQueryException(sqle);
            }
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
    public List<ConceptUID> getCUI(CUIQuerySearchUID uid, List<SAB> sabs,
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
                sql.append(singletonOrSetClause(sabs.get(0).getKeyName(),
                        sabs.size()));
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
            List<SAB> sabs, boolean caseSensitive) throws SQLException {
        StringBuilder sql = new StringBuilder("select distinct(CUI), ");
        sql.append(uids.get(0).getKeyName());
        sql.append(" from MRCONSO where");
        sql.append(caseSensitive ? "BINARY " : " ");
        sql.append(singletonOrSetClause(uids.get(0).getKeyName(), uids.size()));

        if (sabs != null && !sabs.isEmpty()) {
            sql.append(" and ");
            sql.append(singletonOrSetClause(sabs.get(0).getKeyName(),
                    sabs.size()));
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
            List<ConceptUID> cuis, List<SAB> sabs, boolean caseSensitive)
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
            List<SAB> sabs, boolean caseSensitive) throws UMLSQueryException {
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
            List<LexicalUID> luis, List<SAB> sabs, boolean caseSensitive)
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
            List<UMLSQueryStringValue> strings, List<SAB> sabs,
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
            List<StringUID> suis, List<SAB> sabs, boolean caseSensitive)
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
    public List<AtomUID> getAUI(AUIQuerySearchUID uid, SAB sab)
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
    public List<UMLSQueryStringValue> getSTR(STRQuerySearchUID uid, SAB sab,
            LAT lat, UMLSPreferred preferred) throws UMLSQueryException {
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
    public List<SemanticType> getSemanticType(TUIQuerySearchUID uid, SAB sab)
            throws UMLSQueryException {
        try {
            setupConn();

            StringBuilder sql = new StringBuilder(
                    "select distinct(TUI), STY from MRCONSO a, MRSTY b "
                    + "where a.CUI = b.CUI and a.");
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
            List<SemanticType> types = new ArrayList<SemanticType>();
            while (r.next()) {
                types.add(SemanticType.withTUIAndType(
                        TermUID.fromString(r.getString(1)), r.getString(2)));
            }
            return types;
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
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getSemanticTypeForTerm(edu.emory
     * .cci.aiw.umls.TerminologyCode)
     */
    @Override
    public SemanticType getSemanticTypeForTerm(TerminologyCode code)
            throws UMLSQueryException {
        try {
            validateCode(code);
            setupConn();

            SemanticType result = null;
            String sql = "select distinct(TUI), STY from MRCONSO a, MRSTY b where"
                    + " a.CUI = b.CUI and a.CODE = ? and a.SAB = ?";
            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(queryStr(code.getCode()));
            params.add(code.getSab());

            ResultSet r = executeAndLogQuery(substParams(sql, params));
            if (r.next()) {
                result = SemanticType.withTUIAndType(
                        TermUID.fromString(r.getString(1)), r.getString(2));
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
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getSAB(edu.emory.cci.aiw.umls
     * .SABQuerySearchUID)
     */
    @Override
    public List<SAB> getSAB(SABQuerySearchUID uid) throws UMLSQueryException {
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
            List<SAB> sabs = new ArrayList<SAB>();
            while (r.next()) {
                sabs.add(SAB.withName(r.getString(1)));
            }
            return sabs;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } finally {
            tearDownConn();
        }
    }

    private ResultSet mapToId(String phrase, IdType idType, List<SAB> sabs)
            throws SQLException {

        StringBuilder sql = new StringBuilder("select distinct(").append(
                idType.getIdType()).append("), STR from MRCONSO where STR = ?");

        if (sabs != null && !sabs.isEmpty()) {
            sql.append(" and ");
            sql.append(singletonOrSetClause(sabs.get(0).getKeyName(),
                    sabs.size()));
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
            List<SAB> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<AtomUID>> result = new HashMap<String, MapToIdResult<AtomUID>>();
        try {
            setupConn();
            Map<String, List<String>> matches = matches(phrase,
                    mapToId(phrase, IdType.AUI_IDTYPE, sab));
            if (matches.containsKey(phrase)) {
                for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
                    result.put(
                            entry.getKey(),
                            MapToIdResult.<AtomUID>fromUidAndStr(AtomUID
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
                    String permutedString = StringUtils.join(p, ' ');
                    matches = (matches(permutedString,
                            mapToId(permutedString, IdType.AUI_IDTYPE, sab)));
                    for (Map.Entry<String, List<String>> entry : matches
                            .entrySet()) {
                        result.put(entry.getKey(), MapToIdResult
                                .<AtomUID>fromUidAndStr(AtomUID
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
            List<SAB> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<ConceptUID>> result = new HashMap<String, MapToIdResult<ConceptUID>>();
        try {
            setupConn();
            Map<String, List<String>> matches = matches(phrase,
                    mapToId(phrase, IdType.CUI_IDTYPE, sab));
            if (matches.containsKey(phrase)) {
                for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
                    result.put(entry.getKey(), MapToIdResult
                            .<ConceptUID>fromUidAndStr(ConceptUID
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
                    String permutedString = StringUtils.join(p, ' ');
                    matches = (matches(permutedString,
                            mapToId(permutedString, IdType.CUI_IDTYPE, sab)));
                    for (Map.Entry<String, List<String>> entry : matches
                            .entrySet()) {
                        result.put(entry.getKey(), MapToIdResult
                                .<ConceptUID>fromUidAndStr(ConceptUID
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
            List<SAB> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<LexicalUID>> result = new HashMap<String, MapToIdResult<LexicalUID>>();
        try {
            setupConn();
            Map<String, List<String>> matches = matches(phrase,
                    mapToId(phrase, IdType.LUI_IDTYPE, sab));
            if (matches.containsKey(phrase)) {
                for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
                    result.put(entry.getKey(), MapToIdResult
                            .<LexicalUID>fromUidAndStr(LexicalUID
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
                    String permutedString = StringUtils.join(p, ' ');
                    matches = (matches(permutedString,
                            mapToId(permutedString, IdType.LUI_IDTYPE, sab)));
                    for (Map.Entry<String, List<String>> entry : matches
                            .entrySet()) {
                        result.put(entry.getKey(), MapToIdResult
                                .<LexicalUID>fromUidAndStr(LexicalUID
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
            List<SAB> sab) throws UMLSQueryException {
        Map<String, MapToIdResult<StringUID>> result = new HashMap<String, MapToIdResult<StringUID>>();
        try {
            setupConn();
            Map<String, List<String>> matches = matches(phrase,
                    mapToId(phrase, IdType.CUI_IDTYPE, sab));
            if (matches.containsKey(phrase)) {
                for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
                    result.put(entry.getKey(), MapToIdResult
                            .<StringUID>fromUidAndStr(StringUID
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
                    String permutedString = StringUtils.join(p, ' ');
                    matches = (matches(permutedString,
                            mapToId(permutedString, IdType.CUI_IDTYPE, sab)));
                    for (Map.Entry<String, List<String>> entry : matches
                            .entrySet()) {
                        result.put(entry.getKey(), MapToIdResult
                                .<StringUID>fromUidAndStr(StringUID
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
            SAB sab) throws UMLSQueryException {
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
            List<AtomUID> auis, String rela, SAB sab) throws UMLSQueryException {

        Map<AtomUID, Map<PTR, AtomUID>> result = new HashMap<AtomUID, Map<PTR, AtomUID>>();
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(PTR), PAUI, ");
            sql.append(auis.get(0).getKeyName());
            sql.append(" from MRHIER where");
            sql.append(singletonOrSetClause(auis.get(0).getKeyName(),
                    auis.size()));
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
            List<ConceptUID> cuis, String rela, SAB sab)
            throws UMLSQueryException {

        Map<ConceptUID, Map<PTR, AtomUID>> result = new HashMap<ConceptUID, Map<PTR, AtomUID>>();
        List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();

        try {
            setupConn();

            StringBuilder sql = new StringBuilder(
                    "select distinct(PTR), PAUI, ");
            sql.append(cuis.get(0).getKeyName());
            sql.append(" from MRHIER where");
            sql.append(singletonOrSetClause(cuis.get(0).getKeyName(),
                    cuis.size()));
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

    @Override
    public <T extends ParentsQuerySearchUID> CommonParent<T> getCommonParent(
            T uid1, T uid2, String rela, SAB sab) throws UMLSQueryException {
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
                                    .size() - j - 1);
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
    public List<ConceptUID> getChildren(ConceptUID cui, String rela, SAB sab)
            throws UMLSQueryException {
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
            if (rela != null && !rela.equals("")) {
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
    public List<AtomUID> getChildren(AtomUID aui, String rela, SAB sab)
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
            SAB sab) throws UMLSQueryException {
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
            String rela, SAB sab) throws UMLSQueryException {
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
    public Set<SAB> getAvailableSAB(String description)
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
            Set<SAB> result = new HashSet<SAB>();
            while (rs.next()) {
                SAB sab = SAB.withNameAndDescription(rs.getString(1),
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
            SAB sab, int maxR) throws UMLSQueryException {
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
            String rela, SAB sab, String rel) throws UMLSQueryException {
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#codeToUID(edu.emory.cci.aiw.
     * umls.TerminologyCode)
     */
    @Override
    public ConceptUID codeToUID(TerminologyCode code) throws UMLSQueryException {
        if (code == null || code == null || code.getCode().equals("")
                || code.getSab() == null) {
            throw new UMLSQueryException("The code and SAB must not be null");
        }

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(CUI) from MRCONSO where CODE = ? and SAB = ?");
            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(UMLSQueryStringValue.fromString(code.getCode()));
            params.add(code.getSab());
            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            if (rs.next()) {
                return ConceptUID.fromString(rs.getString(1));
            } else {
                return null;
            }
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
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#uidToCode(edu.emory.cci.aiw.
     * umls.ConceptUID, edu.emory.cci.aiw.umls.SAB)
     */
    @Override
    public List<TerminologyCode> uidToCode(CodeQuerySearchUID uid, SAB sab)
            throws UMLSQueryException {
        if (uid == null || sab == null) {
            throw new UMLSQueryException("The UID and SAB must be non-null");
        }

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select distinct(CODE) from MRCONSO where ");
            sql.append(uid.getKeyName());
            sql.append(" = ? and SAB = ?");
            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(uid);
            params.add(sab);

            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            List<TerminologyCode> result = new ArrayList<TerminologyCode>();
            while (rs.next()) {
                result.add(TerminologyCode.fromStringAndSAB(rs.getString(1),
                        sab));
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
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#translateCode(edu.emory.cci.
     * aiw.umls.TerminologyCode, edu.emory.cci.aiw.umls.SAB)
     */
    @Override
    public List<TerminologyCode> translateCode(TerminologyCode from, SAB to)
            throws UMLSQueryException {
        if (from == null || from.getCode() == null || from.getCode().equals("")
                || from.getSab() == null || to == null) {
            throw new UMLSQueryException("Code and SAB must not be null");
        }

        try {
            setupConn();
            StringBuilder sql = new StringBuilder(
                    "select b.CODE from MRCONSO a, MRCONSO b ");
            sql.append("where a.CODE = ? and a.SAB = ? and b.SAB = ? and a.CUI = b.CUI");
            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(queryStr(from.getCode()));
            params.add(from.getSab());
            params.add(to);

            ResultSet rs = executeAndLogQuery(substParams(sql.toString(),
                    params));
            List<TerminologyCode> result = new ArrayList<TerminologyCode>();
            while (rs.next()) {
                result.add(TerminologyCode.fromStringAndSAB(rs.getString(1), to));
            }
            return result;
        } catch (SQLException sqle) {
            throw new UMLSQueryException(sqle);
        } finally {
            tearDownConn();
        }
    }

    @Override
    public List<TerminologyCode> getChildrenByCode(TerminologyCode code)
            throws UMLSQueryException {
        validateCode(code);

        setupConn();
        List<TerminologyCode> childCodes = new ArrayList<TerminologyCode>();

        List<ConceptUID> childCuis = getChildren(codeToUID(code), "",
                code.getSab());
        for (ConceptUID cui : childCuis) {
            childCodes.addAll(uidToCode(cui, code.getSab()));
        }

        tearDownConn();

        return childCodes;
    }

    @Override
    public List<TerminologyCode> getParentsByCode(TerminologyCode code)
            throws UMLSQueryException {
        validateCode(code);

        setupConn();
        List<TerminologyCode> parentCodes = new ArrayList<TerminologyCode>();
        Map<PTR, AtomUID> parentAuis = getParents(codeToUID(code), "",
                code.getSab());
        for (AtomUID aui : parentAuis.values()) {
            for (ConceptUID cui : getCUI(aui,
                    Collections.<SAB>singletonList(code.getSab()), false)) {
                parentCodes.addAll(uidToCode(cui, code.getSab()));
            }
        }
        tearDownConn();
        return parentCodes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getPreferredName(edu.emory.cci
     * .aiw.umls.TerminologyCode)
     */
    @Override
    public String getPreferredName(TerminologyCode code)
            throws UMLSQueryException {
        try {
            validateCode(code);
            setupConn();

            String result = "";
            String sql = new String(
                    "select MRCONSO.STR from MRRANK, MRCONSO where "
                    + "MRRANK.TTY = MRCONSO.TTY and MRRANK.SAB = MRCONSO.SAB and "
                    + "MRCONSO.CODE = ? and MRCONSO.SAB = ? having max(MRRANK.RANK)");
            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(queryStr(code.getCode()));
            params.add(code.getSab());

            ResultSet rs = executeAndLogQuery(substParams(sql, params));
            if (rs.next()) {
                result = rs.getString(1);
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
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getTermDefinition(edu.emory.
     * cci.aiw.umls.TerminologyCode)
     */
    @Override
    public String getTermDefinition(TerminologyCode code)
            throws UMLSQueryException {
        try {
            validateCode(code);
            setupConn();

            String result = "";
            String sql = "select distinct(MRDEF.DEF) from MRDEF, MRCONSO where "
                    + "MRDEF.CUI = MRCONSO.CUI and MRDEF.SAB = MRCONSO.SAB and "
                    + "MRCONSO.SAB = ? and MRCONSO.CODE = ?";
            List<UMLSQuerySearchUID> params = new ArrayList<UMLSQuerySearchUID>();
            params.add(code.getSab());
            params.add(queryStr(code.getCode()));

            ResultSet rs = executeAndLogQuery(substParams(sql, params));
            if (rs.next()) {
                result = rs.getString(1);
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
     * edu.emory.cci.aiw.umls.UMLSQueryExecutor#getTermSubsumption(edu.emory
     * .cci.aiw.umls.TerminologyCode)
     */
    public List<TerminologyCode> getTermSubsumption(TerminologyCode code)
            throws UMLSQueryException, UMLSNoSuchTermException {
        validateCode(code);

        if (!codeExists(code)) {
            throw new UMLSNoSuchTermException("No such terminology code: "
                    + code);
        }

        List<TerminologyCode> result = new ArrayList<TerminologyCode>();

        // stores the unexpanded children
        Queue<TerminologyCode> descendants = new LinkedList<TerminologyCode>();

        result.add(code);
        descendants.addAll(getChildrenByCode(code));

        // loop through all children until the queue is empty, like BFS/DFS
        while (!descendants.isEmpty()) {
            // dequeue from the descendants and set as current term
            TerminologyCode current = descendants.remove();

            // add the current child under examination to the result set
            result.add(current);

            // get all of the current term's children and them to the queue
            List<TerminologyCode> curChildren = getChildrenByCode(current);

            if (!curChildren.isEmpty()) {
                descendants.addAll(curChildren);
            }
        }

        return result;
    }

    private void validateCode(TerminologyCode code) throws UMLSQueryException {
        if (code == null || code.getCode().equals("") || code.getSab() == null) {
            throw new UMLSQueryException("Code and SAB must not be null");
        }
    }

    private boolean codeExists(TerminologyCode code) throws UMLSQueryException {
        return codeToUID(code) != null;
    }

    private UMLSQueryStringValue queryStr(String str) {
        return UMLSQueryStringValue.fromString(str);
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
        log(Level.FINE, "Executing query: " + query);
        return query.executeQuery();
    }
}
