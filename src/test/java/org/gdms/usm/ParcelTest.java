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
}
