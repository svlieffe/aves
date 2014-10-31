/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.AvesObject;

/**
 * Implements a {@link aves.dpt.intf.production.AvesObject}
 * 
 * @author svlieffe
 * @version $Id: AvesObjectImpl.java,v 649d54af3d47 2012/03/29 17:18:33 svlieffe $
 */
public class AvesObjectImpl implements AvesObject {
    
    private ArrayList<String> dataValue = new ArrayList<String>();;
    private AvesObjectType objectType;
    private ObjectDataType type;
    
    /**
     * {@inheritDoc }
     * 
     * @param avesObjectType 
     */
    public void setObjectType(AvesObjectType avesObjectType) {
        objectType = avesObjectType;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    public AvesObjectType getObjectType() {
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
