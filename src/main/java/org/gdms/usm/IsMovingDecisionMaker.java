/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

/**
 *
 * @author Thomas Salliou
 */
public abstract class IsMovingDecisionMaker {
    
    private Manager myManager;

    public abstract boolean isMoving(Household h);
    
    /**
     * @return the myManager
     */
    public final Manager getManager() {
        return myManager;
    }

    /**
     * @param myManager the myManager to set
     */
    public final void setManager(Manager myManager) {
        this.myManager = myManager;
    }
    
    
    
}
