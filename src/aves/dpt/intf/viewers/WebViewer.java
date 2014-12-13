package aves.dpt.intf.viewers;

import aves.dpt.impl.viewers.AvesBrowser;

/**
 *
 * Objects implementing this interface render web content.
 *
 * @author svlieffe
 * 2012/03/29
 */

public interface WebViewer {
     void setCurrentSource(String uri); 
     
     public AvesBrowser getAvesBrowser();
}
