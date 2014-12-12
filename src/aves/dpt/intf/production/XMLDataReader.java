package aves.dpt.intf.production;

import java.util.List;

import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * Objects implementing this interface retrieve DOCUMENTS with specific tags from the 
 * {@link src/main/resource/DOCUMENTS/avesdatasrc.xml} file
 *
 * @author svlieffe
 * 2012/03/29
 */
public interface XMLDataReader {
    
    /**
     * Sets the JOURNEYS or place to which the
     * retrieved XML items belong
     * @param itemGroupName 
     */
    void setGroup(String itemGroupName);
    
    /**
     * Sets the type of the items
     * to be retrieved
     * @param objectType 
     */
    void setType(AvesObjectType objectType);   
    
    /**
     * Reads the DOCUMENTS from the source
     * @param avesObjectType 
     */
    void read();
     
    /** 
     * Returns the list of DOCUMENTS values
     * @return List<String>
     */
     List<String> valueList();
}
