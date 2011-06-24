/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
