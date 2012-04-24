/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import junit.framework.TestCase;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

/**
 *
 * @author Thomas Salliou
 */
public class GaussParcelComparatorTest extends TestCase {
    
    public GaussParcelComparatorTest(String testName) {
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
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String globalsPathForTests = "src/test/resources/globals.gdms";
    private String outputPathForTests = "src/test/resources";
    private DataSourceFactory dsf = new DataSourceFactory(outputPathForTests + "/gdms");
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    public void testGetParcelScore() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        Household iAmHomeless = new Household(1,38,48700);
        GaussParcelComparator gpss = new GaussParcelComparator(iAmHomeless, m);
        WKTReader wktr = new WKTReader();
        Geometry g = wktr.read("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
        Parcel p = new Parcel(1,3,140,14,14,14,87,44109,"ADR",g,bbtc);
        assertTrue(Math.abs(gpss.getParcelScore(p) - 147.0) < 0.000001);
    }
    
    public void testCompare() throws ParseException, DataSourceCreationException, DriverLoadException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        Household iAmHomeless = new Household(1,38,48700);
        GaussParcelComparator gpss = new GaussParcelComparator(iAmHomeless,m);
        WKTReader wktr = new WKTReader();
        Geometry g = wktr.read("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
        Parcel p = new Parcel(1,3,140,14,14,14,87,44109,"ADR",g,bbtc);
        Parcel p2 = new Parcel(2,3,140,18,18,18,97,44109,"ADR",g,bbtc);
        Parcel p3 = new Parcel(3,3,140,10,10,10,77,44109,"ADR",g,bbtc);
        Parcel p4 = new Parcel(4,3,140,14,14,14,87,44109,"ADR",g,bbtc);
        
        assertTrue(gpss.compare(p, p2) == -1);
        assertTrue(gpss.compare(p, p3) == 1);
        assertTrue(gpss.compare(p, p4) == 0);
    }
    
    public void testEquals() throws ParseException, DriverLoadException, DataSourceCreationException, DriverException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = s.getManager();
        m.initializeGlobals();
        Household iAmHomeless = new Household(1,38,48700);
        GaussParcelComparator gpss = new GaussParcelComparator(iAmHomeless,m);
        WKTReader wktr = new WKTReader();
        Geometry g = wktr.read("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
        Parcel p = new Parcel(1,3,140,14,14,14,87,44109,"ADR",g,bbtc);
        Parcel p4 = new Parcel(4,3,140,14,14,14,87,44109,"ADR",g,bbtc);
        
        assertTrue(gpss.equals(p, p4));
    }
}
