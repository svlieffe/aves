/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

/**
 *
 * Serves to produce a callback from the {@link aves.dpt.intf.viewers.MappaViewer}
 * to the {@link aves.dpt.intf.ctrl.MappaManager}.
 * 
 * @author stefaanvanliefferinge
 * @version $Id: ViewerEvent.java,v 649d54af3d47 2012/03/29 17:18:33 svl $
 */
public interface ViewerEvent {
    void viewerEvent();
}
