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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ARTHUR
 */
public class BufferBuildVariable extends NearbyBuildTypeCalculator {

        private Map<Parcel,Parcel[]> neighbours;

        public BufferBuildVariable() {
            neighbours = new HashMap<Parcel,Parcel[]>();
        }

        @Override
        public void setNeighbours() throws NoSuchTableException, DataSourceCreationException, DriverException {
            DataSource sds = getManager().getDsf().getDataSource("Plot");
            sds.open();

            for (Parcel p : getManager().getParcelList()) {

                Geometry consideredGeom = p.getTheGeom();
                
                //setBufferSizer(buffervariable);
                Geometry bufferedGeom = consideredGeom.buffer(getManager().getBufferSize());
                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(bufferedGeom.getEnvelopeInternal(), "the_geom");
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