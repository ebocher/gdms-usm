/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 *
 * @author Nicolas MULLER
 */
public interface ManagerAdvisor{
    
    /**
     * Notifies the build types should be updated
     * @param m the manager that ask for an update.
     */
    public abstract void waitingUpdatedThresholds(Manager m);
    
    /**
     * 
     * @return true if the thresholds are up on date false is they are not 
     */
    public abstract boolean thresholdsUpOnDate();
}
