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
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

/**
 *
 * @author Thomas Salliou
 */
public class StatisticalDecisionMakerTest extends TestCase {
    
    public StatisticalDecisionMakerTest(String testName) {
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
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String globalsPathForTests = "src/test/resources/globals.gdms";
    private String outputPathForTests = "src/test/resources";
    private DataSourceFactory dsf = new DataSourceFactory(outputPathForTests + "/gdms");
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    private Parcel defaultParcelBuilderByInseeCode(int codeInsee) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,2,30,40,10,50,codeInsee,"AB",geometry, bbtc);
    }
    
    private Parcel defaultParcelBuilderByAmenitiesIndex(int amenitiesIndex) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,2,30,40,amenitiesIndex,50,44109,"AB",geometry, bbtc);
    }
    
    private Parcel defaultParcelBuilderByBuildType(int buildType) throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(0,buildType,30,40,10,50,44147,"AB",geometry, bbtc);
    }
    
    public void testGetWMC() throws ParseException {
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        
        Parcel nantesParcel = defaultParcelBuilderByInseeCode(44109);
        Household nantesHousehold = new Household(2,40,50000);
        nantesHousehold.moveIn(nantesParcel);
        assertTrue(sdm.getWillMoveCoefficient(nantesHousehold) == 30);
        
        Parcel notNantesParcel = defaultParcelBuilderByInseeCode(44789);
        Household notNantesHousehold = new Household(3,65,50000);
        notNantesHousehold.moveIn(notNantesParcel);
        assertTrue(sdm.getWillMoveCoefficient(notNantesHousehold) == 8);
    }
    
    public void testGetImmDiss() throws ParseException {
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        Parcel nantesParcel = defaultParcelBuilderByAmenitiesIndex(17);
        Household nantesHousehold = new Household(2,40,50000);
        nantesHousehold.moveIn(nantesParcel);
        assertTrue(Math.abs(sdm.getImmediateDissatisfaction(nantesHousehold) - 1.315) < 0.000001);
    }
    
    public void testAddDissQueue() throws DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        sdm.setManager(m);
        Household annoyedHousehold = new Household(2,47,54789);
        sdm.addHousehold(annoyedHousehold);
        sdm.addToDissatisfactionQueue(annoyedHousehold,1.5478);
        sdm.addToDissatisfactionQueue(annoyedHousehold,2.14752);
        sdm.addToDissatisfactionQueue(annoyedHousehold,0.4971);
        sdm.addToDissatisfactionQueue(annoyedHousehold,4.12765);
        Iterator<Double> i = sdm.getDissatisfactionMemory(annoyedHousehold).iterator();
        
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
    
    public void testGetCumulDiss() throws DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        sdm.setManager(m);
        Household annoyedHousehold = new Household(2,47,54789);
        sdm.addHousehold(annoyedHousehold);
        sdm.addToDissatisfactionQueue(annoyedHousehold,1.5478);
        sdm.addToDissatisfactionQueue(annoyedHousehold,2.14752);
        sdm.addToDissatisfactionQueue(annoyedHousehold,0.4971);
        sdm.addToDissatisfactionQueue(annoyedHousehold,4.12765);
        
        assertTrue(Math.abs(sdm.getCumulatedDissatisfaction(annoyedHousehold) - 6.77227) < 0.000001);
    }
    
    public void testGetIdealHousingCoefficient() throws ParseException {
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        Parcel bigHouses = defaultParcelBuilderByBuildType(2);
        Parcel littleHouses = defaultParcelBuilderByBuildType(3);
        Parcel flats = defaultParcelBuilderByBuildType(4);
        Parcel moreFlats = defaultParcelBuilderByBuildType(5);
        
        Household youngPoor = new Household(1,20,51000,littleHouses);
        Household youngRich = new Household(2,20,140000,moreFlats);
        Household oldPoor = new Household(3,67,24000,flats);
        Household oldRich = new Household(4,68,148700,bigHouses);
        
        assertTrue(sdm.getIdealHousingCoefficient(youngPoor) == 71);
        assertTrue(sdm.getIdealHousingCoefficient(youngRich) == 77);
        assertTrue(sdm.getIdealHousingCoefficient(oldPoor) == 65);
        assertTrue(sdm.getIdealHousingCoefficient(oldRich) == 66);
    }
    
    public void testDeleteHousehold() throws DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        sdm.setManager(m);
        Household futurelyDisappearingHousehold = new Household(4,57,65000);
        sdm.addHousehold(futurelyDisappearingHousehold);
        assertTrue(sdm.getDissatisfactionMemories().containsKey(futurelyDisappearingHousehold));
        sdm.deleteHousehold(futurelyDisappearingHousehold);
        assertTrue(sdm.getDissatisfactionMemories().isEmpty());
    }
    
    public void testIsMoving() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
        sdm.setManager(m);
        Parcel householdHub = defaultParcelBuilderByAmenitiesIndex(14);
        Household iWannaMove = new Household(5,27,87000, householdHub);
        Household iWannaStay = new Household(6,59,48701, householdHub);
        sdm.addHousehold(iWannaStay);
        sdm.addHousehold(iWannaMove);
        sdm.addToDissatisfactionQueue(iWannaMove, 29.0);
        assertTrue(sdm.isMoving(iWannaMove));
        assertFalse(sdm.isMoving(iWannaStay));
    }
}
