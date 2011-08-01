/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.gdms.usm.Household;
import org.gdms.usm.Step;
import org.gdms.usm.StepListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.orbisgis.core.Services;

/**
 *
 * @author Thomas Salliou
 */
public class ProgressFrame extends JFrame implements StepListener {
    
    private int totalSeconds;
    private List<Integer> stepSeconds;
    private JLabel currentTurn;
    private JLabel currentPopulation;
    private JLabel initialPopulationCount;
    private JLabel lastDeathToll;
    private JLabel lastNewbornCount;
    private JLabel lastMoversCount;
    private XYSeries timeChart;
    private XYSeries currentPopulationChart;
    private XYSeries deathTollChart;
    private XYSeries newbornCountChart;
    private XYSeries moversCountChart;
    private Step simulation;
    
    public ProgressFrame(Step s) {
        super("Progress");
        simulation = s;
        s.registerStepListener(this);
        stepSeconds = new LinkedList<Integer>();
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        JPanel globalPanel = new JPanel(new SpringLayout());
        
        //Time elapsed panel
        JPanel timePanel = new JPanel(new BorderLayout(5,5));
        final JLabel timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Serif", Font.BOLD, 45));
        timePanel.add(timeLabel, BorderLayout.SOUTH);
        JLabel elapsed = new JLabel("Time Elapsed :",SwingConstants.CENTER);
        timePanel.add(elapsed, BorderLayout.NORTH);
        statusPanel.add(timePanel, BorderLayout.NORTH);
        
        ActionListener timerListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                totalSeconds++;
                int hours = totalSeconds/3600;
                String hourss;
                if (hours < 10) {
                    hourss = "0"+hours;
                } else {
                    hourss = ""+hours;
                }
                int minutes = (totalSeconds%3600)/60;
                String minutess;
                if (minutes < 10) {
                    minutess = "0"+minutes;
                } else {
                    minutess = ""+minutes;
                }
                int seconds = totalSeconds%60;
                String secondss;
                if (seconds < 10) {
                    secondss = "0"+seconds;
                } else {
                    secondss = seconds+"";
                }
                timeLabel.setText(hourss+":"+minutess+":"+secondss);
            }
        };
        Timer timer = new Timer(1000, timerListener);
        timer.start();
        
        //Turn progress panel
        JPanel turnPanel = new JPanel(new BorderLayout(5,5));
        JLabel turnLabel = new JLabel("Current Step :",SwingConstants.CENTER);
        turnPanel.add(turnLabel, BorderLayout.NORTH);
        currentTurn = new JLabel("Init", SwingConstants.CENTER);
        currentTurn.setFont(new Font("Serif", Font.BOLD, 30));
        turnPanel.add(currentTurn, BorderLayout.SOUTH);
        globalPanel.add(turnPanel);
        
        //Movers panel
        JPanel moversPanel = new JPanel(new BorderLayout(5, 5));
        JLabel moversLabel = new JLabel("Last movers count :",SwingConstants.CENTER);
        moversPanel.add(moversLabel, BorderLayout.NORTH);
        lastMoversCount = new JLabel("Init", SwingConstants.CENTER);
        lastMoversCount.setFont(new Font("Serif", Font.BOLD, 30));
        moversPanel.add(lastMoversCount, BorderLayout.SOUTH);
        globalPanel.add(moversPanel);
        
        //Initial population panel
        JPanel initPopPanel = new JPanel(new BorderLayout(5,5));
        JLabel initialPopulationLabel = new JLabel("Initial population :",SwingConstants.CENTER);
        initPopPanel.add(initialPopulationLabel, BorderLayout.NORTH);
        initialPopulationCount = new JLabel("Init",SwingConstants.CENTER);
        initialPopulationCount.setFont(new Font("Serif", Font.BOLD, 30));
        initPopPanel.add(initialPopulationCount, BorderLayout.SOUTH);
        globalPanel.add(initPopPanel);
        
        //Current population panel
        JPanel curPopPanel = new JPanel(new BorderLayout(5,5));
        JLabel currentPopulationLabel = new JLabel("Current population :",SwingConstants.CENTER);
        curPopPanel.add(currentPopulationLabel, BorderLayout.NORTH);
        currentPopulation = new JLabel("Init",SwingConstants.CENTER);
        currentPopulation.setFont(new Font("Serif", Font.BOLD, 30));
        curPopPanel.add(currentPopulation, BorderLayout.SOUTH);
        globalPanel.add(curPopPanel);
        
        //Dead panel
        JPanel deadPanel = new JPanel(new BorderLayout(5,5));
        JLabel deadLabel = new JLabel("Last death toll :", SwingConstants.CENTER);
        deadPanel.add(deadLabel, BorderLayout.NORTH);
        lastDeathToll = new JLabel("Init", SwingConstants.CENTER);
        lastDeathToll.setFont(new Font("Serif", Font.BOLD, 30));
        deadPanel.add(lastDeathToll, BorderLayout.SOUTH);
        globalPanel.add(deadPanel);
        
        //Newborn panel
        JPanel newbornPanel = new JPanel(new BorderLayout(5,5));
        JLabel newbornLabel = new JLabel("Last newborn count :", SwingConstants.CENTER);
        newbornPanel.add(newbornLabel, BorderLayout.NORTH);
        lastNewbornCount = new JLabel("Init", SwingConstants.CENTER);
        lastNewbornCount.setFont(new Font("Serif", Font.BOLD, 30));
        newbornPanel.add(lastNewbornCount, BorderLayout.SOUTH);
        globalPanel.add(newbornPanel);
        
        SpringUtilities.makeCompactGrid(globalPanel, 3, 2, 5, 5, 20, 10);
        statusPanel.add(globalPanel, BorderLayout.SOUTH);
        
        add(statusPanel, BorderLayout.WEST);
        
        //Graph tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        timeChart = new XYSeries("Step time", true, false);
        tabbedPane.addTab("Step time", createChartPanel("Step time", timeChart));
        currentPopulationChart = new XYSeries("Population", true, false);
        tabbedPane.addTab("Population", createChartPanel("Population", currentPopulationChart));
        deathTollChart = new XYSeries("Deaths", true, false);
        tabbedPane.addTab("Deaths", createChartPanel("Deaths", deathTollChart));
        newbornCountChart = new XYSeries("Newborn", true, false);
        tabbedPane.addTab("Newborn", createChartPanel("Newborn", newbornCountChart));
        moversCountChart = new XYSeries("Movers", true, false);
        tabbedPane.addTab("Movers", createChartPanel("Movers", moversCountChart));
        add(tabbedPane, BorderLayout.EAST);
        
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void nextTurn(int cT, int nT, int pop, int dead, int newb, int mov) {
        //Labels updating
        currentTurn.setText(cT+"/"+nT);
        currentPopulation.setText(""+pop);
        lastDeathToll.setText(""+dead);
        lastNewbornCount.setText(""+newb);
        lastMoversCount.setText(""+mov);
        
        //Charts updating
        //NB : data is recorded at current turn minus one, because this is last-turn data.
        if (cT > 1) {
            timeChart.add(cT-1, totalSeconds-stepSeconds.get(cT-1));
            currentPopulationChart.add(cT-1, pop);
            deathTollChart.add(cT-1, dead);
            newbornCountChart.add(cT-1, newb);
            moversCountChart.add(cT-1, mov);
        }    
        stepSeconds.add(totalSeconds);
    }

    @Override
    public void householdDisappeared(Household h) {
        Services.getOutputManager().println("Warning : household number "+h.getId()+" did not find any suitable parcel. It moved out of the city.");
    }

    @Override
    public void initializationDone() {
        initialPopulationCount.setText(""+simulation.getManager().getPopulation());
        stepSeconds.add(totalSeconds);
        currentTurn.setText("1/"+simulation.getManager().getNumberOfTurns());
    }
    
    private ChartPanel createChartPanel(String valueName, XYSeries data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);
        JFreeChart chart = ChartFactory.createXYLineChart(null, "Step", valueName, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.removeLegend();
        return new ChartPanel(chart);
    }
}
