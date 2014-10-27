/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.production;

import java.util.List;


/**
 * Classes implementing this interface request the production of
 * a {@link java.util.List} of {@link aves.dpt.intf.production.MappaObject}
 * that the {@link aves.dpt.intf.ctrl.MappaManager} 
 * manages during a specific {@link aves.dpt.intf.ctrl.MappaManager.Phase}.
 *
 * @author stefaanvanliefferinge
 * @version $Id: Factory.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface Factory {
    
    /**
     * Defines the different modes of production that will themselves
     * define the {@link aves.dpt.intf.production.MappaObject.MappaObjectType}
     * requested by the {@link aves.dpt.intf.production.Factory}.
     */
    enum ProductionMode {sessionMode, locationMode, docMode}

    /**sets the {@link aves.dpt.intf.production.Factory.ProductionMode}
     * to the {@link aves.dpt.intf.ctrl.MappaManager.Phase} determined by the 
     * {@link aves.dpt.intf.ctrl.MappaManager}
     * 
     * @param mode 
     */
    void setProductionMode(ProductionMode mode);

     /**
     * Sets the name of the dataset of specific items requested by
     * the user by selecting an object in the {@link aves.dpt.intf.viewers.MappaViewer}
     * and send to the {@link aves.dpt.intf.production.Factory} through the 
     * {@link aves.dpt.intf.ctrl.MappaManager}
     * 
     * @param item 
     */
    void setRequestedItem(String item);

    
    /**
     * starts the production of requested objects
     * and adds them to the list with {@link aves.dpt.intf.Factory.addMappaObject}
     */
    void produceObjects();
    
    /**
     * return the list maintained by the Factory.
     * The content of this list depends on the current
     * phase as determined by the 
     * {@link aves.dpt.intf.ctrl.MappaManager}
     * 
     * @return ArrayList<MappaObjectInt>
     */
    List<MappaObject> listOfObjects();
       
}
