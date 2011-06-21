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
    
    private Manager myManager;
    private double DEVIATION = 0.1;
    
    public GaussParcelSelector() {
        //TGV
    }
    
    @Override
    public Parcel selectedParcel(Household h) throws NoSuchTableException, DataSourceCreationException, DriverException {
        List<Parcel> sortedList = getSortedList(h);
        Random generator = new Random();
        Parcel selectedParcel = sortedList.get(- (int) (Math.abs(generator.nextGaussian())*DEVIATION*sortedList.size()) + sortedList.size());
        return selectedParcel;
    }
    
    public List<Parcel> getSortedList(Household h) throws NoSuchTableException, DataSourceCreationException, DriverException {
        List<Parcel> sortedList = new ArrayList<Parcel>();
        for (Parcel p : myManager.getParcelList()) {
            if (!p.isFull() && (h.getWealth() > 0.66*p.getAverageWealth()) && !(p.getBuildType() == 1 && p.getUpgradePotential() < 0.1)) {
                sortedList.add(p);
            }
        }
        GaussParcelComparator gpc = new GaussParcelComparator(h);
        Collections.sort(sortedList, gpc);
        return sortedList;
    }
    
}
