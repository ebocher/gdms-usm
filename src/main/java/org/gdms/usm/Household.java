/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 * Household representation as an object.
 * @author Thomas Salliou
 */
public final class Household {

    private final int id;
    private int age;
    private final int maxWealth;
    private Parcel housingPlot;

    /**
     * Builds a new Household with given age and maximum wealth.
     * @param a
     * @param mW 
     */
    public Household(int id, int a, int mW) {
        this.id = id;
        this.age = a;
        this.maxWealth = mW;
    }

    /**
     * Builds a new Household with given age, maximum wealth and housing parcel.
     * @param a
     * @param mW
     * @param hP 
     */
    public Household(int id, int a, int mW, Parcel hP) {
        this.id = id;
        this.age = a;
        this.maxWealth = mW;
        this.housingPlot = hP;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @return the maxWealth
     */
    public int getMaxWealth() {
        return maxWealth;
    }

    /**
     * @return the housingPlot
     */
    public Parcel getHousingPlot() {
        return housingPlot;
    }

    /**
     * Increments the household age.
     */
    public void grow() {
        this.age++;
    }

    /**
     * Gets the household's wealth.
     * @return the household's wealth
     */
    public int getWealth() {
        if (age < 60) {
            return maxWealth * age / 60;
        } else {
            return maxWealth;
        }
    }

    /**
     * Gets the ideal housing coefficient.
     * @return the ideal housing coefficient
     */
    public int getIdealHousingCoefficient() {

        int ihcByWealth = 66;
        int ihcByAge = 66;
        final int wealth = this.getWealth();
        final int theAge = this.getAge();

        switch (this.housingPlot.getBuildType()) {
            //Empty parcel case
            case 1:
                throw new IllegalArgumentException("Can't get IHC for an empty parcel / the parcel should not be empty.");
            //Build type 2 case : houses with big gardens.
            case 2:
                if (wealth < 18000) {
                    ihcByWealth = 62;
                } else if (wealth < 25200) {
                    ihcByWealth = 67;
                } else if (wealth < 35400) {
                    ihcByWealth = 56;
                } else if (wealth < 45000) {
                    ihcByWealth = 57;
                } else {
                    ihcByWealth = 52;
                }

                if (theAge < 25) {
                    ihcByAge = 43;
                } else if (theAge < 35) {
                    ihcByAge = 58;
                } else if (theAge < 50) {
                    ihcByAge = 53;
                } else if (theAge < 65) {
                    ihcByAge = 63;
                } else {
                    ihcByAge = 81;
                }
                break;
            //Build type 3 case : houses with little gardens.
            case 3:
                if (wealth < 18000) {
                    ihcByWealth = 62;
                } else if (wealth < 25200) {
                    ihcByWealth = 61;
                } else if (wealth < 35400) {
                    ihcByWealth = 55;
                } else if (wealth < 45000) {
                    ihcByWealth = 49;
                } else {
                    ihcByWealth = 69;
                }

                if (theAge < 25) {
                    ihcByAge = 81;
                } else if (theAge < 35) {
                    ihcByAge = 54;
                } else if (theAge < 50) {
                    ihcByAge = 53;
                } else if (theAge < 65) {
                    ihcByAge = 59;
                } else {
                    ihcByAge = 61;
                }
                break;
            //Build types 4 and 5 : flats.
            case 4:
            case 5:
                if (wealth < 18000) {
                    ihcByWealth = 77;
                } else if (wealth < 25200) {
                    ihcByWealth = 72;
                } else if (wealth < 35400) {
                    ihcByWealth = 89;
                } else if (wealth < 45000) {
                    ihcByWealth = 94;
                } else {
                    ihcByWealth = 79;
                }

                if (theAge < 25) {
                    ihcByAge = 76;
                } else if (theAge < 35) {
                    ihcByAge = 88;
                } else if (theAge < 50) {
                    ihcByAge = 94;
                } else if (theAge < 65) {
                    ihcByAge = 79;
                } else {
                    ihcByAge = 58;
                }
                break;
            default:
                throw new IllegalArgumentException("Build type is not valid.");
        }

        return (ihcByAge + ihcByWealth) / 2;

    }

    /**
     * @return the id 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the housing parcel and adds the household to this parcel household list.
     * @param p a Parcel
     */
    public void moveIn(Parcel p) {
        p.addHousehold(this);
        housingPlot = p;
    }

    /**
     * Resets the housing parcel to null, and removes the household of this parcel household list.
     */
    public void moveOut() {
        housingPlot.removeHousehold(this);
        housingPlot = null;
    }
}
