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
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.DriverException;

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
        new File(outputPathForTests+"Household.gdms").delete();
        new File(outputPathForTests+"HouseholdState.gdms").delete();
        new File(outputPathForTests+"Plot.gdms").delete();
        new File(outputPathForTests+"PlotState.gdms").delete();
        new File(outputPathForTests+"Step.gdms").delete();
    }

    private Parcel defaultParcelBuilder() throws ParseException{
        WKTReader wktr = new WKTReader();
        Geometry geometry = wktr.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),((15 5, 40 10, 10 20, 5 10, 15 5)))");
        return new Parcel(7,2,30,40,10,50,44109,"AB",geometry, bbtc);
    }
    
    private Household defaultHouseholdBuilder() {
        return new Household(1,25,48700);
    }
    
    private double getMemoryUsage(Runtime r) {
        return (r.totalMemory() - r.freeMemory())/(1024.0*1024.0);
    }
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String outputPathForTests = "src/test/resources/";
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    public void testGetPopulation() throws ParseException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
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
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        Parcel a = defaultParcelBuilder();
        Household iWantToDie = defaultHouseholdBuilder();
        iWantToDie.moveIn(a);
        assertTrue(a.getHouseholdList().contains(iWantToDie));
        m.kill(iWantToDie);
        assertFalse(a.getHouseholdList().contains(iWantToDie));
    }
    
    public void testCreateImmigrant() throws ParseException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        m.createImmigrant();
        assertFalse(m.getHomelessList().empty());
        assertTrue(m.getHomelessList().peek().getAge() > 19 && m.getHomelessList().peek().getAge() < 60);
        assertTrue(m.getHomelessList().peek().getMaxWealth() > 24999 && m.getHomelessList().peek().getMaxWealth() < 100001);
    }
    
    public void testCreateNewborn() throws ParseException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        Household hornyHousehold = new Household(1,60,58741);
        m.createNewborn(hornyHousehold);
        assertFalse(m.getHomelessList().empty());
        assertTrue(m.getHomelessList().peek().getAge() == 20);
        assertTrue(m.getHomelessList().peek().getMaxWealth() == 58741);
    }
    
    public void testInitializeForParcels() throws DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        m.initializeSimulation();
        
        assertTrue(m.getParcelList().size() == 6978);
        assertTrue(m.getParcelList().get(3).getId() == 3);
        assertTrue(m.getParcelList().get(3).getBuildType() == 2);
        System.out.println(m.getParcelList().get(3).getInverseArea());
        System.out.println(m.getParcelList().get(3).getDensity());
        assertTrue(Math.abs(m.getParcelList().get(3).getDensity()- 0.000177042857497) < 0.000000001);
        assertTrue(Math.abs(m.getParcelList().get(3).getMaxDensity()- 0.000155) < 0.000000001);
        assertTrue(m.getParcelList().get(3).getAmenitiesIndex() == 13);
        assertTrue(m.getParcelList().get(3).getConstructibilityIndex() == 16);
        assertTrue(m.getParcelList().get(3).getInseeCode() == 44171);
        assertTrue("A".equals(m.getParcelList().get(3).getZoning()));
    }
    
    public void testInitializeForHouseholds() throws DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        m.initializeSimulation();
        
        assertTrue(m.getParcelList().get(3).getHouseholdList().size() == 22);
        assertTrue(m.getParcelList().contains(m.getParcelList().get(3).getHouseholdList().iterator().next().getHousingPlot()));
        assertTrue(m.getParcelList().get(3).getHouseholdList().iterator().next().getMaxWealth() > 34132);
        assertTrue(m.getParcelList().get(3).getHouseholdList().iterator().next().getMaxWealth() < 41717);
        assertTrue(m.getPopulation() == 262650);
        
        assertTrue(m.getParcelList().get(6977).getHouseholdList().isEmpty());
        
        assertTrue(m.getParcelList().get(17).getHouseholdList().size() == 34);
        assertTrue(m.getParcelList().contains(m.getParcelList().get(17).getHouseholdList().iterator().next().getHousingPlot()));
        assertTrue(m.getParcelList().get(17).getHouseholdList().iterator().next().getMaxWealth() > 31718);
        assertTrue(m.getParcelList().get(17).getHouseholdList().iterator().next().getMaxWealth() < 38767);
    }
    
    public void testInitializeOutputDatabase() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        m.initializeSimulation();
        m.initializeOutputDatabase();
        
        DataSourceFactory dsf = m.getDsf();
        DataSource householdDS = dsf.getDataSource("Household");
        DataSource plotDS = dsf.getDataSource("Plot");
        DataSource householdStateDS = dsf.getDataSource("HouseholdState");
        DataSource plotStateDS = dsf.getDataSource("PlotState");
        DataSource stepDS = dsf.getDataSource("Step");
        SpatialDataSourceDecorator plotSDS = new SpatialDataSourceDecorator(plotDS);
        
        householdDS.open();
        plotSDS.open();
        householdStateDS.open();
        plotStateDS.open();
        stepDS.open();
        
        //Let's see if our files are created for real.
        assertTrue(new File(outputPathForTests+"Household.gdms").exists());
        assertTrue(new File(outputPathForTests+"HouseholdState.gdms").exists());
        assertTrue(new File(outputPathForTests+"Plot.gdms").exists());
        assertTrue(new File(outputPathForTests+"PlotState.gdms").exists());
        assertTrue(new File(outputPathForTests+"Step.gdms").exists());
        
        //Now let's see if our tables are filled with correct metadata.
        assertTrue(householdDS.getMetadata().getFieldName(1).equals("maximumWealth"));
        assertTrue(householdStateDS.getMetadata().getFieldName(3).equals("age"));
        assertTrue(plotDS.getMetadata().getFieldName(1).equals("the_geom"));
        assertTrue(plotDS.getMetadata().getFieldType(1).getTypeCode() == 4096);
        assertTrue(plotStateDS.getMetadata().getFieldName(2).equals("buildType"));
        assertTrue(stepDS.getMetadata().getFieldName(0).equals("stepNumber"));
        
        //Now let's test the content itself.
        assertTrue(householdDS.getRowCount() == 262650);
        assertTrue(plotSDS.getRowCount() == 6978);
        
        //Did you build my spatial index, dear ?
        assertTrue(m.getDsf().getIndexManager().isIndexed("Plot", "the_geom"));
        
        //And don't forget to close your datasources, folks.
        householdDS.close();
        plotSDS.close();
        householdStateDS.close();
        plotStateDS.close();
        stepDS.close();
    }
    
    public void testMemoryConsumption() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Runtime r = Runtime.getRuntime();
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
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
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        StatisticalManagerListener ml = new StatisticalManagerListener(sdm);
        m.registerManagerListener(ml);
        assertTrue(m.getListeners().contains(ml));
    }
    
    public void testUnregisterManagerListener() {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = new Manager(s, dataPathForTests,outputPathForTests, bbtc, sdm, gps);
        StatisticalManagerListener ml = new StatisticalManagerListener(sdm);
        m.registerManagerListener(ml);
        assertTrue(m.getListeners().contains(ml));
        m.unregisterManagerListener(ml);
        assertTrue(m.getListeners().isEmpty());
    }
    
    public void testSaveState() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Step s = new Step(2000, dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        Manager m = s.getManager();
        m.initializeSimulation();
        m.initializeOutputDatabase();
        m.createNewborn(new Household(m.getPopulation(), 60, 47478));
        m.saveState();
        
        DataSourceFactory dsf = new DataSourceFactory();
        DataSource householdDS = dsf.getDataSource(new File(outputPathForTests+"Household.gdms"));
        householdDS.open();
        assertTrue(householdDS.getFieldValue(m.getPopulation(), 0).getAsInt() == m.getPopulation());
        assertTrue(householdDS.getFieldValue(m.getPopulation(), 1).getAsInt() == 47478);
        householdDS.close();
        
        DataSource plotStateDS = dsf.getDataSource(new File(outputPathForTests+"PlotState.gdms"));
        plotStateDS.open();
        assertTrue(plotStateDS.getFieldValue(4157, 0).getAsInt() == 4157);
        assertTrue(plotStateDS.getFieldValue(4157, 1).getAsInt() == 0);
        assertTrue(plotStateDS.getFieldValue(4157, 2).getAsInt() == 1);
        assertTrue(plotStateDS.getFieldValue(4157, 3).getAsInt() == 0);
        plotStateDS.close();
        
        DataSource householdStateDS = dsf.getDataSource(new File(outputPathForTests+"HouseholdState.gdms"));
        householdStateDS.open();
        assertTrue(householdStateDS.getFieldValue(84125, 1).getAsInt() == 0);
        assertTrue(householdStateDS.getFieldValue(84125, 2).getAsInt() == 2518);
        assertTrue(householdStateDS.getFieldValue(84125, 3).getAsInt() < 81);
        assertTrue(householdStateDS.getFieldValue(84125, 3).getAsInt() > 0);
        assertTrue(householdStateDS.getFieldValue(84125, 4).getAsBoolean());
        householdStateDS.close();
        
        DataSource stepDS = dsf.getDataSource(new File(outputPathForTests+"Step.gdms"));
        stepDS.open();
        assertTrue(stepDS.getFieldValue(0,0).getAsInt() == 0);
        assertTrue(stepDS.getFieldValue(0,1).getAsInt() == 2000);
        assertTrue(stepDS.getFieldValue(0,2).getAsInt() == 262650);
        stepDS.close();
    }    
}
