/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.io.File;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
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
    
    private String dataPathForTests = "src/test/resources/Basedonnesreduiterefaite4.shp";
    private String outputPathForTests = "src/test/resources/";
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    
    public void testGetPopulation() throws ParseException {
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
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
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
        Parcel a = defaultParcelBuilder();
        Household iWantToDie = defaultHouseholdBuilder();
        iWantToDie.moveIn(a);
        assertTrue(a.getHouseholdList().contains(iWantToDie));
        m.kill(iWantToDie);
        assertFalse(a.getHouseholdList().contains(iWantToDie));
    }
    
    public void testCreateImmigrant() throws ParseException {
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
        m.createImmigrant();
        assertFalse(m.getHomelessList().empty());
        assertTrue(m.getHomelessList().peek().getAge() > 19 && m.getHomelessList().peek().getAge() < 60);
        assertTrue(m.getHomelessList().peek().getMaxWealth() > 24999 && m.getHomelessList().peek().getMaxWealth() < 100001);
    }
    
    public void testCreateNewborn() throws ParseException {
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
        Household hornyHousehold = new Household(1,60,58741);
        m.createNewborn(hornyHousehold);
        assertFalse(m.getHomelessList().empty());
        assertTrue(m.getHomelessList().peek().getAge() == 20);
        assertTrue(m.getHomelessList().peek().getMaxWealth() == 58741);
    }
    
    public void testInitializeForParcels() throws DataSourceCreationException, DriverException {
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
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
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
        m.initializeSimulation();
        
        assertTrue(m.getParcelList().get(3).getHouseholdList().size() == 22);
        assertTrue(m.getParcelList().contains(m.getParcelList().get(3).getHouseholdList().iterator().next().getHousingPlot()));
        assertTrue(m.getParcelList().get(3).getHouseholdList().iterator().next().getMaxWealth() > 34132);
        assertTrue(m.getParcelList().get(3).getHouseholdList().iterator().next().getMaxWealth() < 41717);
        assertTrue(m.getPopulation() == 263461);
        
        assertTrue(m.getParcelList().get(6977).getHouseholdList().isEmpty());
        
        assertTrue(m.getParcelList().get(17).getHouseholdList().size() == 34);
        assertTrue(m.getParcelList().contains(m.getParcelList().get(17).getHouseholdList().iterator().next().getHousingPlot()));
        assertTrue(m.getParcelList().get(17).getHouseholdList().iterator().next().getMaxWealth() > 31718);
        assertTrue(m.getParcelList().get(17).getHouseholdList().iterator().next().getMaxWealth() < 38767);
    }
    
    public void testCreateOutputDatabase() throws NoSuchTableException, DataSourceCreationException, DriverException {
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
        m.createOutputDatabase();
        DataSourceFactory dsf = m.getDsf();
        DataSource hh = dsf.getDataSource("Household");
        DataSource hs = dsf.getDataSource("HouseholdState");
        DataSource pl = dsf.getDataSource("Plot");
        DataSource ps = dsf.getDataSource("PlotState");
        DataSource st = dsf.getDataSource("Step");
        
        //Let's see if our files are created for real.
        assertTrue(new File(outputPathForTests+"Household.gdms").exists());
        assertTrue(new File(outputPathForTests+"HouseholdState.gdms").exists());
        assertTrue(new File(outputPathForTests+"Plot.gdms").exists());
        assertTrue(new File(outputPathForTests+"PlotState.gdms").exists());
        assertTrue(new File(outputPathForTests+"Step.gdms").exists());
        
        //Now let's see if our tables are filled with correct metadata.
        hh.open();
        assertTrue(hh.getMetadata().getFieldName(1).equals("maximumWealth"));
        hh.close();
        
        hs.open();
        assertTrue(hs.getMetadata().getFieldName(3).equals("age"));
        hs.close();
        
        pl.open();
        assertTrue(pl.getMetadata().getFieldName(1).equals("the_geom"));
        assertTrue(pl.getMetadata().getFieldType(1).getTypeCode() == 4096);
        pl.close();
        
        ps.open();
        assertTrue(ps.getMetadata().getFieldName(2).equals("buildType"));
        ps.close();
        
        st.open();
        assertTrue(st.getMetadata().getFieldName(0).equals("stepNumber"));
        st.close();
    }
    
    public void testInitializeOutputDatabase() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException {
        Manager m = new Manager(dataPathForTests,outputPathForTests, bbtc);
        m.initializeSimulation();
        m.createOutputDatabase();
        m.initializeOutputDatabase();
        
        DataSourceFactory dsf = m.getDsf();
        DataSource householdDS = dsf.getDataSource("Household");
        DataSource plotDS = dsf.getDataSource("Plot");
        SpatialDataSourceDecorator plotSDS = new SpatialDataSourceDecorator(plotDS);
        
        householdDS.open();
        plotSDS.open();
        assertTrue(householdDS.getRowCount() == 263461);
        assertTrue(plotSDS.getRowCount() == 6978);
        householdDS.close();
        plotSDS.close();
    }
}
