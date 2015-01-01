/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
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
