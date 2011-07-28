/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

/**
 *
 * @author Thomas Salliou
 */
public final class GaussParcelSelector extends MovingInParcelSelector {
    
    private static final double DEVIATION = 0.1;
    
    public GaussParcelSelector() {
        //TGV
    }
    
    @Override
    public Parcel selectedParcel(Household h) throws NoSuchTableException, DataSourceCreationException, DriverException {
        List<Parcel> sortedList = getSortedList(h);
        Random generator = new Random();
        if (sortedList.isEmpty()) {
            for (StepListener sl : getManager().getStep().getListeners()) {
                sl.householdDisappeared(h);
                return null;
            }
        }
        return sortedList.get(- (int) (Math.abs(generator.nextGaussian())*DEVIATION*sortedList.size()) + sortedList.size() - 1);
    }
    
    /**
     * Filters the global parcel list according to physical considerations (parcel full or not)
     * and household expectations. Then sorts this list (the less attractive to the most) and returns it.
     * @param h the household who wants to move in
     * @return the parcel list sorted
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException 
     */
    public List<Parcel> getSortedList(Household h) throws NoSuchTableException, DataSourceCreationException, DriverException {
        List<Parcel> sortedList = new ArrayList<Parcel>();
        for (Parcel p : this.getManager().getParcelList()) {
            if ((p.getBuildType() != 7) && !p.isFull() && (h.getWealth() > 0.66*p.getAverageWealth()) && !(p.getBuildType() == 1 && p.getUpgradePotential() < 0.1)) {
                sortedList.add(p);
            }
        }
        GaussParcelComparator gpc = new GaussParcelComparator(h, getManager());
        Collections.sort(sortedList, gpc);
        return sortedList;
    }
}
