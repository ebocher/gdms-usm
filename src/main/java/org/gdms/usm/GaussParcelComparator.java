/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Thomas Salliou
 */
public final class GaussParcelComparator implements Comparator<Parcel>, Serializable {

    public static final double AMENITIES_WEIGHTING = 1.0;
    public static final double CONSTRUCTIBILITY_WEIGHTING = 1.0;
    public static final double IDEALHOUSING_WEIGHTING = 1.0;
    private Household concernedHousehold;
    
    public GaussParcelComparator(Household h) {
        this.concernedHousehold = h;
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
    
    public double getParcelScore(Parcel p) {
        double amenitiesPart = AMENITIES_WEIGHTING*((double) p.getAmenitiesIndex());
        double constructibilityPart = CONSTRUCTIBILITY_WEIGHTING*((double) p.getConstructibilityIndex());
        double idealHousingPart = IDEALHOUSING_WEIGHTING*(100.0 - (double) concernedHousehold.getIdealHousingCoefficient(p));
        
        return amenitiesPart+constructibilityPart+idealHousingPart;
    }
    
    public boolean equals(Parcel p1, Parcel p2) {
        return (compare(p1,p2) == 0);
    }    
}
