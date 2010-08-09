package edu.emory.cci.aiw.umls;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UMLSQueryExecutor {

	public void init() throws Exception;

	public void finish() throws Exception;

	public List<ConceptUID> getCUI(CUIQuerySearchUID uid, List<SABValue> sabs,
	        boolean caseSensitive);

	public Map<ConceptUID, List<ConceptUID>> getCUIMultByCUI(
	        List<ConceptUID> cuis, List<SABValue> sabs, boolean caseSensitive);

	public Map<AtomUID, List<ConceptUID>> getCUIMultByAUI(List<AtomUID> auis,
	        List<SABValue> sabs, boolean caseSensitive);

	public Map<LexicalUID, List<ConceptUID>> getCUIMultByLUI(
	        List<LexicalUID> luis, List<SABValue> sabs, boolean caseSensitive);

	public Map<StringUID, List<ConceptUID>> getCUIMultBySUI(
	        List<StringUID> suis, List<SABValue> sabs, boolean caseSensitive);

	public Map<UMLSQueryStringValue, List<ConceptUID>> getCUIMultByString(
	        List<UMLSQueryStringValue> strings, List<SABValue> sabs,
	        boolean caseSensitive);

	public List<AtomUID> getAUI(AUIQuerySearchUID uid, SABValue sab);

	public List<UMLSQueryStringValue> getSTR(STRQuerySearchUID uid,
	        SABValue sab, LATValue lat, UMLSPreferred preferred);

	public List<TermUID> getTUI(TUIQuerySearchUID uid, SABValue sab);

	public List<SABValue> getSAB(SABQuerySearchUID uid);

	public Map<String, MapToIdResult<ConceptUID>> mapToCUI(String phrase,
	        List<SABValue> sab);

	public Map<String, MapToIdResult<AtomUID>> mapToAUI(String phrase,
	        List<SABValue> sab);

	public Map<String, MapToIdResult<LexicalUID>> mapToLUI(String phrase,
	        List<SABValue> sab);

	public Map<String, MapToIdResult<StringUID>> mapToSUI(String phrase,
	        List<SABValue> sab);

	public Map<PTR, AtomUID> getParents(ParentsQuerySearchUID uid,
	        String rela, SABValue sab);

	public Map<ConceptUID, Map<PTR, AtomUID>> getParentsMultByCUI(
	        List<ConceptUID> cuis, String rela, SABValue sab);

	public Map<AtomUID, Map<PTR, AtomUID>> getParentsMultByAUI(
	        List<AtomUID> auis, String rela, SABValue sab);

	public <T extends ParentsQuerySearchUID> CommonParent<T> getCommonParent(
	        T uid1, T uid2, String rela, SABValue sab);

	public List<ConceptUID> getChildren(ConceptUID cui, String rela, SABValue sab);

	public List<AtomUID> getChildren(AtomUID aui, String rela, SABValue sab);
		
	public ConceptUID getCommonChild(ConceptUID cui1, ConceptUID cui2,
			String rela, SABValue sab);

	public AtomUID getCommonChild(AtomUID aui1, AtomUID aui2,
			String rela, SABValue sab);

	public Map<SABValue, String> getAvailableSAB(String description);
		
	public int getDistBF(ConceptUID cui1, ConceptUID cui2, String rela, SABValue sab, int maxR);
	 
	public List<ConceptUID> getNeighbors(NeighborQuerySearchUID ui,
	        String rela, SABValue sab, String rel);
}
