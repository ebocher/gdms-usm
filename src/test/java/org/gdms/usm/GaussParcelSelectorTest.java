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
import java.util.List;
import junit.framework.TestCase;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
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
public class GaussParcelSelectorTest extends TestCase {
    
    public GaussParcelSelectorTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        new File(outputPathForTests+"/MiniPlot.gdms").delete();
    }
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String globalsPathForTests = "src/test/resources/globals.gdms";
    private String outputPathForTests = "src/test/resources";
    private DataSourceFactory dsf = new DataSourceFactory(outputPathForTests + "/gdms");
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    private Step instanciateDummyParcels() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        WKTReader wktr = new WKTReader();
        Geometry g1 = wktr.read("POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))");
        Parcel p1 = new Parcel(1,1,100,5,5,5,80,44578,"AUY",g1,bbtc);
        m.addParcel(p1);
        Geometry g2 = wktr.read("POLYGON ((4 0, 10 0, 10 4, 4 4, 4 0))");
        Parcel p2 = new Parcel(3,3,300,14,14,14,70,44109,"OPH",g2,bbtc);
        m.addParcel(p2);
        Geometry g3 = wktr.read("POLYGON ((0 4, 4 4, 4 7, 0 7, 0 4))");
        Parcel p3 = new Parcel(4,4,400,15,15,15,90,44710,"AHA",g3,bbtc);
        m.addParcel(p3);
        Geometry g4 = wktr.read("POLYGON ((4 4, 10 4, 10 7, 4 7, 4 4))");
        Parcel p4 = new Parcel(2,2,200,12,12,12,80,44109,"PLU",g4,bbtc);
        m.addParcel(p4);
        Geometry g5 = wktr.read("POLYGON ((10 0, 13 0, 13 7, 10 7, 10 0))");
        Parcel p5 = new Parcel(5,5,500,18,18,18,100,44109,"POT",g5,bbtc);
        m.addParcel(p5);
        Geometry g7 = wktr.read("POLYGON ((13 0, 16 0, 16 7, 13 7, 13 0))");
        Parcel p7 = new Parcel(7,7,0,10,10,10,47,44780,"PCT",g7,bbtc);
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
    
    public void testGetSortedList() throws ParseException, NoSuchTableException, DataSourceCreationException, DriverException, DriverLoadException, IOException {
        Step s = instanciateDummyParcels();
        Household h = new Household(1, 45, 62000);
        Manager m = s.getManager();
        m.initializeGlobals();
        m.getNbtc().setNeighbours();
        GaussParcelSelector gp = (GaussParcelSelector) m.getMovingInPS();
        List<Parcel> sortedList = gp.getSortedList(h);
        
        assertTrue(sortedList.get(0).getId() == 4);
        assertTrue(sortedList.get(1).getId() == 3);
        assertTrue(sortedList.get(2).getId() == 5);
        assertTrue(sortedList.get(3).getId() == 1);
        assertTrue(sortedList.get(4).getId() == 2);
    }
    
    public void testSelectedParcel() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException, NoSuchTableException {
        Step s = instanciateDummyParcels();
        Household h = new Household(1, 45, 62000);
        Manager m = s.getManager();
        m.initializeGlobals();
        m.getNbtc().setNeighbours();
        GaussParcelSelector gp = (GaussParcelSelector) m.getMovingInPS();
        Parcel p = gp.selectedParcel(h);
        
        assertTrue(p.getId() == 2 || p.getId() == 1 || p.getId() == 5 || p.getId() == 3 || p.getId() == 4);
    }
}
