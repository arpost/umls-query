package edu.emory.cci.aiw.umls;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.arp.javautil.sql.DatabaseAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UMLSDatabaseConnectionTest {
	private UMLSDatabaseConnection conn;
	private List<SABValue> sabs;

	public UMLSDatabaseConnectionTest() {

	}

	@org.junit.BeforeClass
	public static void setUpClass() throws Exception {

	}

	@org.junit.AfterClass
	public static void tearDownClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		String url = "jdbc:mysql://aiwdev02.eushc.org:3307/umls_2010AA", user = "umlsuser", pass = "3SqQgPOh";
		this.conn = UMLSDatabaseConnection.getConnection(
		        DatabaseAPI.DRIVERMANAGER, url, user, pass);
		sabs = new ArrayList<SABValue>();
		sabs.add(SABValue.fromString("SNOMEDCT"));
		sabs.add(SABValue.fromString("RXNORM"));
	}

	@After
	public void tearDown() throws Exception {
		this.conn = null;
	}

	@Test
	public void testGetCUI() throws Exception {
		List<ConceptUID> cuis = conn.getCUI(UMLSQueryStringValue
		        .fromString("Malignant tumour of prostate"), sabs, false);
		assertEquals(1, cuis.size());
		assertEquals(ConceptUID.fromString("C0376358"), cuis.get(0));
	}

	@Test
	public void testGetCUIMultByCUI() throws Exception {

	}

	@Test
	public void testGetCUIMultByAUI() {

	}

	@Test
	public void testGetCUIMultByLUI() {

	}

	@Test
	public void testGetCUIMultBySUI() {

	}

	@Test
	public void testCUIMultByString() {

	}

	@Test
	public void testGetAUI() throws Exception {
		List<AtomUID> auis = conn.getAUI(UMLSQueryStringValue
		        .fromString("Malignant tumour of prostate"), sabs.get(0));
	}

	@Test
	public void testGetSTR() throws Exception {
		List<UMLSQueryStringValue> strings = conn.getSTR(AtomUID
		        .fromString("A3042752"), sabs.get(0), null,
		        UMLSPreferred.NO_PREFERENCE);
	}

	@Test
	public void testGetTUI() throws Exception {
		List<TermUID> tuis = conn.getTUI(UMLSQueryStringValue
		        .fromString("Malignant tumour of prostate"), sabs.get(0));
	}

	@Test
	public void testGetSAB() throws Exception {
		List<SABValue> sabs = conn.getSAB(UMLSQueryStringValue
		        .fromString("prostate"));
	}

	@Test
	public void testMapToCUI() throws Exception {
		Map<String, MapToIdResult<ConceptUID>> results = conn.mapToCUI(
		        "intraductal carcinoma of prostate", sabs);
	}

	@Test
	public void testMapToAUI() {

	}

	@Test
	public void testMapToLUI() {

	}

	@Test
	public void testMapToSUI() {

	}

	@Test
	public void testGetParents() throws Exception {
		Map<PTR, AtomUID> parents = conn.getParents(ConceptUID
		        .fromString("C0007124"), "isa", null);
	}

	@Test
	public void testGetParentsMultByCUI() {

	}

	@Test
	public void testGetParentsMultByAUI() {

	}

	@Test
	public void testGetCommonParent() throws Exception {
		CommonParent<ConceptUID> cp = conn.getCommonParent(ConceptUID
		        .fromString("C0600139"), ConceptUID.fromString("C0007124"),
		        null, null);
	}

	@Test
	public void testGetChildrenCUI() throws Exception {
		List<ConceptUID> children = conn.getChildren(ConceptUID
		        .fromString("C0376358"), "isa", null);
	}

	@Test
	public void testGetChildrenAUI() {

	}

	@Test
	public void testGetCommonChildCUI() {

	}

	@Test
	public void testGetCommonChildAUI() {

	}

	@Test
	public void testGetAvailableSAB() throws Exception {
		Map<SABValue, String> sabsFound = conn.getAvailableSAB("SNOMED");
	}

	@Test
	public void testGetDistBF() throws Exception {
		assertEquals(2, conn.getDistBF(ConceptUID.fromString("C0600139"),
		        ConceptUID.fromString("C0007124"), "", null, 0));
	}

	@Test
	public void testGetNeighbors() {

	}

}
