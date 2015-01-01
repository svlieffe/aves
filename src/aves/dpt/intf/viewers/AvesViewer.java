/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.intf.viewers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import gov.nasa.worldwind.geom.LatLon;
import aves.dpt.intf.ctrl.AvesManager.Phase;
import aves.dpt.intf.production.AvesObject;

/**
 * User interface showing {@link aves.dpt.intf.production.AvesObject}
 * content, managed by a {@link aves.dpt.intf.ctrl.AvesManager}. 
 * Delegates the display of specific content depending on the {@link aves.dpt.intf.production.AvesObject.AvesObjectType}
 * and on the {@link aves.dpt.intf.ctrl.AvesManager.Phase}.

 * @author svlieffe
 * 2012/03/29
 */
public interface AvesViewer extends KeyListener {

	/**
	 * Type of viewer depending on the current {@link aves.dpt.intf.ctrl.AvesManager.Phase}
	 */
	enum ViewerType {
        worldWindSessions, worldWindPlaces, dataViewer
    }
    
	/**
	 * Type of event depending on the selected item
	 */
    enum EventItemType {
    	JOURNEYBUTTON, LOCATIONSPOT, ESCKEY;
    }

    /**
     * Sets the current phase of {@link aves.dpt.intf.viewers.AvesViewer} to be displayed
     * @param phase
     */
    void setCurrentPhase(Phase phase);

    /**
     * Returns the current phase of {@link aves.dpt.intf.viewers.AvesViewer} to be displayed
     * @return currentPhase
     */
    Phase getCurrentPhase();

    /**
     * Selects the type of content-specific 
     * viewer to display.
     * @param phase
     */  
    void selectSpecializedViewer(Phase phase);
    
    /**
     * Sets the list of {@link aves.dpt.intf.production.AvesObject} to be displayed
     * 
     * @param objectList 
     */
    
    void setAvesObjectsList(List<? extends AvesObject> objectList);    
    
    /**
     * Opens a specific viewer depending on the current {@link aves.dpt.intf.ctrl.AvesManager.Phase}
     * <p>
     * {@link aves.dpt.intf.viewers.WorldWindViewer}
     * <p>
     * {@link aves.dpt.intf.viewers.DataViewer}
     *  
     */
    void requestObjectsInViewer();
    
     /**
     * Hides the specialized {@link aves.dpt.intf.viewers.DataViewer}
     * and shows the {@link aves.dpt.intf.viewers.WorldWindViewer} this
     * is a callback from a class 
     * implementing {@link aves.dpt.intf.viewers.DataViewer} object.
     */
    public void closeDataViewer();

    
    /**
     * Sets the {@link aves.dpt.intf.viewers.AvesViewer} to full screen.
     * @param fullScreen
     */
    void setFullScreen(boolean fullScreen);
    
     /**
     * TBI
     * Creates a PLACES based on the list of locations.
     * returns a List of objects extending or implementing {@link gov.nasa.worldwind.geom.LatLon}
     * @return listOfPlaces 
     */
    List<? extends LatLon> makeRoute();
       
    /**
     * TBI
     * Request a WorldWindViewer ({@link aves.dpt.intf.viewers.WorldWindViewer}) to display 
     * the itinerary.
     * @param listOfCoords
     */
    void displayRoute(List<? extends LatLon> listOfCoords);
    
    /**
     * Listens to key events 
     * 
     */
    public void keyPressed(KeyEvent ke);

    /**
     * 
     * @return selectedItemEvent 
     */
    String getSelectedItem();
    
    /**
     * 
     * @return eventItemType 
     */
    EventItemType getSelectedItemType();

    /**
     * 
     * @return keyEvent 
     */
    KeyEvent getKeyEventType();
}
