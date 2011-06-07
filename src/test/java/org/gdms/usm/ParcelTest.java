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
public class ParcelTest extends TestCase {
    
    public ParcelTest(String testName) {
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

    /**
     * Builds a Parcel with given density and maxDensity, and default values for the rest, for test purposes.
     * @param density
     * @param maxDensity
     * @return
     * @throws ParseException 
     */
    private Parcel defaultParcelBuilder(double density, double maxDensity) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(7,2,density,maxDensity,10,50,44109,"AB",geometry);
    }
    
    /**
     * Tests if the isFull method is doing its job for both cases.
     * @throws ParseException 
     */
    public void testIsFull() throws ParseException{
        Parcel rez = defaultParcelBuilder(30,40);
        assertTrue(!rez.isFull());
        
        Parcel rez2 = defaultParcelBuilder(30,30.0016);
        assertTrue(rez2.isFull());
    }
    
    public void testMoveInAndOut() throws ParseException {
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("POLYGON((0 0, 0 10, 10 10, 10 0, 0 0))");
        
        Parcel rez = new Parcel(8,1,2,20,10,50,44109,"AB",geometry);
        Household movingInHousehold = new Household(9,40,50000);
        
        rez.addHousehold(movingInHousehold);
        assertTrue(Math.abs(rez.getDensity()-2.01) < 0.000001);
        assertTrue(rez.getHouseholdList().contains(movingInHousehold));
        assertTrue(rez.getHouseholdList().size() == 1);
        
        rez.removeHousehold(movingInHousehold);
        assertTrue(rez.getHouseholdList().isEmpty());
        assertTrue(Math.abs(rez.getDensity()-2.00) < 0.000001);
    }
    
    public void testGetLocalPopulation() throws ParseException {
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("POLYGON((0 0, 0 10, 10 10, 10 0, 0 0))");
        
        Parcel rez = new Parcel(8,1,2,20,10,50,44109,"AB",geometry);
        Household dupont = new Household(9,40,50000);
        Household dupond = new Household(10,47,48000);
        Household dhupondt = new Household(11,24,78000);
        
        rez.addHousehold(dupont);
        rez.addHousehold(dupond);
        rez.addHousehold(dhupondt);
        
        assertTrue(rez.getLocalPopulation() == 3);
    }
    
    public void testGetAverageWealth() throws ParseException {
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("POLYGON((0 0, 0 10, 10 10, 10 0, 0 0))");
        
        Parcel parisSeizieme = new Parcel(8,1,2,20,10,50,44109,"AB",geometry);
        Household firstHousehold = new Household(1,24,48752); //19500
        Household secondHousehold = new Household(2,35,143258); //83567
        Household thirdHousehold = new Household(3,68,26587); //26587
        Household fourthHousehold = new Household(4,47,49852); //39050
        Household fifthHousehold = new Household(5,25,69703); //29042
        Household sixthHousehold = new Household(6,64,87012); //87012
        
        parisSeizieme.addHousehold(firstHousehold);
        parisSeizieme.addHousehold(secondHousehold);
        parisSeizieme.addHousehold(thirdHousehold);
        parisSeizieme.addHousehold(fourthHousehold);
        parisSeizieme.addHousehold(fifthHousehold);
        parisSeizieme.addHousehold(sixthHousehold);
        
        //Wealth is an integer, averageWealth too, be careful !
        assertTrue(parisSeizieme.getAverageWealth() == 47459);
    }
      
}
