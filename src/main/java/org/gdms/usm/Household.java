/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm;

import java.util.Queue;

/**
 *
 * @author Thomas Salliou
 */
public class Household {
    
    private int age;
    private int maxWealth;
    private Queue<Integer> dissatisfactionMemory;
    private Parcel housingPlot;
    
    /**
     * Builds a new Household.
     * @param a
     * @param mW 
     */
    public Household(int a, int mW){
        
        this.age = a;
        this.maxWealth = mW;
        
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @return the maxWealth
     */
    public int getMaxWealth() {
        return maxWealth;
    }

    /**
     * @return the housingPlot
     */
    public Parcel getHousingPlot() {
        return housingPlot;
    }
    
    /**
     * Increments the household age.
     */
    public void grow(){
        this.age++;
    }
    
    /**
     * Gets the household's wealth.
     * @return the household's wealth
     */
    public int getWealth(){
        return this.maxWealth*this.age/60;
    }
    
    /**
     * Gets the immediate dissatisfaction index.
     * @return the immediate dissatisfaction index
     */
    public int getImmediateDissatisfaction(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets the cumulated dissatisfaction index, based on the dissatisfaction memory.
     * @return the cumulated dissatisfaction index.
     */
    public int getCumulatedDissatisfaction(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets the willingness-to-move coefficient.
     * @return the willingness to move coefficient
     */
    public int getWillMoveCoefficient(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets the ideal housing coefficient.
     * @return the ideal housing coefficient
     */
    public int getIdealHousingCoefficient(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
}
