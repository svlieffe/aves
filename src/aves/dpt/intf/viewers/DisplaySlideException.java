/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.intf.viewers;

/**
 *
 * @author svlieffe
 * 2012/03/29
 */

public class DisplaySlideException extends Exception {

    public DisplaySlideException(String message) {
        super("Error displaying next slide: " + message);
    }
}
