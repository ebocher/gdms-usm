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
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.utils.FileUtils;

/**
 * 
 * @author Thomas Salliou
 */
public class Manager {

    private ArrayList<Parcel> parcelList;
    private Stack<Household> homelessList;
    private int lastCreatedHouseholdId;
    private String dataPath;
    private String outputPath;
    private DataSourceFactory dsf;

    /**
     * Builds a new Manager.
     */
    public Manager(String dP, String oP) {
        this.parcelList = new ArrayList();
        this.homelessList = new Stack();
        this.lastCreatedHouseholdId = 0;
        this.dataPath = dP;
        this.outputPath = oP;
        FileUtils.deleteDir(new File(oP, "gdms"));
        this.dsf = new DataSourceFactory(oP+"gdms");
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
     * Initializes the output database by creating the different tables as gdms files.
     */
    public void createOutputDatabase() throws DriverException {
        Type integ = TypeFactory.createType(64);
        Type geometry = TypeFactory.createType(4096);
        Type bool = TypeFactory.createType(2);
        
        //Household table
        File file1 = new File(outputPath+"Household.gdms");
        String[] fieldNames1 = {"householdID", "maximumWealth"};
        Type[] fieldTypes1 = {integ, integ};
        Metadata m1 = new DefaultMetadata(fieldTypes1, fieldNames1);
        FileSourceCreation f1 = new FileSourceCreation(file1, m1);
        dsf.getSourceManager().register("Household",f1);
        
        //Plot table
        File file2 = new File(outputPath+"Plot.gdms");
        String[] fieldNames2 = {"plotID", "the_geom", "densityOfPopulationMax", "amenitiesIndex", "constructibilityIndex"};
        Type[] fieldTypes2 = {integ, geometry, integ, integ, integ};
        Metadata m2 = new DefaultMetadata(fieldTypes2, fieldNames2);
        FileSourceCreation f2 = new FileSourceCreation(file2, m2);
        dsf.getSourceManager().register("Plot",f2);
        
        //HouseholdState table
        File file3 = new File(outputPath+"HouseholdState.gdms");
        String[] fieldNames3 = {"householdID", "stepNumber", "plotID", "age", "alive"};
        Type[] fieldTypes3 = {integ, integ, integ, integ, bool};
        Metadata m3 = new DefaultMetadata(fieldTypes3, fieldNames3);
        FileSourceCreation f3 = new FileSourceCreation(file3, m3);
        dsf.getSourceManager().register("HouseholdState",f3);
        
        //PlotState table
        File file4 = new File(outputPath+"PlotState.gdms");
        String[] fieldNames4 = {"plotID", "stepNumber", "buildType", "averageWealth"};
        Type[] fieldTypes4 = {integ, integ, integ, integ};
        Metadata m4 = new DefaultMetadata(fieldTypes4, fieldNames4);
        FileSourceCreation f4 = new FileSourceCreation(file4, m4);
        dsf.getSourceManager().register("PlotState",f4);
        
        //Step table
        File file5 = new File(outputPath+"Step.gdms");
        String[] fieldNames5 = {"stepNumber", "year", "population"};
        Type[] fieldTypes5 = {integ, integ, integ};
        Metadata m5 = new DefaultMetadata(fieldTypes5, fieldNames5);
        FileSourceCreation f5 = new FileSourceCreation(file5, m5);
        dsf.getSourceManager().register("Step",f5);
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

    public void initializeSimulation() throws DataSourceCreationException, DriverException {
        Random generator = new Random();
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

    /**
     * @return the dsf
     */
    public DataSourceFactory getDsf() {
        return dsf;
    }
}
