/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
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
     * @param image
     */
    void setCurrentImage(BufferedImage image);
}
