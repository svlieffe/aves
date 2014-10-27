/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.DataRetriever;
import aves.dpt.intf.production.MappaObject.MappaObjectType;


/**
 *
 * Basic implementation of the 
 * {@link aves.dpt.intf.production.DataRetriever}. It maintains an
 * object implementing {@link aves.dpt.intf.production.XMLDataReader}.
 * 
 * @author svlieffe
 */
public class DataRetrieverImpl implements DataRetriever{
    
    private MappaObjectType objectType;
    private String groupName;
    private XMLDataReaderImpl dr = new XMLDataReaderImpl();
    private ArrayList<String> dataList = new ArrayList<String>();
    
    /**
     * {@inheritDoc }
     * @return 
     */
    public List<String> getData() {
        dr.setType(objectType);
        dr.setGroup(groupName);
        dr.read();
        dataList = (ArrayList)dr.valueList();
        return dataList;       
    }
    
    /**
     * {@inheritDoc }
     * @param mappaObjectType 
     */
    public void setDataType(MappaObjectType mappaObjectType) {
        objectType = mappaObjectType;        
    }
    
    /**
     * {@inheritDoc }
     * @param name 
     */
    public void setGroupName(String name) {
        groupName = name;
    }

}
