/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

import java.util.List;

import aves.dpt.impl.production.AvesObjectImpl;
import aves.dpt.intf.production.AvesObject.ObjectDataType;

/**
 * 
 * A basic interface for a viewer specialized in displaying data. Classes implementing
 * this interface can be called by a {@link aves.dpt.intf.viewers.AvesViewer}
 * to display different types of data.
 *
 * <p> 
 * @author stefaanvanliefferinge
 * @version 
 */
public interface DataViewer {
   
    enum DataViewerType {

        textViewer, imageViewer, webViewer;
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
     * data to display in the show.
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
}
