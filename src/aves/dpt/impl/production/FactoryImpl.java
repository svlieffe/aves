/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import aves.dpt.intf.production.Factory;
import aves.dpt.intf.production.AvesObject;
import aves.dpt.intf.production.AvesObject.AvesObjectType;
import aves.dpt.intf.production.AvesObject.ObjectDataType;

/**
 * An implementation of a {@link aves.dpt.intf.production.Factory}
 * to answer to requests of objects implementing
 * {@link aves.dpt.intf.ctrl.AvesManager}
 *
 * @author svlieffe
 * 2012/03/29
 */
public class FactoryImpl implements Factory {

    private ProductionMode productionMode;
    private String requestedItem;
    private AvesObjectType avesObjectType;
    private ArrayList<AvesObject> listOfObjects;


    /**
     * 
     * {@inheritDoc }
     * @param mode 
     */
    @Override
    public void setProductionMode(ProductionMode mode) {
        productionMode = mode;
    }

    /**
     * 
     * {@inheritDoc }
     * @param item 
     */
    @Override
    public void setRequestedItem(String item) {
        requestedItem = item;
    }

    
    /**
     * 
     * Adds a Session object to the {@link java.util.List} of 
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @param avesObjectType
     * @param valueOfObjectToAdd 
     */
    private void addAvesSessionObject(AvesObjectType avesObjectType, String valueOfObjectToAdd) {
        AvesObjectImpl mo = new AvesObjectImpl();
        mo.setObjectType(avesObjectType);
        mo.addDataValue(valueOfObjectToAdd);
        listOfObjects.add(mo);
    }
 
    /**
     * 
     * Adds a Document object to the {@link java.util.List} of 
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @param avesObjectType
     * @param typeOfObjectToAdd
     * @param valueOfObjectToAdd 
     */
    private void addAvesDocumentObject(AvesObjectType avesObjectType, String typeOfObjectToAdd, String valueOfObjectToAdd) {
        ObjectDataType dataType = null;
        AvesObjectImpl mo = new AvesObjectImpl();
        mo.setObjectType(avesObjectType);
        if ("img".equals(typeOfObjectToAdd)) {
        	dataType = ObjectDataType.image;
        } else if ("web".equals(typeOfObjectToAdd)){
        	dataType = ObjectDataType.web;
        // textviewer not implemented	
        //} else if ("txt".equals(typeOfObjectToAdd)){
        //	dataType = ObjectDataType.text;
        } else {
            System.out.println("Unknown object type");
        }
            
        mo.setDataType(dataType);
        mo.addDataValue(valueOfObjectToAdd);
        listOfObjects.add(mo);
    }

    /**
     * 
     * Adds a Location object to the {@link java.util.List} of 
     * {@link aves.dpt.intf.production.AvesObject}
     * 
     * @param avesObjectType
     * @param place
     * @param longitude
     * @param latitude 
     */
    private void addAvesLocationObject(AvesObjectType avesObjectType, String place, String longitude, String latitude) {
        ObjectDataType dataType = null;
        AvesObjectImpl mo = new AvesObjectImpl();
        mo.setObjectType(avesObjectType);
        mo.setDataType(dataType);
        mo.addDataValue(place);
        mo.addDataValue(longitude);
        mo.addDataValue(latitude);
        listOfObjects.add(mo);
    }

    /**
     * 
     * {@inheritDoc }
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public List<AvesObject> listOfObjects() {

        return (ArrayList) listOfObjects;
    }

    /**
     * 
     * {@inheritDoc }
     *  
     */
    public void produceObjects() {

        DataRetrieverImpl dr;
        Iterator<String> e;

        switch (productionMode) {
            case SESSIONSMODE:
                avesObjectType = AvesObjectType.JOURNEYS;
                String session;
                dr = new DataRetrieverImpl();
                dr.setDataType(avesObjectType);
                e = dr.getData().iterator();
                listOfObjects = new ArrayList<AvesObject>();
                while (e.hasNext()) {
                    session = e.next();
                    addAvesSessionObject(avesObjectType, session);
                }
                break;
            case LOCATIONSMODE:
                String place;
                String longitude;
                String latitude;
                listOfObjects.clear();
                avesObjectType = AvesObjectType.PLACES;
                dr = new DataRetrieverImpl();
                dr.setDataType(avesObjectType);
                dr.setGroupName(requestedItem);
                e = dr.getData().iterator();
                listOfObjects = new ArrayList<AvesObject>();
                while (e.hasNext()) {      
                    place = e.next();
                    longitude = e.next();
                    latitude = e.next();
                    addAvesLocationObject(avesObjectType, place, longitude, latitude);
               }
                break;
            case DATAMODE:
                String uri;
                String type;
                listOfObjects.clear();
                avesObjectType = AvesObjectType.DOCUMENTS;
                dr = new DataRetrieverImpl();
                dr.setDataType(avesObjectType);
                dr.setGroupName(requestedItem);
                e = dr.getData().iterator();
                listOfObjects = new ArrayList<AvesObject>();
                while (e.hasNext()) {      
                    type = e.next();
                    uri = e.next();
                    addAvesDocumentObject(avesObjectType, type, uri);
               }
               break;
        }

    }
}
