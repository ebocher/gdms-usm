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
import java.util.Iterator;
import junit.framework.TestCase;

/**
 *
 * @author Thomas Salliou
 */
public class LimitedQueueTest extends TestCase {
    
    public LimitedQueueTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd() {
        LimitedQueue<Integer> myMagnificentQueue = new LimitedQueue<Integer>(3);
        assertTrue(myMagnificentQueue.add(1));
        assertTrue(myMagnificentQueue.add(2));
        assertTrue(myMagnificentQueue.add(3));
        assertTrue(myMagnificentQueue.add(4));
        assertTrue(myMagnificentQueue.add(5));
        
        //And now for something completely different
        ArrayList<Integer> a = new ArrayList();
        a.add(3);
        a.add(4);
        a.add(5);
        Iterator<Integer> j = a.iterator();
        Iterator<Integer> i = myMagnificentQueue.iterator();
        while(j.hasNext()) {
            assertTrue(i.next() == j.next());
        }
        assertFalse(i.hasNext());
        
    }
    
    public void testClear() {
        LimitedQueue<Integer> myMagnificentQueue = new LimitedQueue<Integer>(6);
        assertTrue(myMagnificentQueue.add(1));
        assertTrue(myMagnificentQueue.add(2));
        assertTrue(myMagnificentQueue.add(3));
        assertTrue(myMagnificentQueue.add(4));
        assertTrue(myMagnificentQueue.add(5));
        
        myMagnificentQueue.clear();
        Iterator<Integer> i = myMagnificentQueue.iterator();
        assertFalse(i.hasNext());
        assertTrue(myMagnificentQueue.size() == 0);
    }
    
    public void testPoll() {
        LimitedQueue<Integer> myMagnificentQueue = new LimitedQueue<Integer>(6);
        assertTrue(myMagnificentQueue.add(1));
        assertTrue(myMagnificentQueue.add(2));
        assertTrue(myMagnificentQueue.add(3));
        assertTrue(myMagnificentQueue.add(4));
        assertTrue(myMagnificentQueue.add(5));
        
        assertTrue(myMagnificentQueue.getSize() == 5);
        assertTrue(myMagnificentQueue.poll() == 1);
        assertTrue(myMagnificentQueue.getSize() == 4);
    }
    
    public void testPeek() {
        LimitedQueue<Integer> myMagnificentQueue = new LimitedQueue<Integer>(6);
        assertTrue(myMagnificentQueue.add(1));
        assertTrue(myMagnificentQueue.add(2));
        assertTrue(myMagnificentQueue.add(3));
        assertTrue(myMagnificentQueue.add(4));
        assertTrue(myMagnificentQueue.add(5));
        
        assertTrue(myMagnificentQueue.getSize() == 5);
        assertTrue(myMagnificentQueue.peek() == 1);
        assertTrue(myMagnificentQueue.getSize() == 5);
    }
    
    public void testLimitedQueueIteratorRemove() {
        LimitedQueue<Integer> myMagnificentQueue = new LimitedQueue<Integer>(6);
        assertTrue(myMagnificentQueue.add(1));
        assertTrue(myMagnificentQueue.add(2));
        assertTrue(myMagnificentQueue.add(3));
        assertTrue(myMagnificentQueue.add(4));
        assertTrue(myMagnificentQueue.add(5));
        
        Iterator<Integer> i = myMagnificentQueue.iterator();
        assertTrue(i.next() == 1);
        i.remove();
        
        assertTrue(myMagnificentQueue.getSize() == 4);
        assertTrue(myMagnificentQueue.peek() == 2);
    }
}
