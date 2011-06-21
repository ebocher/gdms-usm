/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

/**
 *
 * @author Thomas Salliou
 */
public abstract class MovingInParcelSelector {
    
    private Manager myManager;

    /**
     * Selects a parcel for the household to move in.
     * @param h the specified household
     * @return the parcel to move into
     */
    public abstract Parcel selectedParcel(Household h) throws NoSuchTableException, DataSourceCreationException, DriverException;
    
    /**
     * @return the myManager
     */
    public final Manager getManager() {
        return myManager;
    }

    /**
     * @param myManager the myManager to set
     */
    public final void setManager(Manager myManager) {
        this.myManager = myManager;
    }
    
}
