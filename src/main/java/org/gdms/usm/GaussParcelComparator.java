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

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Thomas Salliou
 */
public final class GaussParcelComparator implements Comparator<Parcel>, Serializable {

    private Household concernedHousehold;
    private Manager manager;
    
    /**
     * Builds a gauss parcel comparator with the given household (every sorting 
     * depends on the household).
     * @param h the concerned household
     */
    public GaussParcelComparator(Household h, Manager m) {
        this.concernedHousehold = h;
        this.manager = m;
    }
    
    @Override
    public int compare(Parcel p1, Parcel p2) {
        double parcelComparison = getParcelScore(p1) - getParcelScore(p2);
        if (Math.abs(parcelComparison) < 0.000001) {
            return 0;
        }
        else if (parcelComparison > 0) {
            return 1;
        }
        else {
            return -1;
        }
    }
    
    /**
     * Calculates the score of a parcel according to its attraction and the household wishes.
     * @param p the parcel to be evaluated
     * @return the specified parcel score
     */
    public double getParcelScore(Parcel p) {
        double amenitiesPart = 0;
        double constructibilityPart = manager.getConstructibilityWeighting()*((double) p.getConstructibilityIndex());
        double idealHousingPart = manager.getIdealhousingWeighting()*(100.0 - (double) concernedHousehold.getIdealHousingCoefficient(p));
        
         if (concernedHousehold.getAge() < 35) {
            amenitiesPart = manager.getAmenitiesWeighting()*((double) p.getAmenitiesIndex1());
        }
        else if (concernedHousehold.getAge() < 65) {
            amenitiesPart = manager.getAmenitiesWeighting()*((double) p.getAmenitiesIndex2());
        }
        else {
            amenitiesPart = manager.getAmenitiesWeighting()*((double) p.getAmenitiesIndex3());
        }
        
        return amenitiesPart+constructibilityPart+idealHousingPart;
    }
    
    /**
     * Tests if the two given parcels have the same score.
     * @param p1 the first parcel
     * @param p2 the second parcel
     * @return true if the two parcels are as attractive as each other, false if they're not
     */
    public boolean equals(Parcel p1, Parcel p2) {
        return (compare(p1,p2) == 0);
    }
}
