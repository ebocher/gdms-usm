/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.gdms.usm.Step;
import org.gdms.usm.StepListener;

/**
 *
 * @author Thomas Salliou
 */
public class ProgressFrame extends JFrame implements StepListener {
    
    private int totalSeconds;
    private JLabel currentTurn;
    private Step simulation;
    
    public ProgressFrame(Step s) {
        super("Progress");
        this.setLayout(new BorderLayout(20,20));
        simulation = s;
        s.registerStepListener(this);
        
        //Time elapsed panel
        JPanel timePanel = new JPanel(new BorderLayout(5,5));
        final JLabel timeLabel = new JLabel("00:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setFont(new Font("Serif", Font.BOLD, 30));
        timePanel.add(timeLabel, BorderLayout.SOUTH);
        JLabel elapsed = new JLabel("Time Elapsed :");
        timePanel.add(elapsed, BorderLayout.NORTH);
        add(timePanel, BorderLayout.WEST);
        
        ActionListener timerListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                totalSeconds++;
                int minutes = totalSeconds/60;
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
                timeLabel.setText(minutess+":"+secondss);
            }
        };
        Timer timer = new Timer(1000, timerListener);
        timer.start();
        
        //Turn progress panel
        int maxTurn = simulation.getManager().getNumberOfTurns();
        JPanel turnPanel = new JPanel(new BorderLayout(5,5));
        JLabel turnLabel = new JLabel("Current Step :");
        turnPanel.add(turnLabel, BorderLayout.NORTH);
        currentTurn = new JLabel("Init");
        currentTurn.setHorizontalAlignment(SwingConstants.CENTER);
        currentTurn.setFont(new Font("Serif", Font.BOLD, 30));
        turnPanel.add(currentTurn, BorderLayout.SOUTH);
        add(turnPanel, BorderLayout.EAST);
        
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void nextTurn() {
        currentTurn.setText(simulation.getStepNumber()+"/"+simulation.getManager().getNumberOfTurns());
    }
}
