/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import junit.framework.TestCase;

/**
 *
 * @author Thomas Salliou
 */
public class HouseholdTest extends TestCase {
    
    public HouseholdTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private Parcel defaultParcelBuilder(int codeInsee) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,2,30,40,10,50,codeInsee,"AB",geometry);
    }
    
    /*
     * Tests age incrementation.
     */
    public void testGrowIncrementation() {
        Household simpson = new Household(5,31,45000);
        simpson.grow();
        assertTrue(simpson.getAge() == 32);
    }
    
    /*
     * Tests wealth calculation when the result expected is an integer.
     */
    public void testGetWealthIntegerResult() {
        Household simpson = new Household(4,30,45000);
        int wealth = simpson.getWealth();
        assertTrue(wealth == 22500);
    }
    
    /*
     * Tests wealth calculation when the result expected is a float. 
     * Checks if the result is the appropriate floor integer.
     */
    public void testGetWealthFloatResult() {
        Household simpson = new Household(1,29,41159);
        int wealth = simpson.getWealth();
        assertTrue(wealth == 19893);
    }
    
    /*
     * Tests the getWillMoveCoefficient method, in two cases :
     * Inside and outside Nantes (codeInsee == 44109)
     */
    public void testGetWMC() throws ParseException {
        Parcel nantesParcel = defaultParcelBuilder(44109);
        Household nantesHousehold = new Household(2,40,50000,nantesParcel);
        assertTrue(nantesHousehold.getWillMoveCoefficient() == 30);
        
        Parcel notNantesParcel = defaultParcelBuilder(44789);
        Household notNantesHousehold = new Household(3,65,50000,notNantesParcel);
        assertTrue(notNantesHousehold.getWillMoveCoefficient() == 8);
    }
    
}
