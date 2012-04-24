/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

/**
 * Parcel representation as an object.
 * @author Thomas Salliou
 */
public final class Parcel {

    private final int id;
    private int buildType;
    private double density;
    private double maxDensity;
    private final int amenitiesIndex1;
    private final int amenitiesIndex2;
    private final int amenitiesIndex3;
    private final int constructibilityIndex;
    private final double inverseArea;
    private final int inseeCode;
    private String zoning;
    private final Geometry theGeom;
    private Set<Household> householdList;
    private NearbyBuildTypeCalculator nbtc;

    /**
     * Builds a new Parcel.
     * @param id an id
     * @param bT a build type
     * @param d a density
     * @param mD a maximum density
     * @param aI a general amenities index
     * @param aI1 an amenities index for first class age
     * @param aI2 an amenities index for second class age
     * @param aI3 an amenities index for third class age
     * @param iA the inverse of the area
     * @param cI a constructibility index
     * @param iC an insee code
     * @param z a zoning
     * @param geom a geometry
     */
    public Parcel(int id, int bT, double d, double mD, int aI1, int aI2, int aI3, int cI, int iC, String z, Geometry geom, NearbyBuildTypeCalculator c) {

        this.id = id;
        this.buildType = bT;
        this.density = d;
        this.maxDensity = mD;
        this.amenitiesIndex1 = aI1;
        this.amenitiesIndex2 = aI2;
        this.amenitiesIndex3 = aI3;
        this.inverseArea = 1.0 / geom.getArea();
        this.constructibilityIndex = cI;
        this.inseeCode = iC;
        this.zoning = z;
        this.theGeom = geom;
        this.householdList = new HashSet();
        this.nbtc = c;

    }

    /**
     * Builds a new Parcel without providing the density, initializes it to zero.
     * @param id
     * @param bT
     * @param mD
     * @param aI
     * @param aI1
     * @param aI2
     * @param aI3
     * @param cI
     * @param iC
     * @param z
     * @param geom 
     */
    public Parcel(int id, int bT, double mD, int aI1, int aI2, int aI3, int cI, int iC, String z, Geometry geom, NearbyBuildTypeCalculator c) {

        this.id = id;
        this.buildType = bT;
        this.density = 0;
        this.maxDensity = mD;
        this.amenitiesIndex1 = aI1;
        this.amenitiesIndex2 = aI2;
        this.amenitiesIndex3 = aI3;
        this.inverseArea = 1.0 / geom.getArea();
        this.constructibilityIndex = cI;
        this.inseeCode = iC;
        this.zoning = z;
        this.theGeom = geom;
        this.householdList = new HashSet();
        this.nbtc = c;

    }

    /**
     * Adds the Household to the Household list and increases the density.
     * @param movingHousehold a household moving in
     */
    public void addHousehold(Household movingHousehold) {
        householdList.add(movingHousehold);
        density += inverseArea;
    }

    /**
     * Removes the Household from the Household list and decreases the density.
     * @param movingHousehold a household moving out 
     */
    public void removeHousehold(Household movingHousehold) {
        householdList.remove(movingHousehold);
        density -= inverseArea;
    }

    /**
     * Changes the build type if needed.
     * WARNING : lack of break statements is VOLUNTARY.
     */
    public void updateBuildType(double threshold_1, double threshold_2, double threshold_3, double threshold_4) {
        switch (buildType) {
            case 1:
                if (density > threshold_1) {
                    buildType = 2;
                }
            case 2:
                if (density > threshold_2) {
                    buildType = 3;
                }
            case 3:
                if (density > threshold_3) {
                    buildType = 4;
                }
            case 4:
                if (density > threshold_4) {
                    buildType = 5;
                }
            case 5:
                break;
            case 7:
                throw new IllegalArgumentException("Can't update build type for business parks !");
            default:
                throw new IllegalArgumentException("Build type is not valid.");
        }
    }

    /**
     * Indicates if the Plot is full or not.
     * Tests if we can add a Household without breaking the maxDensity limit.
     * @return a boolean
     */
    public boolean isFull() {
        double incrementedDensity = this.density + this.inverseArea;
        return (incrementedDensity > this.maxDensity);
    }

    /**
     * Gets the number of households living in the Plot.
     * @return the population on the Plot
     */
    public int getLocalPopulation() {
        return householdList.size();
    }

    /**
     * @return the buildType
     */
    public int getBuildType() {
        return buildType;
    }

    /**
     * @return the density
     */
    public double getDensity() {
        return density;
    }

    /**
     * @return the maxDensity
     */
    public double getMaxDensity() {
        return maxDensity;
    }

    
     /**
     * @return the amenitiesIndex1
     */
    public int getAmenitiesIndex1() {
        return amenitiesIndex1;
    }
    
     /**
     * @return the amenitiesIndex
     */
    public int getAmenitiesIndex2() {
        return amenitiesIndex2;
    }
    
     /**
     * @return the amenitiesIndex
     */
    public int getAmenitiesIndex3() {
        return amenitiesIndex3;
    }

    /**
     * @return the constructibilityIndex
     */
    public int getConstructibilityIndex() {
        return constructibilityIndex;
    }

    /**
     * @return the inseeCode
     */
    public int getInseeCode() {
        return inseeCode;
    }

    /**
     * @return the zoning
     */
    public String getZoning() {
        return zoning;
    }

    /**
     * @return the theGeom
     */
    public Geometry getTheGeom() {
        return theGeom;
    }

    /**
     * @return the householdList
     */
    public Set<Household> getHouseholdList() {
        return householdList;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the average wealth of the parcel, based on the inhabitants' wealth.
     * @return the average wealth
     */
    public int getAverageWealth() {
        if (this.getLocalPopulation() == 0) {
            return 0;
        } else {
            int total = 0;
            for (Household h : householdList) {
                total += h.getWealth();
            }
            return total / this.getLocalPopulation();
        }
    }
        
    /**
     * @return the inverseArea
     */
    public double getInverseArea() {
        return inverseArea;
    }

    /**
     * Returns a map with the total areas of each neighboring buildtype.
     * @return 
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException 
     */
    public Map<Integer, Double> getNearbyBuildTypeAreas() throws NoSuchTableException, DataSourceCreationException, DriverException {
        return nbtc.calculate(this);
    }

    /**
     * Returns the total proportion of superior neighboring buildtype.
     * @return 
     */
    public double getUpgradePotential() throws NoSuchTableException, DataSourceCreationException, DriverException {
        double totalArea = 0;
        double superiorArea = 0;
        Map<Integer, Double> nbta = getNearbyBuildTypeAreas();
        for (int i : nbta.keySet()) {
            totalArea += nbta.get(i);
            if (i > buildType) {
                superiorArea += nbta.get(i);
            }
        }
        return superiorArea / totalArea;
    }
}
