/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 *
 * @author Thomas Salliou
 */
public interface StepListener {
    
    void nextTurn(int cT, int nT, int pop, int dead, int newb, int mov);
    
    void initializationDone();
    
    void householdDisappeared(Household h);
    
}
