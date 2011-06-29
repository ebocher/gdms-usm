/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 *
 * @author Thomas Salliou
 */
public class SchellingDecisionMaker extends IsMovingDecisionMaker {
    
    public static final double SEGREGATION_THRESHOLD = 0.5;
    public static final double SEGREGATION_TOLERANCE = 0.2;
    
    public SchellingDecisionMaker() {
        //TGV
    }

    @Override
    public boolean isMoving(Household h) {
        if (getSegregationPart(h) > SEGREGATION_THRESHOLD) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Gets the Segregation percentage according to the Schelling segregation model :
     * here it represents the neighbouring household part which is too poor or too rich,
     * regarding the specified household wealth.
     * @param h the household
     * @return the segregation percentage
     */
    public double getSegregationPart(Household h) {
        Parcel[] neighbours = this.getManager().getNbtc().getNeighbours(h.getHousingPlot());
        int neighbouringHouseholdNumber = 0;
        int nonConvenientHouseholdNumber = 0;
        
        //neighbours in the parcel neighborhood
        for (Parcel p : neighbours) {
            neighbouringHouseholdNumber+=p.getLocalPopulation();
            if (h.getWealth() < (1-SEGREGATION_TOLERANCE)*p.getAverageWealth() || h.getWealth() > (1+SEGREGATION_TOLERANCE)*p.getAverageWealth()) {
                nonConvenientHouseholdNumber+=p.getLocalPopulation();
            }
        }
        
        //neighbours in the same parcel
        neighbouringHouseholdNumber+=h.getHousingPlot().getLocalPopulation() - 1;
        if (h.getWealth() < (1-SEGREGATION_TOLERANCE)*h.getHousingPlot().getAverageWealth() || h.getWealth() > (1+SEGREGATION_TOLERANCE)*h.getHousingPlot().getAverageWealth()) {
            nonConvenientHouseholdNumber+=h.getHousingPlot().getLocalPopulation() - 1;
        }
        return (double) nonConvenientHouseholdNumber / (double) neighbouringHouseholdNumber;
    }
}
