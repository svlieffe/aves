/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.production;

import java.util.List;

import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * Classes implementing this interface extract the source data 
 * in the {@link src/main/resource/data/avesdatasrc.xml} for the 
 * {@link aves.dpt.intf.production.Factory} that 
 * manages how {@link aves.dpt.intf.production.AvesObject}s
 *  are added to the list that is returned to the 
 * {@link aves.dpt.intf.ctrl.AvesManager}.
 *
 * @author stefaanvanliefferinge
 * @version $Id: DataRetriever.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface DataRetriever {
    
    /**
     * Sets the {@link aves.dpt.intf.production.AvesObject.AvesObjectType}
     * of data to retrieve
     * @param avesObjectType 
     */
    void setDataType(AvesObjectType avesObjectType);
    
    /**
     * sets the name of the dataset to be retrieved from the data source file.
     * This name is requested by the user selecting an object in the 
     * {@link aves.dpt.intf.viewers.AvesViewer}.
     * 
     * @param name 
     */
    public void setGroupName(String name);
   
    /** 
     * gets the data from the data source
     * @return 
     */
    List<String> getData();
    
}
