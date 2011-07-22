/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.usm.plugin;

import org.orbisgis.core.ui.pluginSystem.Extension;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;

/**
 *
 * @author Thomas Salliou
 */
public class UsmExtension extends Extension{

	@Override
	public void configure(PlugInContext context) throws Exception {
		new UsmPlugIn().initialize(context);	
	}

}

