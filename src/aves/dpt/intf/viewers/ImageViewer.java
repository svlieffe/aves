/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

import java.awt.image.BufferedImage;

/**
 * 
 * Objects implementing this interface render images.
 *  
 * <p>
 * @author stefaanvanliefferinge
 */
public interface ImageViewer {
       
    /**
     * 
     * Sets the image to display.
     */
    void setCurrentImage(BufferedImage image);
}
