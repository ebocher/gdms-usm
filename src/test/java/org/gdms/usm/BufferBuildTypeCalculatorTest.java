/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.DriverException;

/**
 *
 * @author Thomas Salliou
 */
public class BufferBuildTypeCalculatorTest extends TestCase {
    
    public BufferBuildTypeCalculatorTest(String testName) {
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
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String outputPathForTests = "src/test/resources/";
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    public void testGetNeighbours() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Manager m = new Manager(dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        bbtc.setManager(m);
        m.initializeSimulation();
        m.initializeOutputDatabase();
        
        Parcel[] myNeighbours = bbtc.getNeighbours(m.getParcelList().get(3425));
        assertTrue(myNeighbours.length == 7);
        assertTrue(myNeighbours[0].getId() == 3440);
        assertTrue(myNeighbours[1].getId() == 3383);
        assertTrue(myNeighbours[2].getId() == 3382);
        assertTrue(myNeighbours[3].getId() == 3439);
        assertTrue(myNeighbours[4].getId() == 3404);
        assertTrue(myNeighbours[5].getId() == 3438);
        assertTrue(myNeighbours[6].getId() == 3403);
    }
    
    public void testCalculate() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Manager m = new Manager(dataPathForTests, outputPathForTests, bbtc, sdm, gps);
        bbtc.setManager(m);
        m.initializeSimulation();
        m.initializeOutputDatabase();
        
        Map<Integer,Double> nbta = m.getParcelList().get(3425).getNearbyBuildTypeAreas();
        assertTrue(nbta.containsKey(5));
        assertTrue(Math.abs(nbta.get(5) - 40909.88763335168) < 0.000001);
    }
}
