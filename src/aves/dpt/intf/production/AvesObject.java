package aves.dpt.intf.production;

import java.util.List;

/**
 * 
 * Interface defining the type of the objects presented in Aves JOURNEYS. Classes 
 * implementing this interface can be be rendered in a 
 * {@link aves.dpt.intf.viewers.DataViewer}.
 * 
 * @author svlieffe
 * 2012/03/29
 */
public interface AvesObject {
    
    /**
     * 
     * Defines objects depending on the 
     * {@link aves.dpt.intf.ctrl.AvesManager.Phase} 
     * when it will be used during a Aves JOURNEYS.
     */
    enum AvesObjectType{JOURNEYS, PLACES, DOCUMENTS}
    
    /**
     * 
     * defines the {@link aves.dpt.intf.production.AvesObject.ObjectDataType}.
     */
    enum ObjectDataType{image, web}
    
    /**
     * 
     * Sets the {@link aves.dpt.intf.production.AvesObject.AvesObjectType} of a
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @param avesObjectType 
     */
    void setObjectType(AvesObjectType avesObjectType);
    
    /**
     * 
     * Get the {@link aves.dpt.intf.production.AvesObject.AvesObjectType} of a
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @return AvesObjectType
     */
    AvesObjectType getObjectType(); 

    /**
     * 
     * Sets the {@link aves.dpt.intf.production.AvesObject.ObjectDataType} of a
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @param dataType 
     */
    void setDataType(ObjectDataType dataType);
    
    /**
     * 
     * Get the {@link aves.dpt.intf.production.AvesObject.ObjectDataType} of a
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @return ObjectDataType
     */        
    ObjectDataType getDataType();

    /**
     * 
     * Adds a value to the {@link java.util.List} of {@link aves.dpt.intf.production.AvesObject}
     * @param value 
     */
    void addDataValue(String value);
    
    /**
     * 
     * Returns the {@link java.util.List} of {values for a {@link aves.dpt.intf.production.AvesObject}
     * 
     * @param value 
     */
    List<String> getDataValues();
    
}
