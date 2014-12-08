/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import gov.nasa.worldwind.geom.LatLon;
import aves.dpt.intf.ctrl.AvesManager.Phase;
import aves.dpt.intf.production.AvesObject;

/**
 *
 * @author stefaanvanliefferinge
 * @version $Id: AvesViewer.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */

/**
 * User interface showing {@link aves.dpt.intf.production.AvesObject}
 * content managed by a {@link aves.dpt.intf.ctrl.AvesManager}. 
 * Delegates the display of specific content depending on the {@link aves.dpt.intf.production.AvesObject.AvesObjectType}
 * and on the {@link aves.dpt.intf.ctrl.AvesManager.Phase}.
 */
public interface AvesViewer extends KeyListener {
    enum ViewerType {
        worldWindSessions, worldWindPlaces, dataViewer
    }
    
    enum EventItemType {
    	JOURNEYBUTTON, LOCATIONSPOT, ESCKEY;
    }

    /**
     * Sets the current phase of {@link aves.dpt.intf.viewers.AvesViewer} to be displayed
     * 
     * @param Phase 
     */
    void setCurrentPhase(Phase phase);

    /**
     * Returns the current phase of {@link aves.dpt.intf.viewers.AvesViewer} to be displayed
     * 
     * @param 
     */
    Phase getCurrentPhase();

    /**
     * Selects the type of content-specific 
     * viewer to display.
     * @param phase TODO
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
     * @param phase TODO
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
     */
    void setFullScreen(boolean val);
    
     /**
     * TBI
     * Creates a PLACES based on the list of locations.
     * returns a List of objects extending or implementing {@link gov.nasa.worldwind.geom.LatLon} 
     */
    List<? extends LatLon> makeRoute();
       
    /**
     * TBI
     * Request a WorldWindViewer ({@link aves.dpt.impl.viewers.WorldWindViewer}) to display 
     * the itinerary.
     */
    void displayRoute(List<? extends LatLon> listOfCoords);
    
    /**
     * Listens to key events 
     * 
     */
    public void keyPressed(KeyEvent ke);

}
