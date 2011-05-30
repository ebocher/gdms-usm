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
     * Builds a new Household with given age and maximum wealth.
     * @param a
     * @param mW 
     */
    public Household(int a, int mW){
        this.age = a;
        this.maxWealth = mW;
    }
    
    /**
     * Builds a new Household with given age, maximum wealth and housing parcel.
     * @param a
     * @param mW
     * @param hP 
     */
    public Household(int a, int mW, Parcel hP){
        this.age = a;
        this.maxWealth = mW;
        this.housingPlot = hP;
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
        int wmc;
        
        //is it Nantes or not
        if (this.housingPlot.getInseeCode() == 44109) wmc=12;
        else wmc=6;
        
        //how about the age bracket
        if      (this.age < 25) wmc+=36;
        else if (this.age < 35) wmc+=30;
        else if (this.age < 50) wmc+=18;
        else if (this.age < 65) wmc+=8;
        else                    wmc+=2;
        
        return wmc;
    }
    
    /**
     * Gets the ideal housing coefficient.
     * @return the ideal housing coefficient
     */
    public int getIdealHousingCoefficient(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
}
