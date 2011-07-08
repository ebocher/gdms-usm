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
    
    /**
     * Notifies an added household.
     * @param h the added household 
     */
    void householdAdded(Household h);
    
    /**
     * Notifies a deleted household.
     * @param h the deleted household
     */
    void householdDeleted(Household h);
    
    /**
     * Notifies a moving household.
     * @param h the moving household
     */
    void householdMoved(Household h);
}
