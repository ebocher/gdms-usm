/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

/**
 * 
 * @author Thomas Salliou
 */
public class Manager {

    private ArrayList<Parcel> parcelList;
    private Stack<Household> homelessList;
    private int lastCreatedHouseholdId;
    private String dataPath;

    /**
     * Builds a new Manager.
     */
    public Manager(String dP) {
        this.parcelList = new ArrayList();
        this.homelessList = new Stack();
        this.lastCreatedHouseholdId = 0;
        this.dataPath = dP;
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
        Random generator = new Random();
        Household immigrant = new Household(lastCreatedHouseholdId, 20 + generator.nextInt(40), 25000 + generator.nextInt(75000));
        homelessList.add(immigrant);
        lastCreatedHouseholdId++;
    }

    /**
     * Creates a newborn Household and adds it to the homeless list.
     * @param parentHousehold the procreating household
     */
    public void createNewborn(Household parentHousehold) {
        Household newborn = new Household(lastCreatedHouseholdId, 20, parentHousehold.getMaxWealth());
        homelessList.add(newborn);
        lastCreatedHouseholdId++;
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

    public void initialize() throws DataSourceCreationException, DriverException {
        Random generator = new Random();
        DataSourceFactory dsf = new DataSourceFactory();
        File initialFile = new File(dataPath);
        DataSource initialBase = dsf.getDataSource(initialFile);
        SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(initialBase);
        sds.open();

        int id = 0;
        long size = sds.getRowCount();
        for (int j = 0; j < size; j++) {
            //Parcel creation
            Parcel newParcel = new Parcel(id, //id
                    sds.getFieldValue(j, 1).getAsInt(), //buildType
                    sds.getFieldValue(j, 17).getAsDouble() / 1000000, //maxDensity (WARNING : km² input)
                    sds.getFieldValue(j, 19).getAsInt(), //amenitiesIndex
                    sds.getFieldValue(j, 16).getAsInt(), //constructibilityIndex
                    sds.getFieldValue(j, 18).getAsInt(), //inseeCode
                    sds.getFieldValue(j, 15).getAsString(), //zoning
                    sds.getGeometry(j));                                //the_geom
            this.addParcel(newParcel);

            //Households creation by age bracket
            int maxWealth = sds.getFieldValue(j, 9).getAsInt();

            //1-19 years old
            for (int k = 0; k < sds.getFieldValue(j, 10).getAsInt(); k++) {
                Household newHousehold = new Household(lastCreatedHouseholdId,
                        1 + generator.nextInt(19),
                        (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                lastCreatedHouseholdId++;
                newHousehold.moveIn(newParcel);
            }

            //20-39 years old
            for (int k = 0; k < sds.getFieldValue(j, 11).getAsInt(); k++) {
                Household newHousehold = new Household(lastCreatedHouseholdId,
                        20 + generator.nextInt(20),
                        (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                lastCreatedHouseholdId++;
                newHousehold.moveIn(newParcel);
            }

            //40-59 years old
            for (int k = 0; k < sds.getFieldValue(j, 12).getAsInt(); k++) {
                Household newHousehold = new Household(lastCreatedHouseholdId,
                        40 + generator.nextInt(20),
                        (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                lastCreatedHouseholdId++;
                newHousehold.moveIn(newParcel);
            }

            //60-79 years old
            for (int k = 0; k < sds.getFieldValue(j, 13).getAsInt(); k++) {
                Household newHousehold = new Household(lastCreatedHouseholdId,
                        60 + generator.nextInt(20),
                        (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                lastCreatedHouseholdId++;
                newHousehold.moveIn(newParcel);
            }
            id++;
        }

    }
}
