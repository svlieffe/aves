/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.production;

import java.util.List;

/**
 * Interface defining the type of the objects presented in a Mappa session. Classes 
 * implementing this interface can be be rendered in a 
 * {@link aves.dpt.intf.viewers.DataViewer}.
 * 
 * @author stefaanvanliefferinge
 * @version $Id: MappaObject.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface MappaObject {
    
    /**
     * Defines objects depending on the 
     * {@link aves.dpt.intf.ctrl.MappaManager.Phase} 
     * when it will be used during a Mappa session.
     */
    enum MappaObjectType{sessionObject, placeObject, documentObject}
    
    /**
     * defines the {@link aves.dpt.intf.production.MappaObject.ObjectDataType}.
     */
    enum ObjectDataType{text, image, web}
    
    /**
     * Sets the {@link aves.dpt.intf.production.MappaObject.MappaObjectType} of a
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @param mappaObjectType 
     */
    void setObjectType(MappaObjectType mappaObjectType);
    
    /**
     * Get the {@link aves.dpt.intf.production.MappaObject.MappaObjectType} of a
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @return MappaObjectType
     */
    MappaObjectType getObjectType(); 

    /**
     * Sets the {@link aves.dpt.intf.production.MappaObject.ObjectDataType} of a
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @param dataType 
     */
    void setDataType(ObjectDataType dataType);
    
    /**
     * Get the {@link aves.dpt.intf.production.MappaObject.ObjectDataType} of a
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @return ObjectDataType
     */        
    ObjectDataType getDataType();

    /**
     * Adds a value to the {@link java.util.List} of {@link aves.dpt.intf.production.MappaObject}
     * @param value 
     */
    void addDataValue(String value);
    
    /**
     * Returns the {@link java.util.List} of {values for a {@link aves.dpt.intf.production.MappaObject}
     * 
     * @param value 
     */
    List<String> getDataValues();
    
}
