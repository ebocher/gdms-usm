/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.io.IOException;
import org.gdms.data.DataSourceCreationException;
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
    public static final int IMMIGRANT_NUMBER = 7000;
    public static final int NUMBER_OF_TURNS = 3;
    private Manager theManager;

    /**
     * Builds the Step object for a StatisticalDecisionMaker strategy.
     * @param y the initial year
     * @param dP the initial data path
     * @param oP the output data path
     * @param c the nearby build type calculator
     * @param sdm the statistical decision maker
     * @param mips the moving in parcel selector
     */
    public Step(int y, String dP, String oP, NearbyBuildTypeCalculator c, StatisticalDecisionMaker sdm, MovingInParcelSelector mips) {
        theManager = new Manager(this, dP, oP, c, sdm, mips);
        stepNumber = 0;
        year = y;
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
    public Step(int y, String dP, String oP, NearbyBuildTypeCalculator c, SchellingDecisionMaker sdm, MovingInParcelSelector mips) {
        theManager = new Manager(this, dP, oP, c, sdm, mips);
        stepNumber = 0;
        year = y;
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
    public void wholeStep() throws NoSuchTableException, DataSourceCreationException, DriverException, NonEditableDataSourceException {
        stepNumber++;
        theManager.everybodyGrows();
        theManager.whoIsMoving();
        for (int i = 0; i < IMMIGRANT_NUMBER; i++) {
            theManager.createImmigrant();
        }
        theManager.everybodyMovesIn();
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
        for (int i = 0; i<NUMBER_OF_TURNS; i++) {
            wholeStep();
        }
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
}
