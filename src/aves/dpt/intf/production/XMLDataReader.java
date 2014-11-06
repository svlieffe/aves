/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.production;

import java.util.List;

import aves.dpt.intf.production.AvesObject.AvesObjectType;
/**
 * Objects implementing this interface retrieve DATA with specific tags from the 
 * {@link src/main/resource/DATA/avesdatasrc.xml} file
 *
 * @author stefaanvanliefferinge
 * @version $Id: XMLDataReader.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface XMLDataReader {
    
    /**
     * sets the SESSIONS or place to which the
     * retrieved XML items belong
     * @param itemGroupName 
     */
    void setGroup(String itemGroupName);
    
    /**
     * sets the type of the items
     * to be retrieved
     * @param objectType 
     */
    void setType(AvesObjectType objectType);   
    
    /**
     * reads the DATA from the source
     * @param avesObjectType 
     */
    void read();
     
    /** 
     * returns the list of DATA values
     * @return List<String>
     */
     List<String> valueList();
}
