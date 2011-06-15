/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.ArrayList;
import java.util.Iterator;
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
    
    private Parcel defaultParcelBuilderByInseeCode(int codeInsee) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,2,30,40,10,50,codeInsee,"AB",geometry, bbtc);
    }
    
    private Parcel defaultParcelBuilderByBuildType(int buildType) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,buildType,30,40,10,50,44147,"AB",geometry, bbtc);
    }
    
    private Parcel defaultParcelBuilderByAmenitiesIndex(int amenitiesIndex) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,2,30,40,amenitiesIndex,50,44109,"AB",geometry, bbtc);
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
    
    /*
     * Tests the getWillMoveCoefficient method, in two cases :
     * Inside and outside Nantes (codeInsee == 44109)
     */
    public void testGetWMC() throws ParseException {
        Parcel nantesParcel = defaultParcelBuilderByInseeCode(44109);
        Household nantesHousehold = new Household(2,40,50000,nantesParcel);
        assertTrue(nantesHousehold.getWillMoveCoefficient() == 30);
        
        Parcel notNantesParcel = defaultParcelBuilderByInseeCode(44789);
        Household notNantesHousehold = new Household(3,65,50000,notNantesParcel);
        assertTrue(notNantesHousehold.getWillMoveCoefficient() == 8);
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
        
        assertTrue(youngPoor.getIdealHousingCoefficient() == 71);
        assertTrue(youngRich.getIdealHousingCoefficient() == 77);
        assertTrue(oldPoor.getIdealHousingCoefficient() == 65);
        assertTrue(oldRich.getIdealHousingCoefficient() == 66);
    }
    
    public void testGetImmDiss() throws ParseException {
        Parcel nantesParcel = defaultParcelBuilderByAmenitiesIndex(17);
        Household nantesHousehold = new Household(2,40,50000,nantesParcel);
        assertTrue(Math.abs(nantesHousehold.getImmediateDissatisfaction() - 1.315) < 0.000001);
    }
    
    //Won't pass if the HOUSEHOLD_MEMORY exceeds 3
    public void testAddDissQueue() {
        Household annoyedHousehold = new Household(2,47,54789);
        annoyedHousehold.addToDissatisfactionQueue(1.5478);
        annoyedHousehold.addToDissatisfactionQueue(2.14752);
        annoyedHousehold.addToDissatisfactionQueue(0.4971);
        annoyedHousehold.addToDissatisfactionQueue(4.12765);
        Iterator<Double> i = annoyedHousehold.getDissatisfactionMemory().iterator();
        
        ArrayList<Double> control = new ArrayList();
        control.add(2.14752);
        control.add(0.4971);
        control.add(4.12765);
        Iterator<Double> j = control.iterator();
        
        while(j.hasNext()) {
            assertTrue(Math.abs(i.next() - j.next()) < 0.000001);
        }
        assertFalse(i.hasNext());
    }
    
    public void testGetCumulDiss() {
        Household annoyedHousehold = new Household(2,47,54789);
        annoyedHousehold.addToDissatisfactionQueue(1.5478);
        annoyedHousehold.addToDissatisfactionQueue(2.14752);
        annoyedHousehold.addToDissatisfactionQueue(0.4971);
        annoyedHousehold.addToDissatisfactionQueue(4.12765);
        
        assertTrue(Math.abs(annoyedHousehold.getCumulatedDissatisfaction() - 6.77227) < 0.000001);
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
