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

import java.util.AbstractQueue;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A Queue structure which has a limited size.
 * If an element has to be added, it checks if the Queue is full.
 * In that case, it will automatically remove the oldest element and then add the new one.
 * @author Thomas Salliou
 */
public final class LimitedQueue<E> extends AbstractQueue<E> {

    private Deque<E> ll;
    private int size;
    private int maxSize;

    /**
     * Builds a new LimitedQueue with an integer limit.
     * @param s the limit
     */
    public LimitedQueue(int s) {
        this.ll = new LinkedList<E>();
        this.maxSize = s;
        this.size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new LimitedQueueIterator();
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Checks the size of the list in order to choose behavior :
     * If full, removes the oldest element and adds the new one.
     * If not, adds the new element and increments the size.
     * @param e the element to be added
     * @return a boolean
     */
    public boolean offer(E e) {
        if (this.size == this.maxSize) {
            ll.removeFirst();
        } else {
            this.size++;
        }
        ll.offer(e);
        return true;
    }

    /**
     * Retrieves and removes the head of the limited queue (the first element).
     * Consequently decreases the size of it.
     * @return the first element of the limited queue, or null if it is empty.
     */
    public E poll() {
        if (size != 0) {
            size--;
        }
        return ll.poll();
    }

    /**
     * Retrieves the head of the limited queue without removing it.
     * @return the first element of the limited queue, or null if it is empty.
     */
    public E peek() {
        return ll.peek();
    }

    @Override
    public void clear() {
        size = 0;
        ll.clear();
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    class LimitedQueueIterator implements Iterator<E> {

        private Iterator<E> i;

        public LimitedQueueIterator() {
            this.i = ll.iterator();
        }

        @Override
        public void remove() {
            size--;
            i.remove();
        }

        @Override
        public boolean hasNext() {
            return i.hasNext();
        }

        @Override
        public E next() {
            return i.next();
        }
    }
}
