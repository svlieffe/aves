/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.intf.production;

import java.util.List;

import aves.dpt.intf.production.AvesObject.AvesObjectType;


/**
 * Classes implementing this interface request the production of
 * a {@link java.util.List} of {@link aves.dpt.intf.production.AvesObject}s
 * that the {@link aves.dpt.intf.ctrl.AvesManager} 
 * manages during a specific {@link aves.dpt.intf.ctrl.AvesManager.Phase}.
 *
 * @author svlieffe
 * 2012/03/29
 */
public interface Factory {
    
    /**
     * 
     * Defines the different modes of production that will themselves
     * define the {@link aves.dpt.intf.production.AvesObject.AvesObjectType}
     * requested by the {@link aves.dpt.intf.production.Factory}.
     */
    enum ProductionMode {SESSIONSMODE, LOCATIONSMODE, DATAMODE}

    /**
     * 
     * sets the {@link aves.dpt.intf.production.Factory.ProductionMode}
     * to the {@link aves.dpt.intf.ctrl.AvesManager.Phase} determined by the 
     * {@link aves.dpt.intf.ctrl.AvesManager}
     * 
     * @param mode 
     */
    void setProductionMode(ProductionMode mode);

    /**
     * 
     * Sets the name of the dataset of specific items requested by
     * the user by selecting an object in the {@link aves.dpt.intf.viewers.AvesViewer}
     * and send to the {@link aves.dpt.intf.production.Factory} through the 
     * {@link aves.dpt.intf.ctrl.AvesManager}
     * 
     * @param item 
     */
    void setRequestedItem(String item);

    
    /**
     * Starts the production of requested objects
     * and adds them to the list
     */
    void produceObjects();
    
    /**
     * 
     * Return the list maintained by the Factory.
     * The content of this list depends on the current
     * phase as determined by the 
     * {@link aves.dpt.intf.ctrl.AvesManager}
     * 
     * @return listOfObjects
     */
    List<AvesObject> listOfObjects();

}
