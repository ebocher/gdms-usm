/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.List;

/**
 * 
 * @author Thomas Salliou
 */
public class Manager {
    
    private List<Household> householdList;
    private List<Plot> plotList;
    private List<Household> homelessList;
    
    /**
     * Builds a new Manager.
     */
    public Manager(){
    }

    /**
     * @return the householdList
     */
    public List<Household> getHouseholdList() {
        return householdList;
    }

    /**
     * @return the plotList
     */
    public List<Plot> getPlotList() {
        return plotList;
    }

    /**
     * @return the homelessList
     */
    public List<Household> getHomelessList() {
        return homelessList;
    }
    
    /**
     * Saves relevant information about plots and households into the output database.
     */
    public void saveState(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Creates an immigrant Household and adds it to the homeless list.
     */
    public void createImmigrant(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Creates a newborn Household and adds it to the homeless list.
     * @param parentHousehold the procreating household
     */
    public void createNewborn(Household parentHousehold){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Moves out a household and deletes it from the global household list.
     * @param deceasedHousehold the dying household
     */
    public void kill(Household deceasedHousehold){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets the population count.
     * @return the population count
     */
    public int getPopulation(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
}
