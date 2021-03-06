/**
 *
 * Gdms-USM is a library dedicated to multi-agent simulation for modeling urban sprawl.
 * It is based on the GDMS library. It uses the OrbisGIS renderer to display results.
 *
 * This version is developed at French IRSTV Institute and at LIENSs UMR 7266 laboratory
 * (http://lienss.univ-larochelle.fr/) as part of the VegDUD project, funded by the
 * French Agence Nationale de la Recherche (ANR) under contract ANR-09-VILL-0007.
 *
 * Gdms-USM is distributed under GPL 3 license. It is maintained by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2011-2012 IRSTV (FR CNRS 2488)
 *
 * Gdms-USM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms-USM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms-USM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.gdms.usm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.gdms.GdmsWriter;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

/**
 * The Manager keeps track of parcels and new arrivals. 
 * It also manages all the database reading (initialization) and writing (output database) stuff.
 * @author Thomas Salliou
 */
public final class Manager {

    private Step step;
    private List<Parcel> parcelList;
    private Stack<Household> homelessList;
    private Stack<Household> newbornList;
    private int lastCreatedHouseholdId;
    private String dataPath;
    private String globalsPath;
    private String outputPath;
    private DataSourceFactory dsf;
    private NearbyBuildTypeCalculator nbtc;
    private Set<ManagerListener> listeners;
    private IsMovingDecisionMaker isMovingDM;
    private MovingInParcelSelector movingInPS;
    
    private double bufferSize;
    private double amenitiesWeighting;
    private double constructibilityWeighting;
    private double idealhousingWeighting;
    private double gaussDeviation;
    private double segregationThreshold;
    private double segregationTolerance;
    private int householdMemory;
    private double movingThreshold;
    private int immigrantNumber;
    private int numberOfTurns;

    private int newbornNumber;
    private int deadNumber;
    private int moversCount;
    
    //threshold of buildtypes
    private double threshold_1;
    private double threshold_2;
    private double threshold_3;
    private double threshold_4;
    private boolean modifyThresholds;
    private ManagerAdvisor advisor = null;
    
    /**
     * Builds a new Manager.
     * @param dP the initialization data source
     * @param oP the output folder
     * @param c the nearby build type calculator strategy
     */
    public Manager(Step s, String dP, String gP, String oP, NearbyBuildTypeCalculator c, IsMovingDecisionMaker isdm, MovingInParcelSelector mips, DataSourceFactory dsf) {
        this.step = s;
        this.parcelList = new ArrayList();
        this.homelessList = new Stack();
        this.newbornList = new Stack();
        this.lastCreatedHouseholdId = 0;
        this.dataPath = dP;
        this.globalsPath = gP;
        this.outputPath = oP;
        FileUtils.deleteDir(new File(oP, "/gdms"));
        this.dsf = dsf;
        this.nbtc = c;
        this.listeners = new HashSet<ManagerListener>();
        this.isMovingDM = isdm;
        this.movingInPS = mips;
        newbornNumber = 0;
        deadNumber = 0;
        moversCount = 0;
        modifyThresholds = false;
        advisor = null;
    }

