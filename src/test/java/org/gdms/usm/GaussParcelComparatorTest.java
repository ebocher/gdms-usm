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
    
    //Passes only if the different weightings are set to 1.
    public void testGetParcelScore() throws ParseException {
        Household iAmHomeless = new Household(1,38,48700);
        GaussParcelComparator gps = new GaussParcelComparator(iAmHomeless);
        WKTReader wktr = new WKTReader();
        Geometry g = wktr.read("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
        Parcel p = new Parcel(1,3,140,14,87,44109,"ADR",g,bbtc);
        assertTrue(Math.abs(gps.getParcelScore(p) - 147.0) < 0.000001);
    }
    
    //Passes if the different weightings are set to 1.
    public void testCompare() throws ParseException {
        Household iAmHomeless = new Household(1,38,48700);
        GaussParcelComparator gps = new GaussParcelComparator(iAmHomeless);
        WKTReader wktr = new WKTReader();
        Geometry g = wktr.read("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
        Parcel p = new Parcel(1,3,140,14,87,44109,"ADR",g,bbtc);
        Parcel p2 = new Parcel(2,3,140,18,97,44109,"ADR",g,bbtc);
        Parcel p3 = new Parcel(3,3,140,10,77,44109,"ADR",g,bbtc);
        Parcel p4 = new Parcel(4,3,140,14,87,44109,"ADR",g,bbtc);
        
        assertTrue(gps.compare(p, p2) == -1);
        assertTrue(gps.compare(p, p3) == 1);
        assertTrue(gps.compare(p, p4) == 0);
    }
    
    public void testEquals() throws ParseException {
        Household iAmHomeless = new Household(1,38,48700);
        GaussParcelComparator gps = new GaussParcelComparator(iAmHomeless);
        WKTReader wktr = new WKTReader();
        Geometry g = wktr.read("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
        Parcel p = new Parcel(1,3,140,14,87,44109,"ADR",g,bbtc);
        Parcel p4 = new Parcel(4,3,140,14,87,44109,"ADR",g,bbtc);
        
        assertTrue(gps.equals(p, p4));
    }
}
