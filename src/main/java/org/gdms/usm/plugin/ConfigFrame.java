/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Thomas Salliou
 */
public class ConfigFrame extends JFrame implements ActionListener {
    
    public ConfigFrame() {
        
        super("Urban Sprawl Model - Configuration");
        JPanel chooserPanel = new JPanel(new BorderLayout(20, 20));
        
        //Labels
        JLabel welcome = new JLabel("<html>Welcome to the Urban Sprawl Model simulation plugin.<br />You'll need a config file to run it :</html>");
        JLabel create = new JLabel("Create a new config file");
        JLabel or = new JLabel("OR",JLabel.CENTER);
        JLabel choose = new JLabel("Choose an existing one");
        
        //Buttons
        JButton createButton = new JButton("Create");
        createButton.setActionCommand("create");
        createButton.addActionListener(this);
        JButton chooseButton = new JButton("Choose");
        chooseButton.setActionCommand("choose");
        chooseButton.addActionListener(this);
        JButton cancelButton = new JButton("Return to OrbisGIS");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        
        //Layout
        JPanel createPart = new JPanel(new BorderLayout(10,10));
        createPart.add(create, BorderLayout.NORTH);
        createPart.add(createButton, BorderLayout.SOUTH);
        JPanel choosePart = new JPanel(new BorderLayout(10,10));
        choosePart.add(choose, BorderLayout.NORTH);
        choosePart.add(chooseButton, BorderLayout.SOUTH);
        chooserPanel.add(choosePart, BorderLayout.EAST);
        chooserPanel.add(or, BorderLayout.CENTER);
        chooserPanel.add(createPart, BorderLayout.WEST);
        chooserPanel.add(welcome,BorderLayout.NORTH);
        chooserPanel.add(cancelButton, BorderLayout.SOUTH);
        chooserPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        add(chooserPanel);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("create".equals(e.getActionCommand())) {
            new CreateFrame();
            dispose();
        }
        else if ("choose".equals(e.getActionCommand())) {
            new ChooseFrame();
            dispose();
        }
        else {
            dispose();
        }
    }
}
