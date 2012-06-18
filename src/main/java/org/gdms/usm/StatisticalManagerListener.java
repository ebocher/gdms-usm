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

/**
 *
 * @author Thomas Salliou
 */
public final class StatisticalManagerListener implements ManagerListener {
    
    private StatisticalDecisionMaker sdm;
    
    /**
     * Builds a StatisticalManagerListener.
     * @param statdm 
     */
    public StatisticalManagerListener(StatisticalDecisionMaker statdm) {
        this.sdm = statdm;
    }
    
    /**
     * Tells the SDM to add the specified household to the map.
     * @param h the added household
     */
    public void householdAdded(Household h) {
        sdm.addHousehold(h);
    }
    
    /**
     * Tells the SDM to delete the specified household from the map.
     * @param h the household to delete
     */
    public void householdDeleted(Household h) {
        sdm.deleteHousehold(h);
    }
    
    /**
     * Tells the SDM to clear the LimitedQueue of the specified household in the map.
     * @param h the household to reset
     */
    public void householdMoved(Household h) {
        sdm.getDissatisfactionMemory(h).clear();
    }
}
