/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

import java.awt.Dimension;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;

/**
 *
 * Objects implementing this interface 
 * customize the {@link gov.nasa.worldwind} user interface. 
 *
 * @author stefaanvanliefferinge
 * @version $Id: WorldWindViewer.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
*/
public interface WorldWindViewer {
    /**
     * Customizes {@link gov.nasa.worldwind.awt.WorldWindowGLJPanel} 
     * using the {@link gov.nasa.worldwind} API.
     * 
     * @param dim 
     */
    void initComponents(Dimension dim);

    /**
     * Gets the customized {@link gov.nasa.worldwind.awt.WorldWindowGLJPanel}
     */
    WorldWindowGLJPanel getwwPanel();
}
