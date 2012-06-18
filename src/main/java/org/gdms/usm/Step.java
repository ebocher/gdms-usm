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
 * Copyright (C) 2011-1012 IRSTV (FR CNRS 2488)
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
public final class Step {
    
    private int stepNumber;
    private int year;
    private Manager theManager;
    private Set<StepListener> listeners;

    /**
     * Builds the Step object for a StatisticalDecisionMaker strategy.
     * @param y the initial year
     * @param dP the initial data path
     * @param oP the output data path
     * @param c the nearby build type calculator
     * @param sdm the statistical decision maker
     * @param mips the moving in parcel selector
     */
    public Step(int y, String dP, String gP, String oP, NearbyBuildTypeCalculator c, StatisticalDecisionMaker sdm, MovingInParcelSelector mips, DataSourceFactory dsf) {
        theManager = new Manager(this, dP, gP, oP, c, sdm, mips, dsf);
        stepNumber = 0;
        year = y;
        listeners = new HashSet<StepListener>();
        StatisticalManagerListener sml = new StatisticalManagerListener(sdm);
        theManager.registerManagerListener(sml);
        sdm.setManager(theManager);
        mips.setManager(theManager);
        c.setManager(theManager);
    }
    
    /**
     * Builds the Step object for a SchellingDecisionMaker strategy.
     * @param y the initial year
     * @param dP the initial data path
     * @param oP the output data path
     * @param c the nearby build type calculator
     * @param sdm the schelling decision maker
     * @param mips the moving in parcel selector
     */
    public Step(int y, String dP, String gP, String oP, NearbyBuildTypeCalculator c, SchellingDecisionMaker sdm, MovingInParcelSelector mips, DataSourceFactory dsf) {
        theManager = new Manager(this, dP, gP, oP, c, sdm, mips, dsf);
        stepNumber = 0;
        year = y;
        listeners = new HashSet<StepListener>();
        sdm.setManager(theManager);
        mips.setManager(theManager);
        c.setManager(theManager);
    }

    /**
     * Initializes everything needed for the simulation : input data reading,
     * output database creation and neighbours calculation.
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws NoSuchTableException
     * @throws NonEditableDataSourceException
     * @throws IOException
     * @throws IndexException 
     */
    public void initialize() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        theManager.initializeGlobals();
        theManager.initializeSimulation();
        theManager.initializeOutputDatabase();
        theManager.getNbtc().setNeighbours();
    }

    /**
     * Calls every method needed for a step of the simulation : first everybody grows,
     * then the annoyed households move out, then the immigrants come, then all the
     * homeless households move in and finally we save the new state in the output database.
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws NonEditableDataSourceException 
     */
    public void wholeStep() throws NoSuchTableException, DataSourceCreationException, DriverException, NonEditableDataSourceException, IOException {
        stepNumber++;
        notifyNextTurn();
        theManager.everybodyGrows();
        theManager.whoIsMoving();
        for (int i = 0; i < theManager.getImmigrantNumber(); i++) {
            theManager.createImmigrant();
        }
        theManager.everybodyMovesIn();
        theManager.updateBuildType();        
        theManager.saveState();
    }
    
    /**
     * Calls initialize and then wholeStep for a given number of turns in order to launch
     * the entire simulation.
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws NonEditableDataSourceException
     * @throws IOException
     * @throws IndexException 
     */
    public void wholeSimulation() throws NoSuchTableException, DataSourceCreationException, DriverException, NonEditableDataSourceException, IOException, IndexException {
        initialize();
        notifyInitializationDone();
        for (int i = 0; i < theManager.getNumberOfTurns(); i++) {
            wholeStep();
        }
        notifySimulationDone();
    }

    /**
     * @return the stepNumber
     */
    public int getStepNumber() {
        return stepNumber;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @return the theManager
     */
    public Manager getManager() {
        return theManager;
    }
    
    public Set<StepListener> getListeners() {
        return listeners;
    }
    
    /**
     * Registers a StepListener to the listeners set.
     * @param sl the StepListener to register
     */
    public void registerStepListener(StepListener sl) {
        listeners.add(sl);
    }

    /**
     * Unregisters a StepListener from the listeners set.
     * @param sl the StepListener to unregister
     */
    public void unregisterStepListener(StepListener sl) {
        listeners.remove(sl);
    }
    
    /**
     * Notify method, called when the next turn is engaged.
     */
    private void notifyNextTurn() {
        for (StepListener sl : listeners) {
            sl.nextTurn(stepNumber, theManager.getNumberOfTurns(), theManager.getPopulation(), theManager.getDeadNumber(), theManager.getNewbornNumber(), theManager.getMoversCount());
        }
    }

    private void notifyInitializationDone() {
        for (StepListener sl : listeners) {
            sl.initializationDone();
        }
    }
    
    private void notifySimulationDone() {
        for (StepListener sl : listeners) {
            sl.simulationDone();
        }
    }
    /**
     * Set the thresholds in the manager
     * @param t1 a threshold
     * @param t2 a threshold
     * @param t3 a threshold
     * @param t4 a threshold
     */
    public void setThresholds(double t1, double t2, double t3, double t4)
    {
        theManager.setThresholds(t1, t2, t3, t4);
    }    
}
