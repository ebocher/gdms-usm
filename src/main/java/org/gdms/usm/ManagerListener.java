/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 *
 * @author Thomas Salliou
 */
public interface ManagerListener {
    
    public void householdAdded(Household h);
    
    public void householdDeleted(Household h);
    
    public void householdMoved(Household h);
}
