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
 * Copyright (C) 2011-1012 IRSTV (FR CNRS 2488)
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
        return sortedList.get(- (int) (Math.abs(generator.nextGaussian())*getManager().getGaussDeviation()*sortedList.size()) + sortedList.size() - 1);
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
