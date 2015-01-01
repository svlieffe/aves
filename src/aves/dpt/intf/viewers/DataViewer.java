/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.intf.viewers;

import java.util.List;

import aves.dpt.impl.production.AvesObjectImpl;
import aves.dpt.intf.production.AvesObject.ObjectDataType;

/**
 * 
 * A basic interface for a viewer specialized in displaying DOCUMENTS. Classes implementing
 * this interface can be called by a {@link aves.dpt.intf.viewers.AvesViewer}
 * to display different types of DOCUMENTS.
 *
 * @author svlieffe
 * 2012/03/29
 */
public interface DataViewer {

	/**
	 * Type of {@link aves.dpt.intf.viewers.DataViewer}
	 *
	 */
    enum DataViewerType {

        imageViewer, webViewer;
    }
    
	/**
	 * Type of event in the {@link aves.dpt.intf.viewers.DataViewer}
	 *
	 */
    enum DataViewerEvent {
    	
    	UPDATE, ENDSHOW;
    }
    
    /**
     * Displays the next image of the slide show
     * @throws Exception 
     */
    void displayNext() throws Exception;
    
    /**
     * Displays the previous image in the slide show
     * @throws Exception 
     */
    public void displayPrev() throws Exception;
    
    /**
     * Sets the list of {@link aves.dpt.intf.production.AvesObject}
     * DOCUMENTS to display in the show.
     * 
     * @param avesObjects 
     */
    void setObjectsToDisplay(List<? extends AvesObjectImpl> avesObjects);

    /**
     * Sets the {@link aves.dpt.intf.viewers.DataViewer.DataViewerType}
     * based on the {@link aves.dpt.intf.production.AvesObject.ObjectDataType}.
     * @param objectType 
     */
    void setViewerType(ObjectDataType objectType);
    
    /**
     * Displays a slide on the screen
     * 
     * @throws DataNotFoundException 
     */
    void showData() throws DataNotFoundException;
    
    DataViewerEvent getEvent();
    
    void setEvent(DataViewerEvent event);
}
