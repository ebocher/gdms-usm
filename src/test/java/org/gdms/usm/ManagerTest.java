/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.gdms.GdmsWriter;

/**
 *
 * @author Thomas Salliou
 */
public class ManagerTest extends TestCase {
    
    public ManagerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        new File(outputPathForTests+"/Household.gdms").delete();
        new File(outputPathForTests+"/HouseholdState.gdms").delete();
        new File(outputPathForTests+"/Plot.gdms").delete();
        new File(outputPathForTests+"/PlotState.gdms").delete();
        new File(outputPathForTests+"/Step.gdms").delete();
    }

    private Step instanciateDummyParcels() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        WKTReader wktr = new WKTReader();
        Geometry g1 = wktr.read("POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))");
        Parcel p1 = new Parcel(1,1,100,5,0,0,0,80,44578,"AUY",g1,bbtc);
        m.addParcel(p1);
        Geometry g2 = wktr.read("POLYGON ((4 0, 10 0, 10 4, 4 4, 4 0))");
        Parcel p2 = new Parcel(3,3,300,14,0,0,0,70,44109,"OPH",g2,bbtc);
        m.addParcel(p2);
        Geometry g3 = wktr.read("POLYGON ((0 4, 4 4, 4 7, 0 7, 0 4))");
        Parcel p3 = new Parcel(4,4,400,15,0,0,0,90,44710,"AHA",g3,bbtc);
        m.addParcel(p3);
        Geometry g4 = wktr.read("POLYGON ((4 4, 10 4, 10 7, 4 7, 4 4))");
        Parcel p4 = new Parcel(2,2,200,12,0,0,0,80,44109,"PLU",g4,bbtc);
        m.addParcel(p4);
        Geometry g5 = wktr.read("POLYGON ((10 0, 13 0, 13 7, 10 7, 10 0))");
        Parcel p5 = new Parcel(5,5,500,18,0,0,0,100,44109,"POT",g5,bbtc);
        m.addParcel(p5);
        Geometry g7 = wktr.read("POLYGON ((13 0, 16 0, 16 7, 13 7, 13 0))");
        Parcel p7 = new Parcel(7,7,0,10,0,0,0,47,44780,"PCT",g7,bbtc);
        m.addParcel(p7);
        
        File file1 = new File(outputPathForTests + "/MiniPlot.gdms");
        GdmsWriter plotGW = new GdmsWriter(file1);
        String[] fieldNames1 = {"plotID", "the_geom", "densityOfPopulationMax", "amenitiesIndex", "constructibilityIndex"};
        Type[] fieldTypes1 = {TypeFactory.createType(Type.INT), TypeFactory.createType(Type.GEOMETRY), TypeFactory.createType(Type.DOUBLE), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT)};
        Metadata m1 = new DefaultMetadata(fieldTypes1, fieldNames1);
        plotGW.writeMetadata(0, m1);
        plotGW.writeRowIndexes();
        plotGW.writeExtent();
        plotGW.writeWritenRowCount();
        plotGW.close();
        m.getDsf().getSourceManager().register("Plot", file1);
        
        return s;
    }
    
    private Parcel defaultParcelBuilder() throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(7,2,30,40,40,40,10,50,44109,"AB",geometry, bbtc);
    }
    
    private Household defaultHouseholdBuilder() {
        return new Household(1,25,48700);
    }
    
    private double getMemoryUsage(Runtime r) {
        return (r.totalMemory() - r.freeMemory())/(1024.0*1024.0);
    }
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String globalsPathForTests = "src/test/resources/globals.gdms";
    private String outputPathForTests = "src/test/resources";
    private DataSourceFactory dsf = new DataSourceFactory(outputPathForTests + "/gdms");
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    public void testGetPopulation() throws ParseException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Parcel a = defaultParcelBuilder();
        Parcel b = defaultParcelBuilder();
        Parcel c = defaultParcelBuilder();
        m.addParcel(a);
        m.addParcel(b);
        m.addParcel(c);
        Household h1 = defaultHouseholdBuilder();
        Household h2 = defaultHouseholdBuilder();
        Household h3 = defaultHouseholdBuilder();
        Household h4 = defaultHouseholdBuilder();
        Household h5 = defaultHouseholdBuilder();
        Household h6 = defaultHouseholdBuilder();
        Household h7 = defaultHouseholdBuilder();
        Household h8 = defaultHouseholdBuilder();
        Household h9 = defaultHouseholdBuilder();
        Household h10 = defaultHouseholdBuilder();
        Household h11 = defaultHouseholdBuilder();
        a.addHousehold(h1);a.addHousehold(h6);a.addHousehold(h10);
        b.addHousehold(h2);b.addHousehold(h8);
        c.addHousehold(h11);c.addHousehold(h3);c.addHousehold(h4);c.addHousehold(h5);c.addHousehold(h7);c.addHousehold(h9);
        assertTrue(m.getPopulation() == 11);
    }
    
    public void testKill() throws ParseException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Parcel a = defaultParcelBuilder();
        Household iWantToDie = defaultHouseholdBuilder();
        iWantToDie.moveIn(a);
        assertTrue(a.getHouseholdList().contains(iWantToDie));
        m.kill(iWantToDie);
        assertFalse(a.getHouseholdList().contains(iWantToDie));
    }
    
    public void testCreateImmigrant() throws ParseException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        m.createImmigrant();
        assertFalse(m.getHomelessList().empty());
        assertTrue(m.getHomelessList().peek().getAge() > 19 && m.getHomelessList().peek().getAge() < 60);
        assertTrue(m.getHomelessList().peek().getMaxWealth() > 9999 && m.getHomelessList().peek().getMaxWealth() < 60001);
    }
    
    public void testCreateNewborn() throws ParseException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Household hornyHousehold = new Household(1,60,58741);
        m.createNewborn(hornyHousehold);
        assertFalse(m.getHomelessList().empty());
        assertTrue(m.getHomelessList().peek().getAge() == 20);
        assertTrue(m.getHomelessList().peek().getMaxWealth() == 58741);
    }
    
    public void testInitializeForParcels() throws DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        m.initializeSimulation();
        
        assertTrue(m.getParcelList().size() == 6978);
        assertTrue(m.getParcelList().get(3).getId() == 3);
        assertTrue(m.getParcelList().get(3).getBuildType() == 2);
        System.out.println(m.getParcelList().get(3).getInverseArea());
        System.out.println(m.getParcelList().get(3).getDensity());
        assertTrue(Math.abs(m.getParcelList().get(3).getDensity()- 0.000128758441459) < 0.000000001);
        assertTrue(Math.abs(m.getParcelList().get(3).getMaxDensity()- 0.000155) < 0.000000001);
        assertTrue(m.getParcelList().get(3).getAmenitiesIndex1() == 13);
        assertTrue(m.getParcelList().get(3).getAmenitiesIndex2() == 13);
        assertTrue(m.getParcelList().get(3).getAmenitiesIndex3() == 13);
        assertTrue(m.getParcelList().get(3).getConstructibilityIndex() == 16);
        assertTrue(m.getParcelList().get(3).getInseeCode() == 44171);
        assertTrue("A".equals(m.getParcelList().get(3).getZoning()));
    }
    
    public void testInitializeForHouseholds() throws DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        m.initializeSimulation();
        
        assertTrue(m.getParcelList().get(3).getHouseholdList().size() == 16);
        assertTrue(m.getParcelList().contains(m.getParcelList().get(3).getHouseholdList().iterator().next().getHousingPlot()));
        assertTrue(m.getParcelList().get(3).getHouseholdList().iterator().next().getMaxWealth() > 34132);
        assertTrue(m.getParcelList().get(3).getHouseholdList().iterator().next().getMaxWealth() < 41717);
        assertTrue(m.getPopulation() == 193214);
        
        assertTrue(m.getParcelList().get(6977).getHouseholdList().isEmpty());
        
        assertTrue(m.getParcelList().get(17).getHouseholdList().size() == 23);
        assertTrue(m.getParcelList().contains(m.getParcelList().get(17).getHouseholdList().iterator().next().getHousingPlot()));
        assertTrue(m.getParcelList().get(17).getHouseholdList().iterator().next().getMaxWealth() > 31718);
        assertTrue(m.getParcelList().get(17).getHouseholdList().iterator().next().getMaxWealth() < 38767);
    }
    
    public void testInitializeOutputDatabase() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        m.initializeSimulation();
        m.initializeOutputDatabase();
        
        DataSourceFactory dsf = m.getDsf();
        DataSource householdDS = dsf.getDataSource("Household");
        DataSource plotDS = dsf.getDataSource("Plot");
        DataSource householdStateDS = dsf.getDataSource("HouseholdState");
        DataSource plotStateDS = dsf.getDataSource("PlotState");
        DataSource stepDS = dsf.getDataSource("Step");
        
        householdDS.open();
        plotDS.open();
        householdStateDS.open();
        plotStateDS.open();
        stepDS.open();
        
        //Let's see if our files are created for real.
        assertTrue(new File(outputPathForTests+"/Household.gdms").exists());
        assertTrue(new File(outputPathForTests+"/HouseholdState.gdms").exists());
        assertTrue(new File(outputPathForTests+"/Plot.gdms").exists());
        assertTrue(new File(outputPathForTests+"/PlotState.gdms").exists());
        assertTrue(new File(outputPathForTests+"/Step.gdms").exists());
        
        //Now let's see if our tables are filled with correct metadata.
        assertTrue(householdDS.getMetadata().getFieldName(1).equals("maximumWealth"));
        assertTrue(householdStateDS.getMetadata().getFieldName(3).equals("age"));
        assertTrue(plotDS.getMetadata().getFieldName(1).equals("the_geom"));
        assertTrue(plotDS.getMetadata().getFieldType(1).getTypeCode() == 4096);
        assertTrue(plotStateDS.getMetadata().getFieldName(2).equals("buildType"));
        assertTrue(stepDS.getMetadata().getFieldName(0).equals("stepNumber"));
        
        //Now let's test the content itself.
        assertTrue(householdDS.getRowCount() == 193214);
        assertTrue(plotDS.getRowCount() == 6978);
        
        //Did you build my spatial index, dear ?
        assertTrue(m.getDsf().getIndexManager().isIndexed("Plot", "the_geom"));
        
        //And don't forget to close your datasources, folks.
        householdDS.close();
        plotDS.close();
        householdStateDS.close();
        plotStateDS.close();
        stepDS.close();
    }
    
    public void testMemoryConsumption() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Runtime r = Runtime.getRuntime();
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        System.out.println("Initial memory consumption : "+getMemoryUsage(r));
        m.initializeSimulation();
        r.gc();
        System.out.println("Memory consumption after initialization : "+getMemoryUsage(r));
        m.initializeOutputDatabase();
        System.out.println("Memory consumption after database initialization (without gc): "+getMemoryUsage(r));
        r.gc();
        System.out.println("Memory consumption after database initialization (with gc): "+getMemoryUsage(r));
    }
    
    public void testRegisterManagerListener() {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        StatisticalManagerListener ml = new StatisticalManagerListener(sdm);
        m.registerManagerListener(ml);
        assertTrue(m.getListeners().contains(ml));
    }
    
    public void testUnregisterManagerListener() {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        StatisticalManagerListener ml = new StatisticalManagerListener(sdm);
        m.registerManagerListener(ml);
        assertTrue(m.getListeners().contains(ml));
        m.unregisterManagerListener(ml);
        assertTrue(m.getListeners().isEmpty());
    }
    
    public void testSaveState() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeSimulation();
        m.initializeOutputDatabase();
        m.createNewborn(new Household(m.getPopulation(), 60, 47478));
        m.saveState();
        
        DataSourceFactory dsf = new DataSourceFactory();
        DataSource householdDS = dsf.getDataSource(new File(outputPathForTests+"/Household.gdms"));
        householdDS.open();
        assertTrue(householdDS.getFieldValue(m.getPopulation(), 0).getAsInt() == m.getPopulation());
        assertTrue(householdDS.getFieldValue(m.getPopulation(), 1).getAsInt() == 47478);
        householdDS.close();
        
        DataSource plotStateDS = dsf.getDataSource(new File(outputPathForTests+"/PlotState.gdms"));
        plotStateDS.open();
        assertTrue(plotStateDS.getFieldValue(4157, 0).getAsInt() == 4157);
        assertTrue(plotStateDS.getFieldValue(4157, 1).getAsInt() == 0);
        assertTrue(plotStateDS.getFieldValue(4157, 2).getAsInt() == 1);
        assertTrue(plotStateDS.getFieldValue(4157, 3).getAsInt() == 0);
        plotStateDS.close();
        
        DataSource householdStateDS = dsf.getDataSource(new File(outputPathForTests+"/HouseholdState.gdms"));
        householdStateDS.open();
        assertTrue(householdStateDS.getFieldValue(84125, 1).getAsInt() == 0);
        assertTrue(householdStateDS.getFieldValue(84125, 2).getAsInt() == 3466);
        assertTrue(householdStateDS.getFieldValue(84125, 3).getAsInt() < 81);
        assertTrue(householdStateDS.getFieldValue(84125, 3).getAsInt() > 19);
        assertTrue(householdStateDS.getFieldValue(84125, 4).getAsBoolean());
        householdStateDS.close();
        
        DataSource stepDS = dsf.getDataSource(new File(outputPathForTests+"/Step.gdms"));
        stepDS.open();
        assertTrue(stepDS.getFieldValue(0,0).getAsInt() == 0);
        assertTrue(stepDS.getFieldValue(0,1).getAsInt() == 2000);
        assertTrue(stepDS.getFieldValue(0,2).getAsInt() == 193214);
        stepDS.close();
    }    
    
    public void testEverybodyGrows() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException {
        Step s = instanciateDummyParcels();
        Manager m = s.getManager();
        
        Household adult = new Household(1,40,48000);
        adult.moveIn(m.getParcelList().get(2));
        Household pregnant = new Household(2,59,48000);
        pregnant.moveIn(m.getParcelList().get(3));
        Household terminalPhaseCancer = new Household(3,80,48000);
        terminalPhaseCancer.moveIn(m.getParcelList().get(4));
        
        m.everybodyGrows();
        
        //Age incrementation, terminalPhaseCancer should not exist anymore
        //but there it has a reference here in the test, so...
        assertTrue(adult.getAge() == 41);
        assertTrue(pregnant.getAge() == 60);
        assertTrue(terminalPhaseCancer.getAge() == 81);
        
        //Hip hip hurray for the newborn !
        assertFalse(m.getNewbornList().empty());
        assertTrue(m.getNewbornList().peek().getAge() == 20);
        assertTrue(m.getNewbornList().peek().getMaxWealth() == 48000);
        
        //And now for something completely different... and gloomy. Die !
        assertTrue(m.getHomelessList().peek().getAge() == 20);
        assertTrue(m.getParcelList().get(4).getHouseholdList().isEmpty());
    }
    
    public void testWhoIsMoving() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        Parcel householdHub = defaultParcelBuilder();
        m.addParcel(householdHub);
        Household iWannaMove = new Household(5,27,87000);
        Household iWannaMove2 = new Household(12,47,98000);
        Household iWannaStay = new Household(6,59,48701);
        iWannaMove.moveIn(householdHub);
        iWannaMove2.moveIn(householdHub);
        iWannaStay.moveIn(householdHub);
        sdm.addHousehold(iWannaMove);
        sdm.addHousehold(iWannaMove2);
        sdm.addHousehold(iWannaStay);
        sdm.addToDissatisfactionQueue(iWannaMove, 29.9);
        sdm.addToDissatisfactionQueue(iWannaMove2, 29.9);
        
        m.whoIsMoving();
        int moversCount = m.getMoversCount();
        assertTrue(moversCount == 2);
        assertTrue(householdHub.getHouseholdList().size() == 1);
        assertTrue(m.getHomelessList().size() == 2);
    }
    
    public void testEverybodyMovesIn() throws ParseException, NoSuchTableException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        Parcel householdHub = defaultParcelBuilder();
        m.addParcel(householdHub);
        Household homeless1 = new Household(5,27,60000);
        Household homeless2 = new Household(12,47,40000);
        Household homeless3 = new Household(6,59,30000);
        Household homeless4 = new Household(4,47,40000);
        m.getHomelessList().add(homeless1);
        m.getHomelessList().add(homeless2);
        m.getHomelessList().add(homeless3);
        m.getHomelessList().add(homeless4);
        m.everybodyMovesIn();
        assertTrue(m.getHomelessList().empty());
        assertTrue(householdHub.getHouseholdList().size() == 4);
        assertTrue(householdHub.getHouseholdList().contains(homeless1));
        assertTrue(householdHub.getHouseholdList().contains(homeless2));
        assertTrue(householdHub.getHouseholdList().contains(homeless3));
        assertTrue(householdHub.getHouseholdList().contains(homeless4));
    }
    
    public void testInitializeGlobals() throws DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        
        assertTrue(Math.abs(m.getBufferSize() - 30.0) < 0.000001);
        assertTrue(Math.abs(m.getAmenitiesWeighting() - 1.0) < 0.000001);
        assertTrue(Math.abs(m.getConstructibilityWeighting() - 1.0) < 0.000001);
        assertTrue(Math.abs(m.getIdealhousingWeighting() - 1.0) < 0.000001);
        assertTrue(Math.abs(m.getGaussDeviation() - 0.1) < 0.000001);
        assertTrue(Math.abs(m.getSegregationThreshold() - 0.8) < 0.000001);
        assertTrue(Math.abs(m.getSegregationTolerance() - 0.3) < 0.000001);
        assertTrue(m.getHouseholdMemory() == 3);
        assertTrue(Math.abs(m.getMovingThreshold() - 30.0) < 0.000001);
        assertTrue(m.getImmigrantNumber() == 7000);
        assertTrue(m.getNumberOfTurns() == 1);
    }
}