    public void setModifyThresholds(boolean modifyThresholds) {
        this.modifyThresholds = modifyThresholds;
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
     * Initializes the output database with the initialization data.
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws NonEditableDataSourceException 
     */
    public void initializeOutputDatabase() throws NoSuchTableException, DataSourceCreationException, DriverException, NonEditableDataSourceException, IOException, IndexException {
        Type integ = TypeFactory.createType(64);
        Type geometry = TypeFactory.createType(4096);
        Type bool = TypeFactory.createType(2);
        Type doubl = TypeFactory.createType(16);

        //Folder creation (only if not existing)
        File folder = new File(outputPath+"/");
        folder.mkdirs();
        
        //Plot table creation
        File file1 = new File(outputPath + "/Plot.gdms");
        GdmsWriter plotGW = new GdmsWriter(file1);
        String[] fieldNames1 = {"plotID", "the_geom", "densityOfPopulationMax", "amenitiesIndex1", "amenitiesIndex2", "amenitiesIndex3", "constructibilityIndex"};
        Type[] fieldTypes1 = {integ, geometry, doubl, integ, integ, integ, integ};
        Metadata m1 = new DefaultMetadata(fieldTypes1, fieldNames1);
        plotGW.writeMetadata(0, m1);

        //Household table creation
        File file2 = new File(outputPath + "/Household.gdms");
        GdmsWriter householdGW = new GdmsWriter(file2);
        String[] fieldNames2 = {"householdID", "maximumWealth"};
        Type[] fieldTypes2 = {integ, integ};
        Metadata m2 = new DefaultMetadata(fieldTypes2, fieldNames2);
        householdGW.writeMetadata(0, m2);

        //Plot and Household tables filling
        for (Parcel p : parcelList) {
            plotGW.addValues(new Value[]{ValueFactory.createValue(p.getId()),
                        ValueFactory.createValue(p.getTheGeom()),
                        ValueFactory.createValue(p.getMaxDensity()),
                        ValueFactory.createValue(p.getAmenitiesIndex1()),
                        ValueFactory.createValue(p.getAmenitiesIndex2()),
                        ValueFactory.createValue(p.getAmenitiesIndex3()),
                        ValueFactory.createValue(p.getConstructibilityIndex())});
            for (Household h : p.getHouseholdList()) {
                householdGW.addValues(new Value[]{ValueFactory.createValue(h.getId()), ValueFactory.createValue(h.getMaxWealth())});
            }
        }

        plotGW.writeRowIndexes();
        plotGW.writeExtent();
        plotGW.writeWritenRowCount();
        plotGW.close();
        householdGW.writeRowIndexes();
        householdGW.writeExtent();
        householdGW.writeWritenRowCount();
        householdGW.close();

        dsf.getSourceManager().register("Plot", file1);
        if (!dsf.getIndexManager().isIndexed("Plot", "the_geom")) {
            NullProgressMonitor npm = new NullProgressMonitor();
            dsf.getIndexManager().buildIndex("Plot", "the_geom", npm);
        }
        dsf.getSourceManager().register("Household", file2);

        //HouseholdState table creation
        File file3 = new File(outputPath + "/HouseholdState.gdms");
        GdmsWriter householdStateGW = new GdmsWriter(file3);
        String[] fieldNames3 = {"householdID", "stepNumber", "plotID", "age", "alive"};
        Type[] fieldTypes3 = {integ, integ, integ, integ, bool};
        Metadata m3 = new DefaultMetadata(fieldTypes3, fieldNames3);
        householdStateGW.writeMetadata(0, m3);
        householdStateGW.writeRowIndexes();
        householdStateGW.writeExtent();
        householdStateGW.writeWritenRowCount();
        householdStateGW.close();
        dsf.getSourceManager().register("HouseholdState", file3);

        //PlotState table creation
        File file4 = new File(outputPath + "/PlotState.gdms");
        GdmsWriter plotStateGW = new GdmsWriter(file4);
        String[] fieldNames4 = {"plotID", "stepNumber", "buildType", "averageWealth"};
        Type[] fieldTypes4 = {integ, integ, integ, integ};
        Metadata m4 = new DefaultMetadata(fieldTypes4, fieldNames4);
        plotStateGW.writeMetadata(0, m4);
        plotStateGW.writeRowIndexes();
        plotStateGW.writeExtent();
        plotStateGW.writeWritenRowCount();
        plotStateGW.close();
        dsf.getSourceManager().register("PlotState", file4);

        //Step table creation
        File file5 = new File(outputPath + "/Step.gdms");
        GdmsWriter stepGW = new GdmsWriter(file5);
        String[] fieldNames5 = {"stepNumber", "year", "population"};
        Type[] fieldTypes5 = {integ, integ, integ};
        Metadata m5 = new DefaultMetadata(fieldTypes5, fieldNames5);
        stepGW.writeMetadata(0, m5);
        stepGW.writeRowIndexes();
        stepGW.writeExtent();
        stepGW.writeWritenRowCount();
        stepGW.close();
        dsf.getSourceManager().register("Step", file5);
    }

    /**
     * Saves relevant information about plots and households into the output database.
     */
    public void saveState() throws NoSuchTableException, DataSourceCreationException, DriverException, NonEditableDataSourceException, IOException {
        Type integ = TypeFactory.createType(64);
        Type bool = TypeFactory.createType(2);
        
        //Household table
        File file1 = new File(outputPath + "/Household_temp.gdms");
        GdmsWriter householdGW = new GdmsWriter(file1);
        String[] fieldNames1 = {"householdID", "maximumWealth"};
        Type[] fieldTypes1 = {integ, integ};
        Metadata m1 = new DefaultMetadata(fieldTypes1, fieldNames1);
        householdGW.writeMetadata(0, m1);
        
            //Recreate table and copy old data
        DataSource householdDS = dsf.getDataSource("Household");
        householdDS.open();
        for (int i = 0; i < householdDS.getRowCount(); i++) {
            householdGW.addValues(householdDS.getRow(i));
        }
        householdDS.close();
        
            //Fill in new data
        while (!newbornList.empty()) {
            Household h = newbornList.pop();
            householdGW.addValues(new Value[]{ValueFactory.createValue(h.getId()), ValueFactory.createValue(h.getMaxWealth())});
        }
        
        householdGW.writeRowIndexes();
        householdGW.writeExtent();
        householdGW.writeWritenRowCount();
        householdGW.close();
        dsf.getSourceManager().delete("Household");
        File file1b = new File(outputPath + "/Household.gdms");
        file1.renameTo(file1b);
        dsf.getSourceManager().register("Household", file1b);

        //PlotState table
        File file2 = new File(outputPath + "/PlotState_temp.gdms");
        GdmsWriter plotStateGW = new GdmsWriter(file2);
        String[] fieldNames2 = {"plotID", "stepNumber", "buildType", "averageWealth"};
        Type[] fieldTypes2 = {integ, integ, integ, integ};
        Metadata m2 = new DefaultMetadata(fieldTypes2, fieldNames2);
        plotStateGW.writeMetadata(0, m2);
        
            //Recreate and copy
        DataSource plotStateDS = dsf.getDataSource("PlotState");
        plotStateDS.open();
        for (int i = 0; i < plotStateDS.getRowCount(); i++) {
            plotStateGW.addValues(plotStateDS.getRow(i));
        }
        plotStateDS.close();
        
            //Fill in new data
        for (Parcel p : parcelList) {
            plotStateGW.addValues(new Value[]{ValueFactory.createValue(p.getId()),
                        ValueFactory.createValue(step.getStepNumber()),
                        ValueFactory.createValue(p.getBuildType()),
                        ValueFactory.createValue(p.getAverageWealth())
                    });
        }
        
        plotStateGW.writeRowIndexes();
        plotStateGW.writeExtent();
        plotStateGW.writeWritenRowCount();
        plotStateGW.close();
        dsf.getSourceManager().delete("PlotState");
        File file2b = new File(outputPath + "/PlotState.gdms");
        file2.renameTo(file2b);
        dsf.getSourceManager().register("PlotState", file2b);
        
        //HouseholdState table
        File file3 = new File(outputPath + "/HouseholdState_temp.gdms");
        GdmsWriter householdStateGW = new GdmsWriter(file3);
        String[] fieldNames3 = {"householdID", "stepNumber", "plotID", "age", "alive"};
        Type[] fieldTypes3 = {integ, integ, integ, integ, bool};
        Metadata m3 = new DefaultMetadata(fieldTypes3, fieldNames3);
        householdStateGW.writeMetadata(0, m3);
        
            //Old data
        DataSource householdStateDS = dsf.getDataSource("HouseholdState");
        householdStateDS.open();
        for (int i = 0; i < householdStateDS.getRowCount(); i++) {
            householdStateGW.addValues(householdStateDS.getRow(i));
        }
        householdStateDS.close();
        
            //New data
        for (Parcel p : parcelList) {
            for (Household hh : p.getHouseholdList()) {
                householdStateGW.addValues(new Value[]{ValueFactory.createValue(hh.getId()),
                            ValueFactory.createValue(step.getStepNumber()),
                            ValueFactory.createValue(p.getId()),
                            ValueFactory.createValue(hh.getAge()),
                            ValueFactory.createValue(true)
                        });
            }
        }
        
        householdStateGW.writeRowIndexes();
        householdStateGW.writeExtent();
        householdStateGW.writeWritenRowCount();
        householdStateGW.close();
        dsf.getSourceManager().delete("HouseholdState");
        File file3b = new File(outputPath + "/HouseholdState.gdms");
        file3.renameTo(file3b);
        dsf.getSourceManager().register("HouseholdState", file3b);
        
        //Step table
        File file4 = new File(outputPath + "/Step_temp.gdms");
        GdmsWriter stepGW = new GdmsWriter(file4);
        String[] fieldNames4 = {"stepNumber", "year", "population"};
        Type[] fieldTypes4 = {integ, integ, integ};
        Metadata m4 = new DefaultMetadata(fieldTypes4, fieldNames4);
        stepGW.writeMetadata(0, m4);
        
            //Old data
        DataSource stepDS = dsf.getDataSource("Step");
        stepDS.open();
        for (int i = 0; i < stepDS.getRowCount(); i++) {
            stepGW.addValues(stepDS.getRow(i));
        }
        stepDS.close();
        
            //New data
        stepGW.addValues(new Value[]{ValueFactory.createValue(step.getStepNumber()),
            ValueFactory.createValue(step.getYear()),
            ValueFactory.createValue(getPopulation())});
        
        stepGW.writeRowIndexes();
        stepGW.writeExtent();
        stepGW.writeWritenRowCount();
        stepGW.close();
        dsf.getSourceManager().delete("Step");
        File file4b = new File(outputPath + "/Step.gdms");
        file4.renameTo(file4b);
        dsf.getSourceManager().register("Step", file4b);
    }

    /**
     * Creates an immigrant Household and adds it to the homeless list.
     */
    public void createImmigrant() {
        Random generator = new Random();
        Household immigrant = new Household(lastCreatedHouseholdId, 20 + generator.nextInt(40), 10000 + generator.nextInt(50000));
        homelessList.add(immigrant);
        newbornList.add(immigrant);
        householdAdded(immigrant);
        lastCreatedHouseholdId++;
    }

    /**
     * Creates a newborn Household and adds it to the homeless list.
     * @param parentHousehold the procreating household
     */
    public void createNewborn(Household parentHousehold) {
        Household newborn = new Household(lastCreatedHouseholdId, 20, parentHousehold.getMaxWealth());
        homelessList.add(newborn);
        newbornList.add(newborn);
        householdAdded(newborn);
        lastCreatedHouseholdId++;
    }

    /**
     * Moves out a household without putting it into the homeless list.
     * @param deceasedHousehold the dying household
     */
    public void kill(Household deceasedHousehold) {
        deceasedHousehold.moveOut();
        householdDeleted(deceasedHousehold);
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

    /**
     * Generates all the parcels and the initial households according to initialization data.
     * @throws DataSourceCreationException
     * @throws DriverException 
     */
    public void initializeSimulation() throws DataSourceCreationException, DriverException {
        Random generator = new Random();
        File initialFile = new File(dataPath);
        DataSource initialBase = dsf.getDataSource(initialFile);
        initialBase.open();

        long size = initialBase.getRowCount();
        for (int j = 0; j < size; j++) {
            //Parcel creation
            Parcel newParcel = new Parcel(initialBase.getInt(j, "id"), //id
                    initialBase.getInt(j, "buildType"), //buildType
                    initialBase.getDouble(j, "maxDensity") / 1000000, //maxDensity (WARNING : km² input)
                    initialBase.getInt(j, "amenIndex1"), //amenitiesIndex1
                    initialBase.getInt(j, "amenIndex2"), //amenitiesIndex2
                    initialBase.getInt(j, "amenIndex3"), //amenitiesIndex3
                    initialBase.getInt(j, "constIndex"), //constructibilityIndex
                    initialBase.getInt(j, "inseeCode"), //inseeCode
                    initialBase.getString(j, "zoning"), //zoning
                    initialBase.getGeometry(j), //geometry
                    nbtc);                              //nearbyBuildTypeCalculator
            this.addParcel(newParcel);

            if (newParcel.getBuildType() != 7) {

                //Households creation by age bracket
                int maxWealth = initialBase.getInt(j, "wealth");
                
                //20-39 years old
                for (int k = 0; k < initialBase.getInt(j, "localPop20"); k++) {
                    Household newHousehold = new Household(lastCreatedHouseholdId,
                            20 + generator.nextInt(20),
                            (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                    lastCreatedHouseholdId++;
                    householdAdded(newHousehold);
                    newHousehold.moveIn(newParcel);
                }

                //40-59 years old
                for (int k = 0; k < initialBase.getInt(j, "localPop40"); k++) {
                    Household newHousehold = new Household(lastCreatedHouseholdId,
                            40 + generator.nextInt(20),
                            (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                    lastCreatedHouseholdId++;
                    householdAdded(newHousehold);
                    newHousehold.moveIn(newParcel);
                }

                //60-79 years old
                for (int k = 0; k < initialBase.getInt(j, "localPop60"); k++) {
                    Household newHousehold = new Household(lastCreatedHouseholdId,
                            60 + generator.nextInt(20),
                            (int) ((int) (0.90 * maxWealth)) + generator.nextInt((int) (0.20 * maxWealth)));
                    lastCreatedHouseholdId++;
                    householdAdded(newHousehold);
                    newHousehold.moveIn(newParcel);
                }
            }
        }

    }

    /**
     * @return the dsf
     */
    public DataSourceFactory getDsf() {
        return dsf;
    }

    /**
     * Notify method, called when a household is added.
     * @param h the household added
     */
    private void householdAdded(Household h) {
        for (ManagerListener ml : listeners) {
            ml.householdAdded(h);
        }
    }

    /**
     * Notify method, called when a household is deleted.
     * @param h the deleted household 
     */
    private void householdDeleted(Household h) {
        for (ManagerListener ml : listeners) {
            ml.householdDeleted(h);
        }
    }

    /**
     * Notify method, called when a household moves.
     * @param h the moving household
     */
    private void householdMoved(Household h) {
        for (ManagerListener ml : listeners) {
            ml.householdMoved(h);
        }
    }

    /**
     * Registers a ManagerListener to the listeners set.
     * @param ml the ManagerListener to register
     */
    public void registerManagerListener(ManagerListener ml) {
        listeners.add(ml);
    }

    /**
     * Unregisters a ManagerListener from the listeners set.
     * @param ml 
     */
    public void unregisterManagerListener(ManagerListener ml) {
        listeners.remove(ml);
    }

    /**
     * Checks which households wants to move and moves them out. Warns the listeners when a household moves.
     */
    public void whoIsMoving() {
        int moversTempCount = 0;
        for (Parcel p : parcelList) {
            Stack<Household> areGoingToMove = new Stack<Household>();
            for (Household h : p.getHouseholdList()) {
                if (isMovingDM.isMoving(h)) {
                    areGoingToMove.add(h);
                }
            }
            for (Household h : areGoingToMove) {
                h.moveOut();
                homelessList.add(h);
                householdMoved(h);
                moversTempCount++;
            }
        }
        moversCount = moversTempCount;
    }

    /**
     * Moves in every homeless household
     */
    public void everybodyMovesIn() throws NoSuchTableException, DataSourceCreationException, DriverException {
        while (!homelessList.isEmpty()) {
            Household h = homelessList.pop();
            h.moveIn(movingInPS.selectedParcel(h));
        }
    }
    
    /**
     * Update buildtypes if necessary.
     */
    public void updateBuildType()
    {
        if (modifyThresholds) {
            advisor.waitingUpdatedThresholds(this);
            //Here is the worst thing I ever did... 
            while (!advisor.thresholdsUpOnDate()) {
                try {
                    Thread.sleep(50);  
                } catch (InterruptedException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.WARNING, null, ex);
                }
            }
        }
            
        for (Parcel p : parcelList) {
            if (p.getBuildType() != 7) {
                p.updateBuildType(threshold_1, threshold_2, threshold_3, threshold_4);
            }
        }
    }

    /**
     * @return the listeners
     */
    public Set<ManagerListener> getListeners() {
        return listeners;
    }

    /**
     * Calls grow method for every household and checks if it needs to be killed or if it needs to procreate.
     */
    public void everybodyGrows() {
        LinkedList<Household> deadPeople = new LinkedList<Household>();
        newbornNumber = 0;
        deadNumber = 0;
        for (Parcel p : parcelList) {
            for (Household h : p.getHouseholdList()) {
                h.grow();
                if (h.getAge() > 79) {
                    deadPeople.add(h);
                    deadNumber++;
                } else if (h.getAge() == 60) {
                    createNewborn(h);
                    newbornNumber++;
                }
            }
        }
        for (Household h : deadPeople) {
            kill(h);
        }
    }

    /**
     * @return the nbtc
     */
    public NearbyBuildTypeCalculator getNbtc() {
        return nbtc;
    }

    /**
     * @return the isMovingDM
     */
    public IsMovingDecisionMaker getIsMovingDM() {
        return isMovingDM;
    }

    /**
     * @return the movingInPS
     */
    public MovingInParcelSelector getMovingInPS() {
        return movingInPS;
    }

    /**
     * @return the newbornList
     */
    public Stack<Household> getNewbornList() {
        return newbornList;
    }
    
    /**
     * Initializes the global constants with the values provided by a gdms file.
     * @throws DriverLoadException
     * @throws DataSourceCreationException
     * @throws DriverException 
     */
    public void initializeGlobals() throws DataSourceCreationException, DriverException {
        File globalsFile = new File(globalsPath);
        DataSource globals = dsf.getDataSource(globalsFile);
        globals.open();
        
        bufferSize = globals.getDouble(0,"bufferSize");
        amenitiesWeighting = globals.getDouble(0,"amenitiesWeighting");
        constructibilityWeighting = globals.getDouble(0,"constructibilityWeighting");
        idealhousingWeighting = globals.getDouble(0, "idealhousingWeighting");
        gaussDeviation = globals.getDouble(0,"gaussDeviation");
        segregationThreshold = globals.getDouble(0,"segregationThreshold");
        segregationTolerance = globals.getDouble(0,"segregationTolerance");
        householdMemory = globals.getInt(0, "householdMemory");
        movingThreshold = globals.getDouble(0, "movingThreshold");
        immigrantNumber = globals.getInt(0, "immigrantNumber");
        numberOfTurns = globals.getInt(0, "numberOfTurns");
        threshold_1 = globals.getDouble(0, "threshold_1");
        threshold_2 = globals.getDouble(0, "threshold_2");
        threshold_3 = globals.getDouble(0, "threshold_3");
        threshold_4 = globals.getDouble(0, "threshold_4");
        
        globals.close();
    }

    /**
     * @return the bufferSize
     */
    public double getBufferSize() {
        return bufferSize;
    }

    /**
     * @return the amenitiesWeighting
     */
    public double getAmenitiesWeighting() {
        return amenitiesWeighting;
    }

    /**
     * @return the constructibilityWeighting
     */
    public double getConstructibilityWeighting() {
        return constructibilityWeighting;
    }

    /**
     * @return the idealhousingWeighting
     */
    public double getIdealhousingWeighting() {
        return idealhousingWeighting;
    }

    /**
     * @return the gaussDeviation
     */
    public double getGaussDeviation() {
        return gaussDeviation;
    }

    /**
     * @return the segregationThreshold
     */
    public double getSegregationThreshold() {
        return segregationThreshold;
    }

    /**
     * @return the segregationTolerance
     */
    public double getSegregationTolerance() {
        return segregationTolerance;
    }

    /**
     * @return the householdMemory
     */
    public int getHouseholdMemory() {
        return householdMemory;
    }

    /**
     * @return the movingThreshold
     */
    public double getMovingThreshold() {
        return movingThreshold;
    }

    /**
     * @return the immigrantNumber
     */
    public int getImmigrantNumber() {
        return immigrantNumber;
    }

    /**
     * @return the numberOfTurns
     */
    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    /**
     * @return the step
     */
    public Step getStep() {
        return step;
    }

    /**
     * @return the newbornNumber
     */
    public int getNewbornNumber() {
        return newbornNumber;
    }

    /**
     * @return the deadNumber
     */
    public int getDeadNumber() {
        return deadNumber;
    }

    /**
     * @return the moversCount
     */
    public int getMoversCount() {
        return moversCount;
    }

    /**
     * Sort the thresohlds then set them in the right order
     * @param t_1 a threshold
     * @param t_2 a threshold
     * @param t_3 a threshold
     * @param t_4 a threshold
     */
    public void setThresholds(double t_1, double t_2, double t_3, double t_4)
    {
        double[] thresholds = new double[] {t_1, t_2, t_3, t_4};
        Arrays.sort(thresholds);
        
        threshold_1 = thresholds[0];
        threshold_2 = thresholds[1];
        threshold_3 = thresholds[2];
        threshold_4 = thresholds[3];
        return;
    }
    
    /**
     * 
     * @return the list of thresholds
     */
    public double[] getThresholds()
    {
        double[] ret = {threshold_1, threshold_2, threshold_3, threshold_4};
        return ret;
    }

    public void setAdvisor(ManagerAdvisor advisor) {
        this.advisor = advisor;
    }
    
    public void setBufferSize(double value)
    {
        this.bufferSize = value;
    }
}


