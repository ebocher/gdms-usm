/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;

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
    
    public LaunchFrame(File configFile, String choice) throws DataSourceCreationException, DriverException {
        super("Urban Sprawl Model - Launch");
        modelChoice = choice;
        
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
        JButton launchButton = new JButton("Launch");
        buttonPanel.add(launchButton);
        JButton modifyButton = new JButton("Modify");
        buttonPanel.add(modifyButton);
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
