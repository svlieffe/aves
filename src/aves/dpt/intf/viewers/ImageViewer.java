package aves.dpt.intf.viewers;

import java.awt.image.BufferedImage;

/**
 * 
 * Objects implementing this interface render images.
 *  
 * @author svlieffe
 * 2012/03/29
 */
public interface ImageViewer {
       
    /**
     * 
     * Sets the image to display.
     */
    void setCurrentImage(BufferedImage image);
}
