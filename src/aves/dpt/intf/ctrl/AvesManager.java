/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.ctrl;

import java.util.List;

import aves.dpt.intf.production.AvesObject;

/**
 *
 * This interface controls the principal functionalities 
 * of Aves application. Classes that implement this interface keep track of the 
 * current {@link aves.dpt.intf.ctrl.AvesManager.Phase} of the 
 * SESSIONS in order to manage the 
 * production of DATA ({@link aves.dpt.intf.production.AvesObject}) 
 * in the {@link aves.dpt.intf.production.Factory} and 
 * to organize the sequence in which the {@link aves.dpt.intf.viewers.AvesViewer}
 * shows this DATA.
 * 
 * @author stefaanvanliefferinge
 * @version $Id: AvesManager.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface AvesManager {

    /**
     * Different phases in a Aves SESSIONS
     */
    enum Phase {
        SESSIONS, LOCATIONS, DATA
    };
    

    /**
     * Sets the current {@link aves.dpt.intf.ctrl.AvesManager.Phase}
     */
    void setPhase(Phase phase);
    
    /**
     * Consecutively calls {@link aves.dpt.intf.ctrl.AvesManager} methods: 
     * makeObjectsInFactory({@link aves.dpt.intf.ctrl.AvesManager.Phase}), requestObjectsFromFactory() which returns
     * a {@java.util.List} of {@link aves.dpt.intf.production.AvesObject} objects that is passed as an
     * argument to displayObjectsInViewer({@link aves.dpt.intf.ctrl.AvesManager.Phase}, {@link java.util.List}).
     * @param currentPhase 
     */
    void requestShow(Phase currentPhase);
 
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
    List<? extends AvesObject> requestObjectsFromFactory();

    /**
     * 
     * Requests the {@link aves.dpt.impl.viewers.AvesViewer} to open a 
     * content-specific viewer and to display the object. 
     * 
     * The current {@link aves.dpt.intf.ctrl.AvesManager.Phase}
     * needs to be passed to this method as well as the objects implementing the 
     * {@link aves.dpt.intf.production.AvesObject} that will be
     * displayed.
     * 
     * @param phase
     * @param avesObjectList 
     */
    void requestDisplayObjectsInViewer(Phase phase, List<? extends AvesObject> avesObjectList);
    
}
