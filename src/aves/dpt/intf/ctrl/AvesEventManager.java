package aves.dpt.intf.ctrl;


/**
 *
 * Serves to produce a callback from the {@link aves.dpt.intf.viewers.AvesViewer}
 * to the {@link aves.dpt.intf.ctrl.AvesManager}.
 * 
 * Viewer events are handled by the {@link aves.dpt.intf.viewers.AvesViewer}
 * 
 * @author svlieffe
 * 2012/03/29
 */
public interface AvesEventManager {
	
    /**
     * 
     * Handles events for the  {@link aves.dpt.intf.viewers.AvesViewer}
     */
    void avesViewerEvent();

    /**
     * 
     * Handles events for the  {@link aves.dpt.intf.viewers.DataViewer} 
     */
    void dataViewerEvent();

}
