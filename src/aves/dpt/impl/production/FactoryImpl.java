/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//TODO redesign to implement a more general method to add MappaObjects to the List 

package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import aves.dpt.intf.production.Factory;
import aves.dpt.intf.production.MappaObject;
import aves.dpt.intf.production.Factory.ProductionMode;
import aves.dpt.intf.production.MappaObject.MappaObjectType;
import aves.dpt.intf.production.MappaObject.ObjectDataType;

/**
 * An implementation of a {@link aves.dpt.intf.production.Factory}
 * to answer to requests of objects implementing
 * {@link aves.dpt.intf.ctrl.MappaManager}
 *
 * @author svlieffe
 */
public class FactoryImpl implements Factory {

    private ProductionMode productionMode;
    private String requestedItem;
    private MappaObjectType mappaObjectType;
    private ArrayList<MappaObject> listOfObjects = new ArrayList<MappaObject>();


    /**
     * {@inheritDoc }
     *  
     */
    public void setProductionMode(ProductionMode mode) {
        productionMode = mode;
    }

    /**
     * {@inheritDoc }
     * 
     * @param item 
     */
    public void setRequestedItem(String item) {
        requestedItem = item;
    }

    
    /**
     * Adds a Session object to the {@link java.util.List} of 
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @param mappaObjectType
     * @param valueOfObjectToAdd 
     */
    private void addMappaSessionObject(MappaObjectType mappaObjectType, String valueOfObjectToAdd) {
        MappaObjectImpl mo = new MappaObjectImpl();
        mo.setObjectType(mappaObjectType);
        mo.addDataValue(valueOfObjectToAdd);
        listOfObjects.add(mo);
    }
 
   /**
     * Adds a Document object to the {@link java.util.List} of 
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @param mappaObjectType
     * @param typeOfObjectToAdd
     * @param valueOfObjectToAdd 
     */
    private void addMappaDocumentObject(MappaObjectType mappaObjectType, String typeOfObjectToAdd, String valueOfObjectToAdd) {
        ObjectDataType dataType = null;
        MappaObjectImpl mo = new MappaObjectImpl();
        mo.setObjectType(mappaObjectType);
        if ("txt".equals(typeOfObjectToAdd)) {
            dataType = ObjectDataType.text;
        } else if ("img".equals(typeOfObjectToAdd)){
            dataType = ObjectDataType.image;
        } else if ("web".equals(typeOfObjectToAdd)){
            dataType = ObjectDataType.web;
        } else {
            System.out.println("Unknown object type");
        }
            
        mo.setDataType(dataType);
        mo.addDataValue(valueOfObjectToAdd);
        listOfObjects.add(mo);
    }

   /**
     * Adds a Location object to the {@link java.util.List} of 
     * {@link aves.dpt.intf.production.MappaObject}
     * 
     * @param mappaObjectType
     * @param place
     * @param longitude
     * @param latitude 
     */
    private void addMappaLocationObject(MappaObjectType mappaObjectType, String place, String longitude, String latitude) {
        ObjectDataType dataType = null;
        MappaObjectImpl mo = new MappaObjectImpl();
        mo.setObjectType(mappaObjectType);
        mo.setDataType(dataType);
        mo.addDataValue(place);
        mo.addDataValue(longitude);
        mo.addDataValue(latitude);
        listOfObjects.add(mo);
    }

    /**
     * {@inheritDoc }
     *  
     */
    public List<MappaObject> listOfObjects() {

        return listOfObjects;
    }

    /**
     * {@inheritDoc }
     * 
     * @param productionPhase 
     */
    public void produceObjects() {

        DataRetrieverImpl dr;
        Iterator<String> e;

        switch (productionMode) {
            case sessionMode:
                mappaObjectType = MappaObjectType.sessionObject;
                String session;
                dr = new DataRetrieverImpl();
                dr.setDataType(mappaObjectType);
                e = dr.getData().iterator();
                while (e.hasNext()) {
                    session = e.next();
                    addMappaSessionObject(mappaObjectType, session);
                }
                break;
            case locationMode:
                String place;
                String longitude;
                String latitude;
                listOfObjects.clear();
                mappaObjectType = MappaObjectType.placeObject;
                dr = new DataRetrieverImpl();
                dr.setDataType(mappaObjectType);
                dr.setGroupName(requestedItem);
                e = dr.getData().iterator();
                while (e.hasNext()) {      
                    place = e.next();
                    longitude = e.next();
                    latitude = e.next();
                    addMappaLocationObject(mappaObjectType, place, longitude, latitude);
               }
                break;
            case docMode:
                String uri;
                String type;
                listOfObjects.clear();
                mappaObjectType = MappaObjectType.documentObject;
                dr = new DataRetrieverImpl();
                dr.setDataType(mappaObjectType);
                dr.setGroupName(requestedItem);
                e = dr.getData().iterator();
                while (e.hasNext()) {      
                    type = e.next();
                    uri = e.next();
                    addMappaDocumentObject(mappaObjectType, type, uri);
               }
               break;
        }

    }
}
