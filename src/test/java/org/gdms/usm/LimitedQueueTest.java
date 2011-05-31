/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
}
