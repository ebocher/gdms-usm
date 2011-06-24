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
public class Step {

    private int stepNumber;
    private int year;
    public static final int IMMIGRANT_NUMBER = 7000;
    public static final int NUMBER_OF_TURNS = 3;
    private Manager theManager;

    public Step(int y, String dP, String oP, NearbyBuildTypeCalculator c, StatisticalDecisionMaker sdm, MovingInParcelSelector mips) {
        theManager = new Manager(this, dP, oP, c, sdm, mips);
        stepNumber = 0;
        year = y;
        StatisticalManagerListener sml = new StatisticalManagerListener(sdm);
        theManager.registerManagerListener(sml);
        mips.setManager(theManager);
        c.setManager(theManager);
    }

    public void initialize() throws DataSourceCreationException, DriverException, NoSuchTableException, NonEditableDataSourceException, IOException, IndexException {
        theManager.initializeSimulation();
        theManager.initializeOutputDatabase();
        theManager.getNbtc().setNeighbours();
    }

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
}
