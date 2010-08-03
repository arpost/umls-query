package edu.emory.cci.aiw.umls;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UMLSQueryExecutor {
	
	public void init() throws Exception;
	
	public void finish() throws Exception;
	
	public List<ConceptUID> getCUI(CUIQuerySearchUID uid, List<SABValue> sabs,
									boolean caseSensitive);
	
	public Map<String, List<ConceptUID>> getCUIMult(List<CUIQuerySearchUID> uids, 
													List<SABValue> sabs,
													boolean caseSensitive);
//	
//	public List<AtomUID> getAUI(AUIQuerySearchUID uid, SABValue sab);
//	
//	public List<StringUID> getSTR(STRQuerySearchUID uid, SABValue sab,
//									LATValue lat, UMLSPreferred preferred);
//	
//	public List<TermUID> getTUI(TUIQuerySearchUID uid, SABValue sab);
//	
//	public List<SABValue> getSAB(SABQuerySearchUID uid);
//	
//	public Object mapToId(CUIQuerySearchUID uid, String idType, SABValue sab);
//	
//	public void getParents(ParentsQuerySearchUID uid, String rela, 
//								SABValue sab);
//	
//	public Map<String, String> getParentsMultByCUI(List<ConceptUID> cuis,
//													String rela, SABValue sab);
//	
//	public Map<String, String> getParentsMultByAUI(List<AtomUID> auis,
//													String rela, SABValue sab);
//	
//	public void getCommonParents(ConceptUID cui1, ConceptUID cui2,
//			String rela, SABValue sab);
//	
//	public void getCommonParents(AtomUID aui1, AtomUID aui2,
//			String rela, SABValue sab);
//	
//	public void getChildren(ConceptUID cui, String rela, SABValue sab);
//	
//	public void getChilrdren(AtomUID aui, String rela, SABValue sab);
//	
//	public void getCommonChild(ConceptUID cui1, ConceptUID cui2,
//			String rela, SABValue sab);
//	
//	public void getCommonChild(AtomUID aui1, AtomUID aui2,
//			String rela, SABValue sab);
//	
//	public Map<SABValue, String> getAvailableSAB(String description);
//	
//	public int getDistBF(ConceptUID cui1, ConceptUID cui2);
//	
//	public List<ConceptUID> getNeighbors(NeighborQuerySearchUID ui,
//											String rela, SABValue sab,
//											String rel);
}
