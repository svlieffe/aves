package aves.dpt.intf.production;

import java.util.List;

import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * 
 * Classes implementing this interface extract the source DOCUMENTS 
 * in the aves/Journeys/avesJourneys.xml file for the 
 * {@link aves.dpt.intf.production.Factory} that 
 * manages how {@link aves.dpt.intf.production.AvesObject}s
 *  are added to the list that is returned to the 
 * {@link aves.dpt.intf.ctrl.AvesManager}.
 *
 * @author svlieffe
 * 2012/03/29
 */
public interface DataRetriever {
    
    /**
     * 
     * Sets the {@link aves.dpt.intf.production.AvesObject.AvesObjectType}
     * of DOCUMENTS to retrieve
     * @param avesObjectType 
     */
    void setDataType(AvesObjectType avesObjectType);
    
    /**
     * 
     * sets the name of the dataset to be retrieved from the DOCUMENTS source file.
     * This name is requested by the user selecting an object in the 
     * {@link aves.dpt.intf.viewers.AvesViewer}.
     * @param name 
     */
    public void setGroupName(String name);
   
    /**
     *  
     * gets the DOCUMENTS from the DOCUMENTS source
     * @return dataList
     */
    List<String> getData();
    
}
