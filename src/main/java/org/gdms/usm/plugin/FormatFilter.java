/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author alexis
 */
public final class FormatFilter extends FileFilter {

    private final String[] extensions;
    private String description;

    public FormatFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description + " (";
        String separator = "";
        for (String extension : extensions) {
            this.description += separator + "*." + extension;
            separator = ",";
        }
        this.description += ")";
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File f) {
        if (f == null) {
            return true;
        } else {
            for (String extension : extensions) {
                if (f.getAbsolutePath().toLowerCase().endsWith(
                        "." + extension.toLowerCase())
                        || f.isDirectory()) {
                    return true;
                }
            }
            return false;
        }
    }
}
