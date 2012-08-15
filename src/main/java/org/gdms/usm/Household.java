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
    public int getIdealHousingCoefficient(Parcel p) {

        int ihcByWealth = 66;
        int ihcByAge = 66;
        final int wealth = this.getWealth();
        final int theAge = this.getAge();

        switch (p.getBuildType()) {
            //Build type 1 and 2 case : become or is "houses with big gardens".
            case 1:
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
            case 7:
                throw new IllegalArgumentException("Can't determine an IHC for a business park.");
            default:
                throw new IllegalArgumentException("Build type is not valid.");
        }
        return (ihcByAge + ihcByWealth) / 2;
    }
    
    /**
     * @return the result of getIdealHousingCoefficient called for the housingPlot 
     */
    public int getMovingIHC() {
        return getIdealHousingCoefficient(housingPlot);
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
        if (p != null) {
          p.addHousehold(this);
          housingPlot = p;
        }
    }

    /**
     * Resets the housing parcel to null, and removes the household of this parcel household list.
     */
    public void moveOut() {
        housingPlot.removeHousehold(this);
        housingPlot = null;
    }
}
