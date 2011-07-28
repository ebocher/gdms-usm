/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.SourceManager;
import org.gdms.usm.BufferBuildTypeCalculator;
import org.gdms.usm.GaussParcelSelector;
import org.gdms.usm.SchellingDecisionMaker;
import org.gdms.usm.StatisticalDecisionMaker;
import org.gdms.usm.Step;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;

/**
 *
 * @author Thomas Salliou
 */
public class LaunchFrame extends JFrame implements ActionListener {

    private String modelChoice;
    private JFileChooser dataFc;
    private JTextField dataPath;
    private JFileChooser outputFc;
    private JTextField outputPath;
    private String configPath;
    private JButton launchButton;
    private JButton modifyButton;
    private JButton cancelButton;
    
    public LaunchFrame(File configFile, String choice) throws DataSourceCreationException, DriverException {
        super("Urban Sprawl Model - Launch");
        modelChoice = choice;
        configPath = configFile.getAbsolutePath();
        
        //Check panel
        ReadParameterPanel rpp = new ReadParameterPanel(configFile, choice);
        add(rpp, BorderLayout.NORTH);
        
        //Path selecting panel
        JPanel browsePanel = new JPanel(new BorderLayout());
        
        //Data file chooser
        dataFc = new JFileChooser();
        DataManager dm = Services.getService(DataManager.class);
        SourceManager sourceManager = dm.getSourceManager();
        DriverManager driverManager = sourceManager.getDriverManager();
        Driver[] filtered = driverManager.getDrivers(new FileDriverFilter());
        for (int i = 0; i < filtered.length; i++) {
                FileDriver fileDriver = (FileDriver) filtered[i];
                String[] extensions = fileDriver.getFileExtensions();
                dataFc.addChoosableFileFilter(new FormatFilter(extensions,fileDriver.getTypeDescription()));
        }
        dataPath = new JTextField("Please choose initial data", 35);
        dataPath.setEditable(false);
        JButton dataBrowseButton = new JButton("Browse...");
        dataBrowseButton.setActionCommand("dataBrowse");
        dataBrowseButton.addActionListener(this);
        JPanel dataBrowseBar = new JPanel();
        dataBrowseBar.add(dataPath); 
        dataBrowseBar.add(dataBrowseButton);
        
        //Output directory chooser
        outputFc = new JFileChooser();
        outputFc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputPath = new JTextField("Please choose output data destination directory", 35);
        outputPath.setEditable(false);
        JButton outputBrowseButton = new JButton("Browse...");
        outputBrowseButton.setActionCommand("outputBrowse");
        outputBrowseButton.addActionListener(this);
        JPanel outputBrowseBar = new JPanel();
        outputBrowseBar.add(outputPath);
        outputBrowseBar.add(outputBrowseButton);

        browsePanel.add(dataBrowseBar, BorderLayout.NORTH);
        browsePanel.add(outputBrowseBar, BorderLayout.SOUTH);
        add(browsePanel, BorderLayout.CENTER);
        
        //Button panel
        JPanel buttonPanel = new JPanel();
        launchButton = new JButton("Launch");
        launchButton.addActionListener(this);
        launchButton.setActionCommand("launch");
        buttonPanel.add(launchButton);
        modifyButton = new JButton("Modify");
        modifyButton.addActionListener(this);
        modifyButton.setActionCommand("modify");
        buttonPanel.add(modifyButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("cancel");
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("dataBrowse")) {
            int val = dataFc.showDialog(this, "Choose data");
            if (val == JFileChooser.APPROVE_OPTION) {
                dataPath.setText(dataFc.getSelectedFile().getAbsolutePath());
            }
        }
        else if(e.getActionCommand().equals("outputBrowse")) {
            int val = outputFc.showDialog(this, "Choose destination");
            if (val == JFileChooser.APPROVE_OPTION) {
                outputPath.setText(outputFc.getSelectedFile().getAbsolutePath());
            }
        }
        else if(e.getActionCommand().equals("launch")) {
            BufferBuildTypeCalculator bbtc = new BufferBuildTypeCalculator();
            GaussParcelSelector gps = new GaussParcelSelector();
            Step s;
            if(modelChoice.equals("schelling")) {
                SchellingDecisionMaker dm = new SchellingDecisionMaker();
                s = new Step(2000, dataPath.getText(), configPath, outputPath.getText(), bbtc, dm, gps);
            }
            else {
                StatisticalDecisionMaker dm = new StatisticalDecisionMaker();
                s = new Step(2000, dataPath.getText(), configPath, outputPath.getText(), bbtc, dm, gps);
            }
            new ProgressFrame(s);
            launchButton.setEnabled(false);
            modifyButton.setEnabled(false);
            cancelButton.setEnabled(false);
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            bm.backgroundOperation(new ExecuteSimulation(s));
        }
        else if(e.getActionCommand().equals("modify")) {
            System.out.println("Config modification, not implemented yet.");
        }
        else {
            new ConfigFrame();
            dispose();
        }
    }
}
