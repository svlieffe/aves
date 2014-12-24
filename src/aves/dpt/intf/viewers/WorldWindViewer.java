package aves.dpt.intf.viewers;

import java.awt.Dimension;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;

/**
 *
 * Objects implementing this interface 
 * customize the {@link gov.nasa.worldwind} user interface. 
 *
 * @author svlieffe
 * 2012/03/29
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
     * @return worldWindPanel
     */
    WorldWindowGLJPanel getwwPanel();
}
