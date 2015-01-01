/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.intf.production;

import java.util.List;

import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * 
 * Objects implementing this interface retrieve DOCUMENTS with specific tags from the 
 * aves/Journeys/avesJourneys.xml file
 *
 * @author svlieffe
 * 2012/03/29
 */
public interface XMLDataReader {
    
    /**
     * 
     * Sets the JOURNEYS or place to which the
     * retrieved XML items belong
     * @param itemGroupName 
     */
    void setGroup(String itemGroupName);
    
    /**
     * 
     * Sets the type of the items
     * to be retrieved
     * @param objectType 
     */
    void setType(AvesObjectType objectType);   
    
    /**
     * 
     * Reads the DOCUMENTS from the source
     *  
     */
    void read();
     
    /**
     *  
     * Returns the list of DOCUMENTS values
     * @return valueList
     */
     List<String> valueList();
}
