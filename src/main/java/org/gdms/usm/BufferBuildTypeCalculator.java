/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

/**
 *
 * @author Thomas Salliou
 */
public final class BufferBuildTypeCalculator extends NearbyBuildTypeCalculator {

    public BufferBuildTypeCalculator() {
        //TGV : The Great Vacuum
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

    /**
     * This method returns a Parcel array containing all the parcels neighboring a specified parcel.
     * It uses the buffer-intersect way to determine them.
     * @param p the considered parcel
     * @return the neighborhood
     * @throws DriverLoadException
     * @throws NoSuchTableException
     * @throws DataSourceCreationException
     * @throws DriverException 
     */
    Parcel[] getNeighbours(Parcel p) throws DriverLoadException, NoSuchTableException, DataSourceCreationException, DriverException {
        SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(getManager().getDsf().getDataSource("Plot"));
        Geometry consideredGeom = p.getTheGeom();
        Geometry bufferedGeom = consideredGeom.buffer(30);

        sds.open();
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

        return intersectedParcels;
    }
}