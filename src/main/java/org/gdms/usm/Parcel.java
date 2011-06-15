/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Parcel representation as an object.
 * @author Thomas Salliou
 */
public final class Parcel {

    private final int id;
    private int buildType;
    private double density;
    private double maxDensity;
    private final int amenitiesIndex;
    private final int constructibilityIndex;
    private final double inverseArea;
    private final int inseeCode;
    private String zoning;
    private final Geometry theGeom;
    private Set<Household> householdList;

    /**
     * Builds a new Parcel.
     * @param id an id
     * @param bT a build type
     * @param d a density
     * @param mD a maximum density
     * @param aI an amenities index
     * @param iA the inverse of the area
     * @param cI a constructibility index
     * @param iC an insee code
     * @param z a zoning
     * @param geom a geometry
     */
    public Parcel(int id, int bT, double d, double mD, int aI, int cI, int iC, String z, Geometry geom) {

        this.id = id;
        this.buildType = bT;
        this.density = d;
        this.maxDensity = mD;
        this.amenitiesIndex = aI;
        this.inverseArea = 1.0 / geom.getArea();
        this.constructibilityIndex = cI;
        this.inseeCode = iC;
        this.zoning = z;
        this.theGeom = geom;
        this.householdList = new HashSet();

    }

    /**
     * Builds a new Parcel without providing the density, initializes it to zero.
     * @param id
     * @param bT
     * @param mD
     * @param aI
     * @param cI
     * @param iC
     * @param z
     * @param geom 
     */
    public Parcel(int id, int bT, double mD, int aI, int cI, int iC, String z, Geometry geom) {

        this.id = id;
        this.buildType = bT;
        this.density = 0;
        this.maxDensity = mD;
        this.amenitiesIndex = aI;
        this.inverseArea = 1.0 / geom.getArea();
        this.constructibilityIndex = cI;
        this.inseeCode = iC;
        this.zoning = z;
        this.theGeom = geom;
        this.householdList = new HashSet();

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
    public void updateBuildType() {
        switch (buildType) {
            case 1:
                if (density > 0) {
                    buildType = 2;
                }
            case 2:
                if (density > 155) {
                    buildType = 3;
                }
            case 3:
                if (density > 1000) {
                    buildType = 4;
                }
            case 4:
                if (density > 1466) {
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
     * @return the amenitiesIndex
     */
    public int getAmenitiesIndex() {
        return amenitiesIndex;
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
        Iterator<Household> i = householdList.iterator();
        int total = 0;
        while (i.hasNext()) {
            total += i.next().getWealth();
        }
        return total / this.getLocalPopulation();
    }

    /**
     * @return the inverseArea
     */
    public double getInverseArea() {
        return inverseArea;
    }
}
