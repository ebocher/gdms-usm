/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 
 * @author Thomas Salliou
 */
public class Manager {

    private ArrayList<Parcel> parcelList;
    private Stack<Household> homelessList;

    /**
     * Builds a new Manager.
     */
    public Manager() {
        this.parcelList = new ArrayList();
        this.homelessList = new Stack();
    }

    /**
     * @return the plotList
     */
    public List<Parcel> getParcelList() {
        return parcelList;
    }

    /**
     * @return the homelessList
     */
    public Stack<Household> getHomelessList() {
        return homelessList;
    }

    /**
     * Saves relevant information about plots and households into the output database.
     */
    public void saveState() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates an immigrant Household and adds it to the homeless list.
     */
    public void createImmigrant() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates a newborn Household and adds it to the homeless list.
     * @param parentHousehold the procreating household
     */
    public void createNewborn(Household parentHousehold) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Moves out a household and deletes it from the global household list.
     * @param deceasedHousehold the dying household
     */
    public void kill(Household deceasedHousehold) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the population count.
     * @return the population count
     */
    public int getPopulation() {
        int pop = 0;

        for (Parcel p : parcelList) {
            pop += p.getLocalPopulation();
        }

        return pop;
    }
    
    /**
     * Adds the specified parcel to the parcelList.
     * @param p a Parcel
     */
    public void addParcel(Parcel p) {
        parcelList.add(p);
    }
}
