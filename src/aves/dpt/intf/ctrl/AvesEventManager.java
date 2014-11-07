/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.ctrl;

/**
 *
 * Serves to produce a callback from the {@link aves.dpt.intf.viewers.AvesViewer}
 * to the {@link aves.dpt.intf.ctrl.AvesManager}.
 * 
 * Viewer events are handled by the {@link aves.dpt.intf.viewers.AvesViewer}
 * 
 * @author stefaanvanliefferinge
 * @version $Id: AvesEventManager.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface AvesEventManager {
	
	Object getAvesEventSubject();
	
	void setAvesEventSubject(Object subject);
	
    /**
     * handles events for the  {@link aves.dpt.intf.viewers.AvesViewer}
     */
    void avesViewerEvent();

    /**
     * handles events for the  {@link aves.dpt.intf.viewers.DataViewer}
     */
    void dataViewerEvent();
}
