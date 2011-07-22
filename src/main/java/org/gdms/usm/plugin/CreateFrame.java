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
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.gdms.GdmsWriter;
import org.orbisgis.core.Services;

/**
 *
 * @author Thomas Salliou
 */
public class CreateFrame extends JFrame implements ActionListener {

    private SpinnerParameterPanel spp;
    private JFileChooser fc;
    private JTextField path;

    public CreateFrame() {

        super("Urban Sprawl Model - Create");
        spp = new SpinnerParameterPanel();

        //Spinner panel
        add(spp, BorderLayout.NORTH);

        //File chooser
        fc = new JFileChooser();
        fc.setFileFilter(new GdmsFileFilter());

        //Path text field
        path = new JTextField("Please select a target file", 25);
        path.setEditable(false);

        //Button labels
        JButton create = new JButton("Create");
        JButton cancel = new JButton("Cancel");
        JButton browse = new JButton("Browse...");

        //Button actions
        create.setActionCommand("create");
        cancel.setActionCommand("cancel");
        browse.setActionCommand("browse");
        create.addActionListener(this);
        cancel.addActionListener(this);
        browse.addActionListener(this);

        //Button tooltips
        create.setToolTipText("Creates a config file with the given parameters.");
        cancel.setToolTipText("Cancels and returns to the selection panel.");
        browse.setToolTipText("Select a target location, existing or not.");

        //Browse path panel
        JPanel browsePanel = new JPanel();
        browsePanel.add(path);
        browsePanel.add(browse);

        //Button panel, flow layout is efficient here
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(create);
        buttonPanel.add(cancel);

        add(buttonPanel, BorderLayout.SOUTH);
        add(browsePanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("create".equals(e.getActionCommand())) {
            if (fc.getSelectedFile() == null || !fc.getSelectedFile().getAbsolutePath().endsWith(".gdms")) {
                JOptionPane.showMessageDialog(this, "Please select or create a valid GDMS file.", "Invalid file", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    File globals = fc.getSelectedFile().getAbsoluteFile();

                    //Table creation
                    GdmsWriter globalsGW = new GdmsWriter(globals);
                    String[] fieldNames1 = {"bufferSize",
                        "amenitiesWeighting",
                        "constructibilityWeighting",
                        "idealhousingWeighting",
                        "gaussDeviation",
                        "segregationThreshold",
                        "segregationTolerance",
                        "householdMemory",
                        "movingThreshold",
                        "immigrantNumber",
                        "numberOfTurns",
                        "year"};
                    Type integ = TypeFactory.createType(64);
                    Type doubl = TypeFactory.createType(16);
                    Type[] fieldTypes1 = {doubl, doubl, doubl, doubl, doubl, doubl, doubl, integ, doubl, integ, integ, integ};
                    Metadata m1 = new DefaultMetadata(fieldTypes1, fieldNames1);
                    globalsGW.writeMetadata(0, m1);

                    //Table filling
                    Map<String, JSpinner> sp = spp.getSpinners();
                    globalsGW.addValues(new Value[]{ValueFactory.createValue((Double) sp.get("bufferSize").getValue()),
                                ValueFactory.createValue((Double) sp.get("amenitiesWeighting").getValue()),
                                ValueFactory.createValue((Double) sp.get("constructibilityWeighting").getValue()),
                                ValueFactory.createValue((Double) sp.get("idealhousingWeighting").getValue()),
                                ValueFactory.createValue((Double) sp.get("gaussDeviation").getValue()),
                                ValueFactory.createValue((Double) sp.get("segregationThreshold").getValue()),
                                ValueFactory.createValue((Double) sp.get("segregationTolerance").getValue()),
                                ValueFactory.createValue((Integer) sp.get("householdMemory").getValue()),
                                ValueFactory.createValue((Double) sp.get("movingThreshold").getValue()),
                                ValueFactory.createValue((Integer) sp.get("immigrantNumber").getValue()),
                                ValueFactory.createValue((Integer) sp.get("numberOfTurns").getValue()),
                                ValueFactory.createValue((Integer) sp.get("year").getValue())
                            });

                    //Table closing
                    globalsGW.writeRowIndexes();
                    globalsGW.writeExtent();
                    globalsGW.writeWritenRowCount();
                    globalsGW.close();

                    try {
                        if (spp.getSelections().get("statistical").isSelected()) {
                            new LaunchFrame(fc.getSelectedFile(), "statistical");
                        } else if (spp.getSelections().get("schelling").isSelected()) {
                            new LaunchFrame(fc.getSelectedFile(), "schelling");
                        }
                    } catch (DriverException ex) {
                        Services.getErrorManager().error("Driver Exception", ex);
                        JOptionPane.showMessageDialog(this, "Some driver error has occurred.", "Driver Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    } catch (DataSourceCreationException ex) {
                        Services.getErrorManager().error("DataSourceCreation Exception", ex);
                        JOptionPane.showMessageDialog(this, "Some DataSource creation error has occurred.", "DataSource Creation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    dispose();
                } catch (IOException ex) {
                    Services.getErrorManager().error("I/O Exception", ex);
                    JOptionPane.showMessageDialog(this, "Some I/O error has occurred.", "I/O Error", JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (DriverException ex) {
                    Services.getErrorManager().error("Driver Exception", ex);
                    JOptionPane.showMessageDialog(this, "Some driver error has occurred.", "Driver Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

            }
        } else if ("browse".equals(e.getActionCommand())) {
            int value = fc.showSaveDialog(this);
            if (value == JFileChooser.APPROVE_OPTION) {
                if (!fc.getSelectedFile().getAbsolutePath().endsWith(".gdms")) {
                    fc.setSelectedFile(new File(fc.getSelectedFile().getAbsolutePath() + ".gdms"));
                }
                path.setText(fc.getSelectedFile().getAbsolutePath());
            }
        } else {
            new ConfigFrame();
            dispose();
        }

    }
}
