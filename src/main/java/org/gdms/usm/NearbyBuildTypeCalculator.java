/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.Map;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;

/**
 *
 * @author Thomas Salliou
 */
public abstract class NearbyBuildTypeCalculator {

    protected Manager myManager;

    /**
     * Calculates the nearby build type proportions and returns them in a map.
     * @param p the parcel
     * @return the map with proportions associated to a build type
     */
    public abstract Map<Integer, Double> calculate(Parcel p);

    /**
     * @param m the simulation manager
     */
    public final void setManager(Manager m) {
        myManager = m;
    }
}
