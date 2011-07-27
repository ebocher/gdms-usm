/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.DriverException;
import org.gdms.usm.Step;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author Thomas Salliou
 */
class ExecuteSimulation implements BackgroundJob {

    private Step simulation;
    
    public ExecuteSimulation(Step s) {
        simulation = s;
    }

    @Override
    public void run(ProgressMonitor pm) {
        try {
            simulation.wholeSimulation();
        } catch (NoSuchTableException ex) {
            Logger.getLogger(ExecuteSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataSourceCreationException ex) {
            Logger.getLogger(ExecuteSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverException ex) {
            Logger.getLogger(ExecuteSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonEditableDataSourceException ex) {
            Logger.getLogger(ExecuteSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExecuteSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IndexException ex) {
            Logger.getLogger(ExecuteSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getTaskName() {
        return "USM Simulation";
    }
    
}
