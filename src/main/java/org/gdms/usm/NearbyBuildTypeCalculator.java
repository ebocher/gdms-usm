/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.Map;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

/**
 *
 * @author Thomas Salliou
 */
public abstract class NearbyBuildTypeCalculator {

    private Manager myManager;

    /**
     * Calculates the nearby build type proportions and returns them in a map.
     * @param p the parcel
     * @return the map with proportions associated to a build type
     */
    public abstract Map<Integer, Double> calculate(Parcel p) throws NoSuchTableException, DataSourceCreationException, DriverException;
    
    /**
     * @param m the simulation manager
     */
    public final void setManager(Manager m) {
        myManager = m;
    }
    
    /**
     * 
     * @return the manager
     */
    public final Manager getManager() {
        return myManager;
    }
}
