/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 *
 * @author Thomas Salliou
 */
public final class StatisticalManagerListener implements ManagerListener {
    
    private StatisticalDecisionMaker sdm;
    
    /**
     * Builds a StatisticalManagerListener.
     * @param statdm 
     */
    public StatisticalManagerListener(StatisticalDecisionMaker statdm) {
        this.sdm = statdm;
    }
    
    /**
     * Tells the SDM to add the specified household to the map.
     * @param h the added household
     */
    public void householdAdded(Household h) {
        sdm.addHousehold(h);
    }
    
    /**
     * Tells the SDM to delete the specified household from the map.
     * @param h the household to delete
     */
    public void householdDeleted(Household h) {
        sdm.deleteHousehold(h);
    }
    
    /**
     * Tells the SDM to clear the LimitedQueue of the specified household in the map.
     * @param h the household to reset
     */
    public void householdMoved(Household h) {
        sdm.getDissatisfactionMemory(h).clear();
    }
}
