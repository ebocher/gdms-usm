/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

/**
 *
 * @author Thomas Salliou
 */
public class SpinnerParameterPanel extends JPanel implements ActionListener{
    
    private Map<String, JSpinner> spinners;
    private Map<String, JRadioButton> selections;
    
    public SpinnerParameterPanel() {
        super (new SpringLayout());
        spinners = new HashMap<String, JSpinner>();
        selections = new HashMap<String, JRadioButton>();
        
        String[] labels = {"Year :",
            "Number of Turns :",
            "Buffer size :",
            "Amenities Weighting :",
            "Constructibility Weighting :",
            "Idealhousing Weighting :",
            "Gauss Deviation :",
            "Statistical model",
            "Schelling model",
            "Segregation Threshold :",
            "Segregation Tolerance :",
            "Household Memory :",
            "Moving Threshold :",
            "Immigrant Number :"
        };
        
        String[] tooltips = {"The starting year of the simulation.",
            "The number of turns the simulation is going to run.",
            "The size of the buffer used to determine neighbouring parcels.",
            "The weighting of amenities index used for dissatisfaction calculations.",
            "The weighting of constructibility index used for dissatisfaction calculations.",
            "The weighting of idealhousing coefficient used for dissatisfaction calculations.",
            "The relative deviation of the gaussian needed for moving in parcel selection.",
            "Selects the Statistical segregation model as a moving out model.",
            "Selects the Schelling segregation model as a moving out model.",
            "The part of neighbours too much rich and also too much poor, needed to decide if the household moves or not.",
            "The tolerance for determining if a neighbour is too much rich or too much poor.",
            "The size of the dissatisfaction memory of a household.",
            "If the total dissatisfaction exceeds this value, the household moves.",
            "The number of immigrants per turn."
        };
        int spinnerNumber = labels.length;
        
        SpinnerModel yearModel = new SpinnerNumberModel(2000,1900,2500,1);
        JSpinner spinner = addLabeledSpinner(this, labels[0], tooltips[0], yearModel);
        spinners.put("year", spinner);
        
        SpinnerModel turnsModel = new SpinnerNumberModel(1,1,1000,1);
        spinner = addLabeledSpinner(this, labels[1], tooltips[1], turnsModel);
        spinners.put("numberOfTurns", spinner);
        
        SpinnerModel bufferModel = new SpinnerNumberModel(10.00,0.01,100.00,0.01);
        spinner = addLabeledSpinner(this, labels[2], tooltips[2], bufferModel);
        spinners.put("bufferSize", spinner);
        
        SpinnerModel weightingModel1 = new SpinnerNumberModel(1.00,0.00,10.00,0.01);
        SpinnerModel weightingModel2 = new SpinnerNumberModel(1.00,0.00,10.00,0.01);
        SpinnerModel weightingModel3 = new SpinnerNumberModel(1.00,0.00,10.00,0.01);
        spinner = addLabeledSpinner(this, labels[3], tooltips[3], weightingModel1);
        spinners.put("amenitiesWeighting", spinner);
        spinner = addLabeledSpinner(this, labels[4], tooltips[4], weightingModel2);
        spinners.put("constructibilityWeighting", spinner);
        spinner = addLabeledSpinner(this, labels[5], tooltips[5], weightingModel3);
        spinners.put("idealhousingWeighting", spinner);
                
        SpinnerModel gaussModel = new SpinnerNumberModel(0.10,0.01,1.00,0.01);
        spinner = addLabeledSpinner(this, labels[6], tooltips[6], gaussModel);
        spinners.put("gaussDeviation", spinner);
        
        ButtonGroup choices = new ButtonGroup();
        
        JRadioButton statisticalButton = new JRadioButton("Statistical Model", true);
        statisticalButton.setToolTipText(tooltips[7]);
        statisticalButton.addActionListener(this);
        statisticalButton.setActionCommand("statistical");
        this.add(statisticalButton);
        choices.add(statisticalButton);
        selections.put("statistical", statisticalButton);
        
        JRadioButton schellingButton = new JRadioButton("Schelling Model", false);
        schellingButton.setToolTipText(tooltips[8]);
        schellingButton.addActionListener(this);
        schellingButton.setActionCommand("schelling");
        this.add(schellingButton);
        choices.add(schellingButton);
        selections.put("schelling", schellingButton);
        
        SpinnerModel memoryModel = new SpinnerNumberModel(5,1,50,1);
        spinner = addLabeledSpinner(this, labels[11], tooltips[11], memoryModel);
        spinners.put("householdMemory", spinner);
        
        SpinnerModel movingModel = new SpinnerNumberModel(20.00,0.01,200.0,0.01);
        spinner = addLabeledSpinner(this, labels[12], tooltips[12], movingModel);
        spinners.put("movingThreshold", spinner);
        
        SpinnerModel segThresholdModel = new SpinnerNumberModel(0.80,0.01,1.00,0.01);
        spinner = addLabeledSpinner(this, labels[9], tooltips[9], segThresholdModel);
        spinner.setEnabled(false);
        spinners.put("segregationThreshold", spinner);
        
        SpinnerModel segToleranceModel = new SpinnerNumberModel(0.30,0.01,1.00,0.01);
        spinner = addLabeledSpinner(this, labels[10], tooltips[10], segToleranceModel);
        spinner.setEnabled(false);
        spinners.put("segregationTolerance", spinner);
        
        SpinnerModel immigrantModel = new SpinnerNumberModel(5000,0,200000,1);
        spinner = addLabeledSpinner(this, labels[13], tooltips[13], immigrantModel);
        spinners.put("immigrantNumber", spinner);
       
        SpringUtilities.makeCompactGrid(this,spinnerNumber-1,2,10,10,6,10);
    }
    
    private static JSpinner addLabeledSpinner(Container c, String label, String tooltip, SpinnerModel model) {
        JLabel jl = new JLabel(label);
        c.add(jl);
        
        JSpinner spinner = new JSpinner(model);
        jl.setLabelFor(spinner);
        jl.setToolTipText(tooltip);
        c.add(spinner);
        
        return spinner;
    }
    
    public Map<String, JSpinner> getSpinners() {
        return spinners;
    }    
    
    public Map<String, JRadioButton> getSelections() {
        return selections;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("schelling")) {
            spinners.get("segregationThreshold").setEnabled(true);
            spinners.get("segregationTolerance").setEnabled(true);
            spinners.get("movingThreshold").setEnabled(false);
            spinners.get("householdMemory").setEnabled(false);
        }
        else {
            spinners.get("segregationThreshold").setEnabled(false);
            spinners.get("segregationTolerance").setEnabled(false);
            spinners.get("movingThreshold").setEnabled(true);
            spinners.get("householdMemory").setEnabled(true);
        }
    }
}
