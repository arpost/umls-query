package edu.emory.cci.aiw.umls;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        sabs.add(SABValue.withName("SNOMEDCT"));
        sabs.add(SABValue.withName("RXNORM"));
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
        assertEquals(2, auis.size());
        Set<AtomUID> actual = new HashSet<AtomUID>(auis);
        Set<AtomUID> expected = new HashSet<AtomUID>();
        expected.add(AtomUID.fromString("A4786634"));
        expected.add(AtomUID.fromString("A3042752"));
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSTR() throws Exception {
        List<UMLSQueryStringValue> strings = conn.getSTR(AtomUID
                .fromString("A3042752"), sabs.get(0), null,
                UMLSPreferred.NO_PREFERENCE);
        assertEquals(1, strings.size());
        assertEquals(UMLSQueryStringValue
                .fromString("Malignant tumour of prostate"), strings.get(0));
    }

    @Test
    public void testGetTUI() throws Exception {
        List<TermUID> tuis = conn.getTUI(UMLSQueryStringValue
                .fromString("Malignant tumour of prostate"), sabs.get(0));
        assertEquals(1, tuis.size());
        assertEquals(TermUID.fromString("T191"), tuis.get(0));
    }

    @Test
    public void testGetSAB() throws Exception {
        List<SABValue> sabs = conn.getSAB(UMLSQueryStringValue
                .fromString("prostate"));
        assertEquals(2, sabs.size());
        Set<SABValue> actual = new HashSet<SABValue>(sabs);
        Set<SABValue> expected = new HashSet<SABValue>();
        expected.add(sabs.get(0));
        expected.add(sabs.get(1));
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToCUI() throws Exception {
        Map<String, MapToIdResult<ConceptUID>> results = conn.mapToCUI(
                "intraductal carcinoma of prostate", sabs);
        assertEquals(6, results.size());
        Map<String, MapToIdResult<ConceptUID>> expected = new HashMap<String, MapToIdResult<ConceptUID>>();
        expected.put("intraductal", MapToIdResult.fromUidAndStr(ConceptUID
                .fromString("C1644197"), UMLSQueryStringValue
                .fromString("Intraductal")));
        expected.put("intraductal carcinoma", MapToIdResult.fromUidAndStr(
                ConceptUID.fromString("C0007124"), UMLSQueryStringValue
                        .fromString("Intraductal carcinoma")));
        expected.put("carcinoma", MapToIdResult.fromUidAndStr(ConceptUID
                .fromString("C0007097"), UMLSQueryStringValue
                .fromString("Carcinoma")));
        expected.put("carcinoma of prostate", MapToIdResult.fromUidAndStr(
                ConceptUID.fromString("C0600139"), UMLSQueryStringValue
                        .fromString("Carcinoma of prostate")));
        expected.put("prostate", MapToIdResult.fromUidAndStr(ConceptUID
                .fromString("C0033572"), UMLSQueryStringValue
                .fromString("Prostate")));
        expected.put("prostate carcinoma", MapToIdResult.fromUidAndStr(
                ConceptUID.fromString("C0600139"), UMLSQueryStringValue
                        .fromString("Prostate carcinoma")));
        assertEquals(expected, results);
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
        assertEquals(675, parents.size());
        parents = conn.getParents(ConceptUID.fromString("C0600139"), "isa",
                null);
        assertEquals(370, parents.size());
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
        assertEquals(AtomUID.fromString("A3684559"), cp.getParent());
        assertEquals(3, cp.getChild1Links());
        assertEquals(8, cp.getChild2Links());
    }

    @Test
    public void testGetChildrenCUI() throws Exception {
        List<ConceptUID> children = conn.getChildren(ConceptUID
                .fromString("C0376358"), "isa", null);
        assertEquals(6, children.size());
        Set<ConceptUID> actual = new HashSet<ConceptUID>(children);
        Set<ConceptUID> expected = new HashSet<ConceptUID>();
        expected.add(ConceptUID.fromString("C1330959"));
        expected.add(ConceptUID.fromString("C1328504"));
        expected.add(ConceptUID.fromString("C0347001"));
        expected.add(ConceptUID.fromString("C1297952"));
        expected.add(ConceptUID.fromString("C1302530"));
        expected.add(ConceptUID.fromString("C1282482"));
        assertEquals(expected, actual);
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
        Set<SABValue> actual = conn.getAvailableSAB("SNOMED");
        Set<SABValue> expected = new HashSet<SABValue>();
        expected.add(SABValue.withNameAndDescription("SNMI",
                "SNOMED Clinical Terms, 2009_07_31"));
        expected
                .add(SABValue
                        .withNameAndDescription(
                                "SCTSPA",
                                "SNOMED Terminos Clinicos (SNOMED CT), Edicion en Espanol, Distribucion Internacional, Octubre de 2009, 2009_10_31"));
        expected.add(SABValue.withNameAndDescription("SNM", "SNOMED-2, 2"));
        expected.add(SABValue.withNameAndDescription("SNOMEDCT",
                "SNOMED International, 1998"));
        assertEquals(expected, actual);
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
