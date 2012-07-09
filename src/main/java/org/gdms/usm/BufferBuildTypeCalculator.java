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

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.driver.DriverException;

/**
 *
 * @author Thomas Salliou
 */
public final class BufferBuildTypeCalculator extends NearbyBuildTypeCalculator {

    private Map<Parcel,Parcel[]> neighbours;
    
    public BufferBuildTypeCalculator() {
        neighbours = new HashMap<Parcel,Parcel[]>();
    }
    
    @Override
    public void setNeighbours() throws NoSuchTableException, DataSourceCreationException, DriverException {
        DataSource sds = getManager().getDsf().getDataSource("Plot");
        sds.open();
        
        for (Parcel p : getManager().getParcelList()) {

            Geometry consideredGeom = p.getTheGeom();
            Geometry bufferedGeom = consideredGeom.buffer(getManager().getBufferSize());

            DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery("the_geom", bufferedGeom.getEnvelopeInternal());
            Iterator<Integer> s = sds.queryIndex(query);
            LinkedList<Integer> intersectedRowIds = new LinkedList<Integer>();

            while (s.hasNext()) {
                int i = s.next();
                if (bufferedGeom.intersects(sds.getGeometry(i)) && i != p.getId()) {
                    intersectedRowIds.add(i);
                }
            }

            Parcel[] intersectedParcels = new Parcel[intersectedRowIds.size()];
            Iterator<Integer> k = intersectedRowIds.iterator();

            int l = 0;
            while (k.hasNext()) {
                int j = k.next();
                intersectedParcels[l] = getManager().getParcelList().get(j);
                l++;
            }

            neighbours.put(p, intersectedParcels);
        }
    }

    @Override
    public Map<Integer, Double> calculate(Parcel p) throws NoSuchTableException, DataSourceCreationException, DriverException {
        Parcel[] theNeighbours = this.getNeighbours(p);
        HashMap<Integer, Double> buildTypeAreas = new HashMap<Integer, Double>();
        for (int i = 0; i < theNeighbours.length; i++) {
            if (buildTypeAreas.get(theNeighbours[i].getBuildType()) != null) {
                buildTypeAreas.put(theNeighbours[i].getBuildType(), buildTypeAreas.get(theNeighbours[i].getBuildType()) + theNeighbours[i].getTheGeom().getArea());
            }
            else {
                buildTypeAreas.put(theNeighbours[i].getBuildType(), theNeighbours[i].getTheGeom().getArea());
            }
        }
        return buildTypeAreas;
    }
    
    @Override
    Parcel[] getNeighbours(Parcel p) {
        return neighbours.get(p);
    }
}