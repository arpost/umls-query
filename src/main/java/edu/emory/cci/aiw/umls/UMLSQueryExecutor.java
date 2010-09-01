package edu.emory.cci.aiw.umls;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.omg.SendingContext.CodeBase;

/**
 * This is an API for querying a UMLS database. It allows searching for unique
 * identifiers, mapping to identifiers, finding parents, children, and
 * neighbors, searching for dictionaries, and calculating distances between
 * identifiers.
 * 
 * @author Michel Mansour
 * 
 */
public interface UMLSQueryExecutor {

    /**
     * Retrieves the Concept Unique Identifiers for the given search UID,
     * optionally restricted to the given dictionaries. The search is for an
     * exact match. Acceptable search types are: Atom Unique Identifiers (AUIs),
     * String Unique Identifiers (SUI), Lexical Unique Identifers (LUI), Concept
     * Unique Identifiers (CUI), or any string. The SABs are the dictionaries to
     * search in; if null, all dictionaries are searched.
     * 
     * @param uid
     *            the unique identifier or text string whose CUI is to be
     *            retrieved
     * @param sabs
     *            a list of dictionaries to restrict the search; if null, all
     *            dictionaries are searched
     * @param caseSensitive
     *            whether the search is case sensitive or not
     * @return a list of <tt>ConceptUID</tt>s for the given search parameter,
     *         possibly restricted by one or more SABs
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<ConceptUID> getCUI(CUIQuerySearchUID uid, List<SAB> sabs,
            boolean caseSensitive) throws UMLSQueryException;

    /**
     * Retrieves the Concept Unique Identifiers (CUIs) for the given CUI values,
     * optionally restricted to a list of dictionaries. The result is a map from
     * the given CUIs to their respective CUI matches.
     * 
     * @param cuis
     *            the list of CUI values to search for
     * @param sabs
     *            the list of SAB dictionaries to restrict the search to; if
     *            null, all dictionaries are searched
     * @param caseSensitive
     *            whether or not the search should be case sensitive
     * @return a map from the given CUI values to their respective CUI matches
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<ConceptUID, List<ConceptUID>> getCUIMultByCUI(
            List<ConceptUID> cuis, List<SAB> sabs, boolean caseSensitive)
            throws UMLSQueryException;

    /**
     * Retrieves the Concept Unique Identifiers (CUIs) for the given Atom Unique
     * Identifer (AUI) values, optionally restricted to a list of dictionaries.
     * The result is a map from the given AUIs to their respective CUI matches.
     * 
     * @param auis
     *            the list of AUI values to search for
     * @param sabs
     *            the list of SAB dictionaries to restrict the search to; if
     *            null, all dictionaries are searched
     * @param caseSensitive
     *            whether or not the search should be case sensitive
     * @return a map from the given AUI values to their respective CUI matches
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<AtomUID, List<ConceptUID>> getCUIMultByAUI(List<AtomUID> auis,
            List<SAB> sabs, boolean caseSensitive) throws UMLSQueryException;

    /**
     * Retrieves the Concept Unique Identifiers (CUIs) for the given Lexical
     * Unique Identifer (LUI) values, optionally restricted to a list of
     * dictionaries. The result is a map from the given LUIs to their respective
     * CUI matches.
     * 
     * @param luis
     *            the list of LUI values to search for
     * @param sabs
     *            the list of SAB dictionaries to restrict the search to; if
     *            null, all dictionaries are searched
     * @param caseSensitive
     *            whether or not the search should be case sensitive
     * @return a map from the given LUI values to their respective CUI matches
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<LexicalUID, List<ConceptUID>> getCUIMultByLUI(
            List<LexicalUID> luis, List<SAB> sabs, boolean caseSensitive)
            throws UMLSQueryException;

    /**
     * Retrieves the Concept Unique Identifiers (CUIs) for the given String
     * Unique Identifer (SUI) values, optionally restricted to a list of
     * dictionaries. The result is a map from the given SUIs to their respective
     * CUI matches.
     * 
     * @param suis
     *            the list of SUI values to search for
     * @param sabs
     *            the list of SAB dictionaries to restrict the search to; if
     *            null, all dictionaries are searched
     * @param caseSensitive
     *            whether or not the search should be case sensitive
     * @return a map from the given SUI values to their respective CUI matches
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<StringUID, List<ConceptUID>> getCUIMultBySUI(
            List<StringUID> suis, List<SAB> sabs, boolean caseSensitive)
            throws UMLSQueryException;

    /**
     * Retrieves the Concept Unique Identifiers (CUIs) for the given text
     * strings, optionally restricted to a list of dictionaries. The result is a
     * map from the given strings to their respective CUI matches.
     * 
     * @param strings
     *            the list of text strings to search for
     * @param sabs
     *            the list of SAB dictionaries to restrict the search to; if
     *            null, all dictionaries are searched
     * @param caseSensitive
     *            whether or not the search should be case sensitive
     * @return a map from the given text strings to their respective CUI matches
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<UMLSQueryStringValue, List<ConceptUID>> getCUIMultByString(
            List<UMLSQueryStringValue> strings, List<SAB> sabs,
            boolean caseSensitive) throws UMLSQueryException;

    /**
     * Retrieves the Atom Unique Identifiers (AUIs) for the given search
     * parameter, optionally restricted by SAB dictionary. The acceptable search
     * types are Concept Unique Identifier (CUI), String Unique Identifier
     * (SUI), Lexical Unique Identifier (LUI), or any text string.
     * 
     * @param uid
     *            the unique identifier or string to search for
     * @param sab
     *            the SAB dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a list of AUIs that match the search parameter
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<AtomUID> getAUI(AUIQuerySearchUID uid, SAB sab)
            throws UMLSQueryException;

    /**
     * Retrieves the string description of the given unique identifier,
     * optionally restricted to the given SAB dictionary and LAT value. The
     * acceptable search types are: Concept Unique Identifier (CUI), Atom Unique
     * Identifier (AUI), String Unique Identifier (SUI), and Lexical Unique
     * Identifier (LUI). The return value is a list of string descriptions.
     * 
     * @param uid
     *            the unique identifier to search for
     * @param sab
     *            the SAB dictionary value to restrict the search to; if null,
     *            all dictionaries are searched
     * @param lat
     *            the LAT value to restrict the search to; if null, all LAT
     *            values are considered
     * @param preferred
     *            whether to consider preferred values. Possible values for this
     *            parameter are: no preference; preferred; and not preferred.
     * @return a list of the string descriptions matching the search parameter
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<UMLSQueryStringValue> getSTR(STRQuerySearchUID uid, SAB sab,
            LAT lat, UMLSPreferred preferred) throws UMLSQueryException;

    /**
     * Retrieves the Term Unique Identifiers (TUIs) for the given search values,
     * optionally restricted to the given SAB dictionary. The acceptable search
     * types are: Concept Unique Identifier (CUI), Atom Unique Identifier (AUI),
     * String Unique Identifier (SUI), Lexical Unique Identifier (LUI), and any
     * text string. A list of TUI values matching the search term is returned.
     * 
     * @param uid
     *            the unique identifier or text string to search for
     * @param sab
     *            SAB dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a list of TUI values matching the search parameter
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<TermUID> getTUI(TUIQuerySearchUID uid, SAB sab)
            throws UMLSQueryException;

    /**
     * Retrieves the SAB dictionaries the given unique identifier or text string
     * belongs to. The search is for an exact match. The acceptable search types
     * are: Concept Unique Identifier (CUI), Atom Unique Identifier (AUI),
     * String Unique Identifier (SUI), Lexical Unique Identifier (LUI), and any
     * text string. A list of SAB values (dictionaries0 is returned.
     * 
     * @param uid
     *            the unique identifier or text string to search for
     * @return a list of SAB dictionaries that match the search parameter
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<SAB> getSAB(SABQuerySearchUID uid) throws UMLSQueryException;

    /**
     * Maps the given phrase to a Concept Unique Identifier (CUI), in the
     * following manner:
     * 
     * First, an exact match search is performed on the phrase. If no match is
     * found, exact match searches are done on all permutations of all lengths
     * of the phrase. Note: This is an O(n!) operation, where n is the number of
     * words in the search phrase. For large n (n > ~10), it may take a while to
     * complete; for very large n, the operation may never complete. The search
     * can be optionally restricted by SAB dictionary.
     * 
     * The result is a map from the portion of the phrase that matched to a pair
     * consisting of the matched CUI and string.
     * 
     * @param phrase
     *            the phrase to search for
     * @param sab
     *            the dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a map of the matched portions of the phrase to the CUI and string
     *         that matched
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<String, MapToIdResult<ConceptUID>> mapToCUI(String phrase,
            List<SAB> sab) throws UMLSQueryException;

    /**
     * Maps the given phrase to a Atom Unique Identifier (AUI), in the following
     * manner:
     * 
     * First, an exact match search is performed on the phrase. If no match is
     * found, exact match searches are done on all permutations of all lengths
     * of the phrase. Note: This is an O(n!) operation, where n is the number of
     * words in the search phrase. For large n (n > ~10), it may take a while to
     * complete; for very large n, the operation may never complete. The search
     * can be optionally restricted by SAB dictionary.
     * 
     * The result is a map from the portion of the phrase that matched to a pair
     * consisting of the matched AUI and string.
     * 
     * @param phrase
     *            the phrase to search for
     * @param sab
     *            the dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a map of the matched portions of the phrase to the AUI and string
     *         that matched
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<String, MapToIdResult<AtomUID>> mapToAUI(String phrase,
            List<SAB> sab) throws UMLSQueryException;

    /**
     * Maps the given phrase to a Lexical Unique Identifier (LUI), in the
     * following manner:
     * 
     * First, an exact match search is performed on the phrase. If no match is
     * found, exact match searches are done on all permutations of all lengths
     * of the phrase. Note: This is an O(n!) operation, where n is the number of
     * words in the search phrase. For large n (n > ~10), it may take a while to
     * complete; for very large n, the operation may never complete. The search
     * can be optionally restricted by SAB dictionary.
     * 
     * The result is a map from the portion of the phrase that matched to a pair
     * consisting of the matched LUI and string.
     * 
     * @param phrase
     *            the phrase to search for
     * @param sab
     *            the dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a map of the matched portions of the phrase to the CUI and string
     *         that matched
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<String, MapToIdResult<LexicalUID>> mapToLUI(String phrase,
            List<SAB> sab) throws UMLSQueryException;

    /**
     * Maps the given phrase to a String Unique Identifier (SUI), in the
     * following manner:
     * 
     * First, an exact match search is performed on the phrase. If no match is
     * found, exact match searches are done on all permutations of all lengths
     * of the phrase. Note: This is an O(n!) operation, where n is the number of
     * words in the search phrase. For large n (n > ~10), it may take a while to
     * complete; for very large n, the operation may never complete. The search
     * can be optionally restricted by SAB dictionary.
     * 
     * The result is a map from the portion of the phrase that matched to a pair
     * consisting of the matched SUI and string.
     * 
     * @param phrase
     *            the phrase to search for
     * @param sab
     *            the dicionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a map of the matched portions of the phrase to the SUI and string
     *         that matched
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<String, MapToIdResult<StringUID>> mapToSUI(String phrase,
            List<SAB> sab) throws UMLSQueryException;

    /**
     * Retrieves the parents of the specified unique identifier, optionally
     * restricted by a particular relationship type and to a given SAB
     * dictionary.
     * 
     * The possible search types are: Concept Unique Identifier (CUI) and Atom
     * Unique Identifier (AUI).
     * 
     * The result is a mapping from the path from the given UI's direct parent
     * to the root (the <tt>PTR</tt> class) to the UI's direct parent. The PTR
     * class stores the path to the root, which can be accessed either as a list
     * of AUIs or as a string of dot-delimited AUIs.
     * 
     * @param uid
     *            the CUI or AUI whose parents are to be found
     * @param rela
     *            the relationship type to restrict the search to; if null, all
     *            relationship types are considered
     * @param sab
     *            the dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a mapping from parent path to direct parent AUI
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<PTR, AtomUID> getParents(ParentsQuerySearchUID uid, String rela,
            SAB sab) throws UMLSQueryException;

    /**
     * Retrieves the parents of a list of Concept Unique Identifiers (CUIs),
     * optionally restricted to a particular relationship type and to a given
     * SAB dictionary.
     * 
     * The result is a map from the specified CUIs to a map of all of their
     * respective parents. The inner map is from the parent path to the direct
     * parent AUI. PTR objects store the path of AUI values to the root AUI,
     * which can be accessed either as a list of AUI, or as a dot-delimited
     * string.
     * 
     * @param cuis
     *            the CUIs to search for
     * @param rela
     *            the relationship type to restrict the search to; if null, all
     *            relationship types are considered
     * @param sab
     *            the SAB dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a mapping from CUI to a mapping from parent path to direct parent
     *         AUI
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<ConceptUID, Map<PTR, AtomUID>> getParentsMultByCUI(
            List<ConceptUID> cuis, String rela, SAB sab)
            throws UMLSQueryException;

    /**
     * Retrieves the parents of a list of Atom Unique Identifiers (AUIs),
     * optionally restricted to a particular relationship type and to a given
     * SAB dictionary.
     * 
     * The result is a map from the specified AUIs to a map of all of their
     * respective parents. The inner map is from the parent path to the direct
     * parent AUI. PTR objects store the path of AUI values to the root AUI,
     * which can be accessed either as a list of AUI, or as a dot-delimited
     * string.
     * 
     * @param auis
     *            the AUIs to search for
     * @param rela
     *            the relationship type to restrict the search to; if null, all
     *            relationship types are considered
     * @param sab
     *            the SAB dictionary to restrict the search to; if null, all
     *            dictionaries are searched
     * @return a mapping from AUI to a mapping from parent path to direct parent
     *         AUI
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Map<AtomUID, Map<PTR, AtomUID>> getParentsMultByAUI(
            List<AtomUID> auis, String rela, SAB sab) throws UMLSQueryException;

    /**
     * Retrieves the common parent of two Concept Unique Identifiers (CUIs) or
     * Atom Unique Identifiers (AUIs). Both identifiers must be of the same
     * type. The search is optionally restricted along a given relationship type
     * and a given SAB dictionary.
     * 
     * The common parent of the two identifiers is returned as a
     * <tt>CommonParent</tt> object, which holds the common parent AUI, the two
     * children, and the number of links from the parent to each child.
     * 
     * @param <T>
     *            the type of unique identifier to be searched for, either CUI
     *            or AUI
     * @param uid1
     *            the first UI
     * @param uid2
     *            the second UI
     * @param rela
     *            if not null, the relationship to restrict the search to;
     *            otherwise, all relationships are considered
     * @param sab
     *            if not null, the dictionary to restrict the search to;
     *            otherwise, all dictionaries are considered
     * @return the common parent of the specified UIs
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public <T extends ParentsQuerySearchUID> CommonParent<T> getCommonParent(
            T uid1, T uid2, String rela, SAB sab) throws UMLSQueryException;

    /**
     * Retrieves the direct children Concept Unique Identifiers (CUIs) for the
     * given CUI, optionally restricted along a given relationship type and to a
     * given SAB dictionary.
     * 
     * @param cui
     *            the CUI whose children are to be retrieved
     * @param rela
     *            if not null, the relationship type to restrict the search to;
     *            otherwise, all relationship types are considered
     * @param sab
     *            if not null, the SAB dictionary to restrict the search to;
     *            otherwise, all dictionaries are searched
     * @return the list of CUIs that are the children of the specified CUI
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<ConceptUID> getChildren(ConceptUID cui, String rela, SAB sab)
            throws UMLSQueryException;

    /**
     * Retrieves the direct children Atom Unique Identifiers (AUIs) for the
     * given AUI, optionally restricted along a given relationship type and to a
     * given SAB dictionary.
     * 
     * @param aui
     *            the AUI whose children are to be retrieved
     * @param rela
     *            if not null, the relationship type to restrict the search to;
     *            otherwise, all relationship types are considered
     * @param sab
     *            if not null, the SAB dictionary to restrict the search to;
     *            otherwise, all dictionaries are searched
     * @return the list of AUIs that are the children of the specified AUI
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<AtomUID> getChildren(AtomUID aui, String rela, SAB sab)
            throws UMLSQueryException;

    /**
     * Retrieves the child Concept Unique Identifier (CUI) that is common to the
     * two specified CUIs. The search is optionally restricted to a particular
     * relationship type and to a particular SAB dictionary.
     * 
     * @param cui1
     *            the first CUI
     * @param cui2
     *            the second CUI
     * @param rela
     *            if not null, the relationship to restrict the search to;
     *            otherwise, all relationships are considered
     * @param sab
     *            if not null, the dictionary to restrict the search to;
     *            otherwise, all relationships are considered
     * @return the CUI that is the common child of the specified CUIs
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public ConceptUID getCommonChild(ConceptUID cui1, ConceptUID cui2,
            String rela, SAB sab) throws UMLSQueryException;

    /**
     * Retrieves the child Atom Unique Identifier (AUI) that is common to the
     * two specified AUIs. The search is optionally restricted to a particular
     * relationship type and to a particular SAB dictionary.
     * 
     * @param aui1
     *            the first AUI
     * @param aui2
     *            the second AUI
     * @param rela
     *            if not null, the relationship to restrict the search to;
     *            otherwise, all relationships are considered
     * @param sab
     *            if not null, the dictionary to restrict the search to;
     *            otherwise, all relationships are considered
     * @return the AUI that is the common child of the specified AUIs
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public AtomUID getCommonChild(AtomUID aui1, AtomUID aui2, String rela,
            SAB sab) throws UMLSQueryException;

    /**
     * Retrieves all the SAB dictionaries whose descriptions contain the search
     * string. The result is a set of SABs.
     * 
     * @param term
     *            the search terms to match in the SAB descriptions
     * @return a <code>Set</code> of <code>SABValue</code> objects.
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public Set<SAB> getAvailableSAB(String term) throws UMLSQueryException;

    /**
     * Retrieves the distance from a specified Concept Unique Identifier (CUI)
     * to a second CUI, using a breadth first search. The search is optionally
     * restricted along a particular relationship and to a particular SAB
     * dictionary. The search ends when either the second CUI is found, or when
     * the search radius exceeds the given maximum.
     * 
     * @param cui1
     *            the CUI to start from
     * @param cui2
     *            the CUI to be found
     * @param rela
     *            if not null, the relationship to restrict the search to;
     *            otherwise, all relationships are considered
     * @param sab
     *            if not null, the dictionary to restrict the search to;
     *            otherwise, all relationships are considered
     * @param maxR
     *            the maximum radius to consider when searching for
     *            <tt>cui2</tt>. If this value is <= 0, then the default value
     *            of 3 is used.
     * @return the distance from <tt>cui1</tt> to <tt>cui2</tt>
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public int getDistBF(ConceptUID cui1, ConceptUID cui2, String rela,
            SAB sab, int maxR) throws UMLSQueryException;

    /**
     * Retrieves the neighboring Concept Unique Identifiers for the given CUI or
     * Atom Unique Identifier (AUI). The search is optionally restricted along a
     * given relationship and to a given SAB dictionary.
     * 
     * @param ui
     *            the CUI or AUI whose neighbors are to be found
     * @param rela
     *            if not null, the relationship to restrict the search to;
     *            otherwise, all relationships are considered
     * @param sab
     *            if not null, the dictionary to restrict the search to;
     *            otherwise, all relationships are considered
     * @param rel
     * @return a list of CUIs that are the neighbors of the given CUI or AUI
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<ConceptUID> getNeighbors(NeighborQuerySearchUID ui,
            String rela, SAB sab, String rel) throws UMLSQueryException;

    /**
     * Retrieves the terminology code for the given UID as it is represented in
     * the given terminology (SAB). The acceptable UIDs are {@link ConceptUID}
     * (CUI) and {@link AtomUID} (AUI).
     * 
     * @param uid
     *            the AUI or CUI whose terminology code is to be found
     * @param sab
     *            the terminology (SAB) to use
     * @return a {@link TerminologyCode} storing the resulting code
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<TerminologyCode> uidToCode(CodeQuerySearchUID uid, SAB sab)
            throws UMLSQueryException;

    /**
     * Retrieves the the Concept Unique Identifier (CUI) for the given
     * terminology code.
     * 
     * @param code
     *            the terminology code whose CUI is to be found
     * @return the {@link ConceptUID} associated with the given code
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public ConceptUID codeToUID(TerminologyCode code) throws UMLSQueryException;

    /**
     * Translates a terminology code from its usage in one SAB terminology to
     * another. It does this via CUIs. This is basically a convenience method
     * for:
     * <p>
     * <code>uidToCode(codeToUid(TerminologyCode.fromCodeAndSAB(code, sab1)), sab2);</code>
     * 
     * @param from
     *            the code to translate from
     * @param to
     *            the SAB terminology to translate to
     * @return a <code>List</code> of {@link TerminologyCode} with the code for
     *         the concept as it appears in the "to" SAB
     * @throws UMLSQueryException
     *             if something goes wrong during the query execution
     */
    public List<TerminologyCode> translateCode(TerminologyCode from, SAB to)
            throws UMLSQueryException;

    /**
     * Finds the parent codes of the given terminology code with the same
     * terminology.
     * 
     * @param code
     *            the terminology code whose parents are to be retrieved
     * @return a list of <code>TerminologyCode</code> objects that are the
     *         parents of the specified code
     * @throws UMLSQueryException
     *             if something goes wrong during query execution
     */
    public List<TerminologyCode> getParentsByCode(TerminologyCode code)
            throws UMLSQueryException;

    /**
     * Finds the child codes of the given terminology code in the same
     * terminology.
     * 
     * @param code
     *            the terminology code whose children are to be retrieved
     * @return a list <code>TerminologyCode</code> objewcts that are the
     *         children of the specified code
     * @throws UMLSQueryException
     *             if something goes wrong during query execution
     */
    public List<TerminologyCode> getChildrenByCode(TerminologyCode code)
            throws UMLSQueryException;
}
