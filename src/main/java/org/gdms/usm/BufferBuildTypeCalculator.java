/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import com.vividsolutions.jts.geom.Geometry;
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
    public Map<Integer, Double> calculate(Parcel p) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
    
    public Parcel[] getNeighbors(Parcel p) throws DriverLoadException, NoSuchTableException, DataSourceCreationException, DriverException {
        SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(myManager.getDsf().getDataSource("Plot"));
        Geometry consideredGeom = p.getTheGeom();
        Geometry bufferedGeom = consideredGeom.buffer(10);
        
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
        
        while (k.hasNext()) {
            int j = k.next();
            intersectedParcels[j] = myManager.getParcelList().get(j);
        }
        
        return intersectedParcels;
    }
}