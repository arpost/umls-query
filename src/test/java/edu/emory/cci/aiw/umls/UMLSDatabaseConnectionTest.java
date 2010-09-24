package edu.emory.cci.aiw.umls;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
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
    private List<SAB> sabs;

    private final SAB ICD9SAB = SAB.withName("ICD9CM");
    private final SAB SNOMEDCTSAB = SAB.withName("SNOMEDCT");

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
        sabs = new ArrayList<SAB>();
        sabs.add(SAB.withName("SNOMEDCT"));
        sabs.add(SAB.withName("RXNORM"));
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
        List<SemanticType> tuis = conn.getSemanticType(UMLSQueryStringValue
                .fromString("Malignant tumour of prostate"), sabs.get(0));
        assertEquals(1, tuis.size());
        System.out.println(tuis.get(0));
        assertEquals(SemanticType.withTUIAndType(TermUID.fromString("T191"),
                "Neoplastic Process"), tuis.get(0));
    }

    @Test
    public void testGetSAB() throws Exception {
        List<SAB> sabs = conn.getSAB(UMLSQueryStringValue
                .fromString("prostate"));
        assertEquals(2, sabs.size());
        Set<SAB> actual = new HashSet<SAB>(sabs);
        Set<SAB> expected = new HashSet<SAB>();
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
    public void testGetChildrenAUI() throws Exception {
        List<AtomUID> children = conn.getChildren(AtomUID
                .fromString("A3323363"), "isa", null);
        assertEquals(9, children.size());
        Set<AtomUID> actual = new HashSet<AtomUID>(children);
        Set<AtomUID> expected = new HashSet<AtomUID>();
        expected.add(AtomUID.fromString("A2949514"));
        expected.add(AtomUID.fromString("A2949516"));
        expected.add(AtomUID.fromString("A3295134"));
        expected.add(AtomUID.fromString("A3095622"));
        expected.add(AtomUID.fromString("A3184304"));
        expected.add(AtomUID.fromString("A3594641"));
        expected.add(AtomUID.fromString("A3567685"));
        expected.add(AtomUID.fromString("A3586937"));
        expected.add(AtomUID.fromString("A16962310"));
        assertEquals(expected, actual);

    }

    @Test
    public void testGetCommonChildCUI() throws Exception {
        ConceptUID child = conn.getCommonChild(ConceptUID
                .fromString("C0376358"), ConceptUID.fromString("C0346554"), "",
                null);
        assertEquals(null, child);
    }

    @Test
    public void testGetCommonChildAUI() throws Exception {
        AtomUID child = conn.getCommonChild(AtomUID.fromString("A3261244"),
                AtomUID.fromString("A3339540"), "", null);
        assertEquals(null, child);
    }

    @Test
    public void testGetAvailableSAB() throws Exception {
        Set<SAB> actual = conn.getAvailableSAB("SNOMED");
        Set<SAB> expected = new HashSet<SAB>();
        expected.add(SAB.withNameAndDescription("SNMI",
                "SNOMED Clinical Terms, 2009_07_31"));
        expected
                .add(SAB
                        .withNameAndDescription(
                                "SCTSPA",
                                "SNOMED Terminos Clinicos (SNOMED CT), Edicion en Espanol, Distribucion Internacional, Octubre de 2009, 2009_10_31"));
        expected.add(SAB.withNameAndDescription("SNM", "SNOMED-2, 2"));
        expected.add(SAB.withNameAndDescription("SNOMEDCT",
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

    @Test
    public void testUidToCode() throws Exception {
        SAB sab = SAB.withName("ICD9CM");
        ConceptUID cui = ConceptUID.fromString("C0271635");
        List<TerminologyCode> actual = conn.uidToCode(cui, sab);

        List<TerminologyCode> expected = new ArrayList<TerminologyCode>();
        expected.add(TerminologyCode.fromStringAndSAB("250.0", sab));
        assertEquals(expected, actual);

        actual.clear();
        expected.clear();

        sab = SAB.withName("SNOMEDCT");
        actual = conn.uidToCode(cui, sab);
        expected.add(TerminologyCode.fromStringAndSAB("111552007", sab));
        expected.add(TerminologyCode.fromStringAndSAB("190321005", sab));
        expected.add(TerminologyCode.fromStringAndSAB("154674007", sab));
        expected.add(TerminologyCode.fromStringAndSAB("190324002", sab));

        assertEquals(expected, actual);

        AtomUID aui = AtomUID.fromString("A8340910");
        sab = SAB.withName("ICD9CM");
        actual = conn.uidToCode(aui, sab);
        expected.clear();
        expected.add(TerminologyCode.fromStringAndSAB("250.0", sab));
        assertEquals(expected, actual);

    }

    @Test
    public void testCodeToUid() throws Exception {
        ConceptUID expected = ConceptUID.fromString("C0271635");
        TerminologyCode icd9Code = TerminologyCode.fromStringAndSAB("250.0",
                SAB.withName("ICD9CM"));
        TerminologyCode snomedCode = TerminologyCode.fromStringAndSAB(
                "111552007", SAB.withName("SNOMEDCT"));

        ConceptUID cui = conn.codeToUID(icd9Code);
        assertEquals(expected, cui);

        cui = conn.codeToUID(snomedCode);
        assertEquals(expected, cui);
    }

    @Test
    public void testTranslateCode() throws Exception {
        SAB sab1 = SAB.withName("ICD9CM");
        SAB sab2 = SAB.withName("SNOMEDCT");
        List<TerminologyCode> actual = conn.translateCode(TerminologyCode
                .fromStringAndSAB("250.0", sab1), sab2);
        List<TerminologyCode> expected = new ArrayList<TerminologyCode>();
        expected.add(TerminologyCode.fromStringAndSAB("111552007", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("190321005", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("154674007", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("154674007", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("190324002", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("190321005", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("154674007", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("190324002", sab2));
        expected.add(TerminologyCode.fromStringAndSAB("111552007", sab2));
        assertEquals(expected, actual);
    }

    @Test
    public void testGetChildrenByCode() throws Exception {
        List<TerminologyCode> expected = new ArrayList<TerminologyCode>();
        expected.add(TerminologyCode.fromStringAndSAB("250.00", ICD9SAB));
        expected.add(TerminologyCode.fromStringAndSAB("250.01", ICD9SAB));
        expected.add(TerminologyCode.fromStringAndSAB("250.02", ICD9SAB));
        expected.add(TerminologyCode.fromStringAndSAB("250.03", ICD9SAB));

        List<TerminologyCode> actual = conn.getChildrenByCode(TerminologyCode
                .fromStringAndSAB("250.0", ICD9SAB));
        assertEquals(expected, actual);
    }

    @Test
    public void testGetParentsByCode() throws Exception {
        List<TerminologyCode> expected = Collections
                .<TerminologyCode> singletonList(TerminologyCode
                        .fromStringAndSAB("250", ICD9SAB));
        List<TerminologyCode> actual = conn.getParentsByCode(TerminologyCode
                .fromStringAndSAB("250.0", ICD9SAB));
        assertEquals(expected, actual);

        expected = Collections.<TerminologyCode> singletonList(TerminologyCode
                .fromStringAndSAB("249-259.99", ICD9SAB));
        actual = conn.getParentsByCode(TerminologyCode.fromStringAndSAB("250",
                ICD9SAB));
        assertEquals(expected, actual);
    }

    @Test
    public void testGetPreferredName() throws Exception {
        String expected = "Dipalmitoylphosphatidylcholine";
        String actual = conn.getPreferredName(TerminologyCode.fromStringAndSAB(
                "102735002", SNOMEDCTSAB));
        assertEquals(expected, actual);
    }
}
