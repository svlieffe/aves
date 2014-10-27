/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.MappaObject;

/**
 * Implements a {@link aves.dpt.intf.production.MappaObject}
 * 
 * @author svlieffe
 * @version $Id: MappaObjectImpl.java,v 649d54af3d47 2012/03/29 17:18:33 svlieffe $
 */
public class MappaObjectImpl implements MappaObject {
    
    private ArrayList<String> dataValue = new ArrayList<String>();;
    private MappaObjectType objectType;
    private ObjectDataType type;
    
    /**
     * {@inheritDoc }
     * 
     * @param mappaObjectType 
     */
    public void setObjectType(MappaObjectType mappaObjectType) {
        objectType = mappaObjectType;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    public MappaObjectType getObjectType() {
        return objectType;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param dataType 
     */
    public void setDataType(ObjectDataType dataType) {
        type = dataType;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    public ObjectDataType getDataType() {
        return type;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param value 
     */
    public void addDataValue(String value) {
        dataValue.add(value);
    }
    
    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    public List<String> getDataValues() {
        return dataValue;
    }

}
