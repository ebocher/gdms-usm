/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Thomas Salliou
 */
public class GdmsFileFilter extends FileFilter {

    GdmsFileFilter() {
        //TGV
    }

    @Override
    public boolean accept(File f) {
        return f.getAbsolutePath().endsWith(".gdms") || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "GDMS config file (.gdms)";
    }
}
