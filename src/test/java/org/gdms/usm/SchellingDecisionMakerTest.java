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
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.gdms.GdmsWriter;

/**
 *
 * @author Thomas Salliou
 */
public class SchellingDecisionMakerTest extends TestCase {
    
    public SchellingDecisionMakerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        new File(outputPathForTests + "/MiniPlot.gdms").delete();
    }
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String globalsPathForTests = "src/test/resources/globals.gdms";
    private String outputPathForTests = "src/test/resources";
    private DataSourceFactory dsf = new DataSourceFactory(outputPathForTests + "/gdms");
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private SchellingDecisionMaker sdm = new SchellingDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    private Step instanciateDummyParcels() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        WKTReader wktr = new WKTReader();
        Geometry g1 = wktr.read("POLYGON ((0 0, 4 0, 4 4, 0 4, 0 0))");
        Parcel p1 = new Parcel(0,1,100,5,0,0,0,80,44578,"AUY",g1,bbtc);
        m.addParcel(p1);
        Geometry g2 = wktr.read("POLYGON ((4 0, 10 0, 10 4, 4 4, 4 0))");
        Parcel p2 = new Parcel(2,3,300,14,0,0,0,70,44109,"OPH",g2,bbtc);
        m.addParcel(p2);
        Geometry g3 = wktr.read("POLYGON ((0 4, 4 4, 4 7, 0 7, 0 4))");
        Parcel p3 = new Parcel(3,4,400,15,0,0,0,90,44710,"AHA",g3,bbtc);
        m.addParcel(p3);
        Geometry g4 = wktr.read("POLYGON ((4 4, 10 4, 10 7, 4 7, 4 4))");
        Parcel p4 = new Parcel(1,2,200,12,0,0,0,80,44109,"PLU",g4,bbtc);
        m.addParcel(p4);
        Geometry g5 = wktr.read("POLYGON ((10 0, 13 0, 13 7, 10 7, 10 0))");
        Parcel p5 = new Parcel(4,5,500,18,0,0,0,100,44109,"POT",g5,bbtc);
        m.addParcel(p5);
        Geometry g7 = wktr.read("POLYGON ((13 0, 16 0, 16 7, 13 7, 13 0))");
        Parcel p7 = new Parcel(6,7,0,10,0,0,0,47,44780,"PCT",g7,bbtc);
        m.addParcel(p7);
        
        File file1 = new File(outputPathForTests + "/MiniPlot.gdms");
        GdmsWriter plotGW = new GdmsWriter(file1);
        String[] fieldNames1 = {"plotID", "the_geom", "densityOfPopulationMax", "amenitiesIndex1", "amenitiesIndex2", "amenitiesIndex3", "constructibilityIndex"};
        Type[] fieldTypes1 = {TypeFactory.createType(Type.INT), TypeFactory.createType(Type.GEOMETRY), TypeFactory.createType(Type.DOUBLE), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT), TypeFactory.createType(Type.INT)};
        Metadata m1 = new DefaultMetadata(fieldTypes1, fieldNames1);
        plotGW.writeMetadata(0, m1);
        
        for (Parcel p : m.getParcelList()) {
            plotGW.addValues(new Value[]{ValueFactory.createValue(p.getId()),
                        ValueFactory.createValue(p.getTheGeom()),
                        ValueFactory.createValue(p.getMaxDensity()),
                        ValueFactory.createValue(p.getAmenitiesIndex1()),
                        ValueFactory.createValue(p.getAmenitiesIndex2()),
                        ValueFactory.createValue(p.getAmenitiesIndex3()),
                        ValueFactory.createValue(p.getConstructibilityIndex())});
        }
        
        plotGW.writeRowIndexes();
        plotGW.writeExtent();
        plotGW.writeWritenRowCount();
        plotGW.close();
        m.getDsf().getSourceManager().register("Plot", file1);
        
        return s;
    }
    
    public void testGetSegregationPart() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException, NoSuchTableException, IndexException {
        Step s = instanciateDummyParcels();
        Manager m = s.getManager();
        m.initializeGlobals();
        m.getNbtc().setManager(m);
        m.getNbtc().setNeighbours();
        
        //Too rich
        Household richie1 = new Household(1,60,80000);
        Household richie2 = new Household(2,60,80000);
        Household richie3 = new Household(3,60,80000);
        richie1.moveIn(m.getParcelList().get(2));
        richie2.moveIn(m.getParcelList().get(2));
        richie3.moveIn(m.getParcelList().get(2));
        
        //Too poor
        Household poorie1 = new Household(4,60,20000);
        Household poorie2 = new Household(5,60,20000);
        Household poorie3 = new Household(6,60,20000);
        poorie1.moveIn(m.getParcelList().get(3));
        poorie2.moveIn(m.getParcelList().get(3));
        poorie3.moveIn(m.getParcelList().get(3));
        
        //Average is convenient
        Household neighbour1 = new Household(7,60,25000);
        Household neighbour2 = new Household(8,60,45000);
        Household neighbour3 = new Household(9,60,55000);
        Household neighbour4 = new Household(10,60,70000);
        neighbour1.moveIn(m.getParcelList().get(4));
        neighbour2.moveIn(m.getParcelList().get(4));
        neighbour3.moveIn(m.getParcelList().get(4));
        neighbour4.moveIn(m.getParcelList().get(4));
        
        Household helloThere = new Household(11,60,50000);
        helloThere.moveIn(m.getParcelList().get(4));
        
        assertTrue(Math.abs(sdm.getSegregationPart(helloThere) - 0.6) < 0.000001); 
    }
    
    public void testIsMoving() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException, IOException, NoSuchTableException {
        Step s = instanciateDummyParcels();
        Manager m = s.getManager();
        m.initializeGlobals();
        m.getNbtc().setManager(m);
        m.getNbtc().setNeighbours();
        Household richie1 = new Household(1,60,80000);
        Household richie2 = new Household(2,60,80000);
        Household richie3 = new Household(3,60,80000);
        richie1.moveIn(m.getParcelList().get(2));
        richie2.moveIn(m.getParcelList().get(2));
        richie3.moveIn(m.getParcelList().get(2));
        Household poorie1 = new Household(4,60,20000);
        Household poorie2 = new Household(5,60,20000);
        Household poorie3 = new Household(6,60,20000);
        poorie1.moveIn(m.getParcelList().get(3));
        poorie2.moveIn(m.getParcelList().get(3));
        poorie3.moveIn(m.getParcelList().get(3));
        Household neighbour1 = new Household(7,60,25000);
        Household neighbour2 = new Household(8,60,45000);
        Household neighbour3 = new Household(9,60,55000);
        Household neighbour4 = new Household(10,60,70000);
        neighbour1.moveIn(m.getParcelList().get(4));
        neighbour2.moveIn(m.getParcelList().get(4));
        neighbour3.moveIn(m.getParcelList().get(4));
        neighbour4.moveIn(m.getParcelList().get(4));
        Household helloThere = new Household(11,60,50000);
        helloThere.moveIn(m.getParcelList().get(4));
        
        assertFalse(sdm.isMoving(helloThere));
    }
}
