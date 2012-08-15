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

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

/**
 *
 * @author Thomas Salliou
 */
public abstract class MovingInParcelSelector {
    
    private Manager myManager;

    /**
     * Selects a parcel for the household to move in.
     * @param h the specified household
     * @return the parcel to move into
     */
    public abstract Parcel selectedParcel(Household h) throws NoSuchTableException, DataSourceCreationException, DriverException;
    
    /**
     * @return the myManager
     */
    public final Manager getManager() {
        return myManager;
    }

    /**
     * @param myManager the myManager to set
     */
    public final void setManager(Manager myManager) {
        this.myManager = myManager;
    }
    
}
