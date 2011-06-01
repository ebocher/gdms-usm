/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * 
 * @author Thomas Salliou
 */
public class Manager {

    private ArrayList<Parcel> parcelList;
    private Stack<Household> homelessList;
    private int lastCreatedHouseholdId;

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
        lastCreatedHouseholdId++;
        Random generator = new Random();
        Household immigrant = new Household(lastCreatedHouseholdId,20+generator.nextInt(40),25000+generator.nextInt(75000));
        homelessList.add(immigrant);
    }

    /**
     * Creates a newborn Household and adds it to the homeless list.
     * @param parentHousehold the procreating household
     */
    public void createNewborn(Household parentHousehold) {
        lastCreatedHouseholdId++;
        Household newborn = new Household(lastCreatedHouseholdId,20,parentHousehold.getMaxWealth());
        homelessList.add(newborn);
    }

    /**
     * Moves out a household without putting it into the homeless list.
     * @param deceasedHousehold the dying household
     */
    public void kill(Household deceasedHousehold) {
        deceasedHousehold.moveOut();
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
