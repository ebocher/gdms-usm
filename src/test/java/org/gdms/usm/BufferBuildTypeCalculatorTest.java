/**
 *
 * Gdms-USM is a library dedicated to multi-agent simulation for modeling urban sprawl.
 * It is based on the GDMS library. It uses the OrbisGIS renderer to display results.
 *
 * This version is developed at French IRSTV Institute and at LIENSs UMR 7266 laboratory
 * (http://lienss.univ-larochelle.fr/) as part of the VegDUD project, funded by the
 * French Agence Nationale de la Recherche (ANR) under contract ANR-09-VILL-0007.
 *
 * Gdms-USM is distributed under GPL 3 license. It is maintained by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2011-2012 IRSTV (FR CNRS 2488)
 *
 * Gdms-USM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms-USM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms-USM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.gdms.usm;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
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
        new File(outputPathForTests+"/Household.gdms").delete();
        new File(outputPathForTests+"/HouseholdState.gdms").delete();
        new File(outputPathForTests+"/Plot.gdms").delete();
        new File(outputPathForTests+"/PlotState.gdms").delete();
        new File(outputPathForTests+"/Step.gdms").delete();
    }
    
    private String dataPathForTests = "src/test/resources/initialdatabase.gdms";
    private String globalsPathForTests = "src/test/resources/globals.gdms";
    private String outputPathForTests = "src/test/resources";
    private DataSourceFactory dsf = new DataSourceFactory(outputPathForTests + "/gdms");
    private BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
    private StatisticalDecisionMaker sdm = new StatisticalDecisionMaker();
    private GaussParcelSelector gps = new GaussParcelSelector();
    
    public void testGetNeighbours() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        bbtc.setManager(m);
        m.initializeSimulation();
        m.initializeOutputDatabase();
        m.initializeGlobals();
        m.getNbtc().setNeighbours();
        
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
        Step s = new Step(2000, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        Manager m = new Manager(s, dataPathForTests, globalsPathForTests, outputPathForTests, bbtc, sdm, gps, dsf);
        bbtc.setManager(m);
        m.initializeGlobals();
        m.initializeSimulation();
        m.initializeOutputDatabase();
        m.getNbtc().setNeighbours();
        
        Map<Integer,Double> nbta = m.getParcelList().get(3425).getNearbyBuildTypeAreas();
        assertTrue(nbta.containsKey(5));
        assertTrue(Math.abs(nbta.get(5) - 40909.88763335168) < 0.000001);
    }
}
