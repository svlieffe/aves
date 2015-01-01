/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.intf.viewers;

/**
 *
 * Generates an error when the program cannot find the source DOCUMENTS
 * of the requested {@link aves.dpt.intf.production.AvesObject}
 * 
 * @author svlieffe
 * 2012/03/29
 */
public class DataNotFoundException extends Exception {
    private static final long serialVerstionUID = 1L;
    
    public DataNotFoundException(String message) {
        super(message);
    }
    
}
