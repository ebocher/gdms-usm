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

    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    
    private Parcel defaultParcelBuilderByBuildType(int buildType) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,buildType,30,40,10,50,44147,"AB",geometry, bbtc);
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
        
        Household oldSimpson = new Household(8,69,49875);
        assertTrue(oldSimpson.getWealth() == 49875);
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
    
    public void testGetIHC() throws ParseException {
        Parcel bigHouses = defaultParcelBuilderByBuildType(2);
        Parcel littleHouses = defaultParcelBuilderByBuildType(3);
        Parcel flats = defaultParcelBuilderByBuildType(4);
        Parcel moreFlats = defaultParcelBuilderByBuildType(5);
        
        Household youngPoor = new Household(1,20,51000,littleHouses);
        Household youngRich = new Household(2,20,140000,moreFlats);
        Household oldPoor = new Household(3,67,24000,flats);
        Household oldRich = new Household(4,68,148700,bigHouses);
        
        assertTrue(youngPoor.getMovingIHC() == 71);
        assertTrue(youngRich.getMovingIHC() == 77);
        assertTrue(oldPoor.getMovingIHC() == 65);
        assertTrue(oldRich.getMovingIHC() == 66);
    }
    
    
    public void testMoveInAndOut() throws ParseException {
        Household movingHousehold = new Household(3,41,58719);
        Parcel rez = defaultParcelBuilderByBuildType(3);
        
        movingHousehold.moveIn(rez);
        assertTrue(movingHousehold.getHousingPlot().equals(rez));
        
        movingHousehold.moveOut();
        assertTrue(movingHousehold.getHousingPlot() == null);
    }
}
