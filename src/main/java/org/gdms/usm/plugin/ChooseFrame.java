/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;

/**
 *
 * @author Thomas Salliou
 */
public class ChooseFrame extends JFrame implements ActionListener {

    private JFileChooser fc;
    private JTextField path;
    private Map<String, JRadioButton> selections;

    public ChooseFrame() {

        super("Urban Sprawl Model - Import a config file");
        selections = new HashMap<String, JRadioButton>();
        
        //File chooser
        fc = new JFileChooser();
        fc.setFileFilter(new GdmsFileFilter());

        //Buttons
        JButton importButton = new JButton("Import");
        importButton.setActionCommand("import");
        importButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        JButton browseButton = new JButton("Browse...");
        browseButton.setActionCommand("browse");
        browseButton.addActionListener(this);

        //Radio choice
        ButtonGroup choices = new ButtonGroup();

        JRadioButton statisticalButton = new JRadioButton("Statistical Model", true);
        statisticalButton.setToolTipText("Selects the statistical model as the moving out model of the simulation.");
        choices.add(statisticalButton);
        selections.put("statistical", statisticalButton);

        JRadioButton schellingButton = new JRadioButton("Schelling Model", false);
        schellingButton.setToolTipText("Selects the Schelling model as the moving out model.");
        choices.add(schellingButton);
        selections.put("schelling", schellingButton);

        //Path text field
        path = new JTextField("Please choose a valid GDMS config file", 35);
        path.setEditable(false);

        //Layout
        JPanel browsePart = new JPanel();
        browsePart.add(path);
        browsePart.add(browseButton);
        add(browsePart, BorderLayout.NORTH);
        JPanel actionPart = new JPanel();
        actionPart.add(importButton);
        actionPart.add(cancelButton);
        add(actionPart, BorderLayout.SOUTH);
        JPanel selectPart = new JPanel();
        selectPart.add(statisticalButton);
        selectPart.add(schellingButton);
        add(selectPart, BorderLayout.CENTER);

        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("browse".equals(e.getActionCommand())) {
            int val = fc.showDialog(this, "Choose");
            if (val == JFileChooser.APPROVE_OPTION) {
                path.setText(fc.getSelectedFile().getAbsolutePath());
            }
        } else if ("import".equals(e.getActionCommand())) {
            if (fc.getSelectedFile() == null || !fc.getSelectedFile().getAbsolutePath().endsWith(".gdms")) {
                JOptionPane.showMessageDialog(this, "Please select a valid GDMS file.", "Invalid file", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    if (selections.get("statistical").isSelected()) {
                        new LaunchFrame(fc.getSelectedFile(), "statistical");
                    } else if (selections.get("schelling").isSelected()) {
                        new LaunchFrame(fc.getSelectedFile(), "schelling");
                    }
                    dispose();
                } catch (DriverException ex) {
                    Services.getErrorManager().error("Driver Exception", ex);
                    JOptionPane.showMessageDialog(this, "Some driver error has occurred.", "Driver Error", JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (DataSourceCreationException ex) {
                    Services.getErrorManager().error("DataSourceCreation Exception", ex);
                    JOptionPane.showMessageDialog(this, "Some DataSource creation error has occurred.", "DataSource Creation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } else {
            new ConfigFrame();
            dispose();
        }
    }
}
