package edu.emory.cci.aiw.umls;

import org.arp.javautil.sql.DatabaseAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UMLSDatabaseConnectionTest {
	private UMLSDatabaseConnection instance;

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
		String url = "", user = "", pass = "";
		this.instance = UMLSDatabaseConnection.getConnection(
		        DatabaseAPI.DRIVERMANAGER, url, user, pass);
	}
	
	@After
	public void tearDown() throws Exception {
		this.instance = null;
	}
	
	@Test
	public void testGetCUI() {
		
	}
	
	@Test
	public void testGetCUIMultByCUI() {
		
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
	public void testGetAUI() {
		
	}
	
	@Test
	public void testGetSTR() {
		
	}
	
	@Test
	public void testGetTUI() {
		
	}
	
	@Test
	public void testGetSAB() {
		
	}
	
	@Test
	public void testMapToCUI() {
		
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
	public void testGetParents() {
		
	}
	
	@Test
	public void testGetParentsMultByCUI() {
		
	}
	
	@Test
	public void testGetParentsMultByAUI() {
		
	}
	
	@Test
	public void testGetCommonParent() {
		
	}
	
	@Test
	public void testGetChildrenCUI() {
		
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
	public void testGetAvailableSAB() {
		
	}
	
	@Test
	public void testGetDistBF() {
		
	}
	
	@Test
	public void testGetNeighbors() {
		
	}
	
}
