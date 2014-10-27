/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

import java.util.List;

import gov.nasa.worldwind.geom.LatLon;
import aves.dpt.intf.production.MappaObject;

/**
 *
 * @author stefaanvanliefferinge
 * @version $Id: MappaViewer.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */

/**
 * User interface showing {@link aves.dpt.intf.production.MappaObject}
 * content managed by a {@link aves.dpt.intf.ctrl.MappaManager}. 
 * Delegates the display of specific content depending on the {@link aves.dpt.intf.production.MappaObject.MappaObjectType}
 * and on the {@link aves.dpt.intf.ctrl.MappaManager.Phase}.
 */
public interface MappaViewer{
    enum ViewerType {
        worldWindSessions, worldWindPlaces, dataViewer
    }
    
    /**
     * Selects the type of content-specific 
     * viewer to display.
     * @param type 
     */  
    void selectSpecializedViewer(ViewerType type);
    
    /**
     * Sets the list of {@link aves.dpt.intf.production.MappaObject} to be displayed
     * 
     * @param objectList 
     */
    
    void setMappaObjectsList(List<? extends MappaObject> objectList);    
    
    /**
     * Opens a specific viewer depending on the current {@link aves.dpt.intf.ctrl.MappaManager.Phase}
     * <p>
     * {@link aves.dpt.intf.viewers.WorldWindViewer}
     * <p>
     * {@link aves.dpt.intf.viewers.DataViewer}
     */
    void runSpecializedViewers();
    
     /**
     * Hides the specialized {@link aves.dpt.intf.viewers.DataViewer}
     * and shows the {@link aves.dpt.intf.viewers.WorldWindViewer} this
     * is a callback from a class 
     * implementing {@link aves.dpt.intf.viewers.DataViewer} object.
     */
    public void closeDataViewer();

    
    /**
     * Sets the {@link aves.dpt.intf.viewers.MappaViewer} to full screen.
     */
    void setFullScreen(boolean val);
    
     /**
     * TBI
     * Creates a route based on the list of locations.
     * returns a List of objects extending or implementing {@link gov.nasa.worldwind.geom.LatLon} 
     */
    List<? extends LatLon> makeRoute();
       
    /**
     * TBI
     * Request a WorldWindViewer ({@link aves.dpt.impl.viewers.WorldWindViewer}) to display 
     * the itinerary.
     */
    void displayRoute(List<? extends LatLon> listOfCoords);

}
