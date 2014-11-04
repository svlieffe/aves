/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.production;

import java.util.List;


/**
 * Classes implementing this interface request the production of
 * a {@link java.util.List} of {@link aves.dpt.intf.production.AvesObject}
 * that the {@link aves.dpt.intf.ctrl.AvesManager} 
 * manages during a specific {@link aves.dpt.intf.ctrl.AvesManager.Phase}.
 *
 * @author stefaanvanliefferinge
 * @version $Id: Factory.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface Factory {
    
    /**
     * Defines the different modes of production that will themselves
     * define the {@link aves.dpt.intf.production.AvesObject.AvesObjectType}
     * requested by the {@link aves.dpt.intf.production.Factory}.
     */
    enum ProductionMode {SESSIONSMODE, LOCATIONSMODE, DATAMODE}

    /**sets the {@link aves.dpt.intf.production.Factory.ProductionMode}
     * to the {@link aves.dpt.intf.ctrl.AvesManager.Phase} determined by the 
     * {@link aves.dpt.intf.ctrl.AvesManager}
     * 
     * @param mode 
     */
    void setProductionMode(ProductionMode mode);

     /**
     * Sets the name of the dataset of specific items requested by
     * the user by selecting an object in the {@link aves.dpt.intf.viewers.AvesViewer}
     * and send to the {@link aves.dpt.intf.production.Factory} through the 
     * {@link aves.dpt.intf.ctrl.AvesManager}
     * 
     * @param item 
     */
    void setRequestedItem(String item);

    
    /**
     * starts the production of requested objects
     * and adds them to the list with {@link aves.dpt.intf.Factory.addAvesObject}
     */
    void produceObjects();
    
    /**
     * return the list maintained by the Factory.
     * The content of this list depends on the current
     * phase as determined by the 
     * {@link aves.dpt.intf.ctrl.AvesManager}
     * 
     * @return ArrayList<AvesObjectInt>
     */
    List<AvesObject> listOfObjects();
       
}
