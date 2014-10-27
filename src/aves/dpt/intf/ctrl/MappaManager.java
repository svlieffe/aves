/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.ctrl;

import java.util.List;

import aves.dpt.intf.production.MappaObject;

/**
 *
 * This interface controls the principal functionalities 
 * of Mappa application. Classes that implement this interface keep track of the 
 * current {@link aves.dpt.intf.ctrl.MappaManager.Phase} of the 
 * session in order to manage the 
 * production of data ({@link aves.dpt.intf.production.MappaObject}) 
 * in the {@link aves.dpt.intf.production.Factory} and 
 * to organize the sequence in which the {@link aves.dpt.intf.viewers.MappaViewer}
 * shows this data.
 * 
 * @author stefaanvanliefferinge
 * @version $Id: MappaManager.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface MappaManager {

    /**
     * Different phases in a Mappa session
     */
    enum Phase {
        session, route, data
    };
    

    /**
     * Sets the current {@link aves.dpt.intf.ctrl.MappaManager.Phase}
     * based on the previous {@link aves.dpt.intf.ctrl.MappaManager.Phase}.
     */
    void setPhase(Phase currentPhase);
    
    /**
     * Consecutively calls {@link aves.dpt.intf.ctrl.MappaManager} methods: 
     * makeObjectsInFactory({@link aves.dpt.intf.ctrl.MappaManager.Phase}), requestObjectsFromFactory() which returns
     * a {@java.util.List} of {@link aves.dpt.intf.production.MappaObject} objects that is passed as an
     * argument to displayObjectsInViewer({@link aves.dpt.intf.ctrl.MappaManager.Phase}, {@link java.util.List}).
     * @param currentPhase 
     */
    void produceAndShow(Phase currentPhase);
 
    /**
     * 
     * Requests instances of wrapper classes that define objects to be
     * displayed in the presentation. {@link Factory} maintains a list of
     * these instances. The list maintained depends on the current operation phase
     * that defines different modes of operation.
     */
    void makeObjectsInFactory(Phase currentPhase);

    /**
     * Requests the objects from {@link Factory}. The
     * objects can then be displayed in the {@link aves.dpt.intf.viewers.DataViewer}.
     *
     */
    List<? extends MappaObject> requestObjectsFromFactory();

    /**
     * 
     * Requests the {@link aves.dpt.impl.viewers.MappaViewer} to open a 
     * content-specific viewer and to display the object. 
     * 
     * The current {@link aves.dpt.intf.ctrl.MappaManager.Phase}
     * needs to be passed to this method as well as the objects implementing the 
     * {@link aves.dpt.intf.production.MappaObject} that will be
     * displayed.
     * 
     * @param phase
     * @param mappaObjectList 
     */
    void displayObjectsInViewer(Phase phase, List<? extends MappaObject> mappaObjectList);
    
}
