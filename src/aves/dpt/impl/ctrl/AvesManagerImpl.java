package aves.dpt.impl.ctrl;

import java.util.ArrayList;

//import aves.dpt.impl.production.AvesObjectImpl;
//import aves.dpt.intf.production.AvesObject.AvesObjectType;

import aves.dpt.impl.production.FactoryImpl;
import aves.dpt.impl.viewers.AvesViewerImpl;
import aves.dpt.intf.ctrl.AvesManager;
import aves.dpt.intf.production.AvesObject;
import aves.dpt.intf.production.Factory.ProductionMode;
import aves.dpt.intf.viewers.ViewerEvent;
import aves.dpt.intf.viewers.AvesViewer.ViewerType;

//import gov.nasa.worldwind.geom.LatLon;
import java.awt.Color;
import java.util.List;

/**
 *
 * This class implements a {@link aves.dpt.intf.ctrl.AvesManager}. It
 * organizes requests for {@link aves.dpt.intf.production.AvesObject}s 
 * from the {@link aves.dpt.intf.production.Factory}
 * and delegates the display of these
 * {@link aves.dpt.intf.production.AvesObject}s to 
 * the {@link aves.dpt.intf.viewers.AvesViewer}. It
 * receives user input as a callback from the {@link aves.dpt.intf.viewers.AvesViewer} 
 * and analyzes this input to make further decisions.
 * 
 * @author svlieffe
 * 
 * @version $Id: AvesManagerImpl.java,v 649d54af3d47 2012/03/29 17:18:33 svlieffe $
 */
public class AvesManagerImpl implements AvesManager, ViewerEvent {

    private AvesViewerImpl mv;//
    private ProductionMode mode;
    private FactoryImpl factory;
    private boolean fullScreen;
    private String mvSelected;
    private String mvSelectedType;
    private boolean callIsFromAvesViewer;

    public AvesManagerImpl() {

        fullScreen = true;             // toggle fullscreen
        callIsFromAvesViewer = false;

        factory = new FactoryImpl();

        mv = new AvesViewerImpl(this);
        mv.setFullScreen(fullScreen);
        mv.getContentPane().setBackground(Color.black);
        this.setPhase(Phase.session); // the first phase is to show the sessions

    }

    /**
     * {@inheritDoc }
     * <p>
     *  
     */
    @Override
    public void viewerEvent() {
        Phase currentPhase = Phase.session;
        callIsFromAvesViewer = true;
        mvSelected = mv.getSelectedItem();
        mvSelectedType = mv.getSelectedItemType();
        System.out.println("Manager receives:" + mvSelected);
        System.out.println("Manager receives type:" + mvSelectedType);
        if ("spot".equals(mvSelectedType)) {
            currentPhase = Phase.route;
        }
        if (currentPhase != null) {
            this.setPhase(currentPhase);
        }
    }

    /**
     * {@inheritDoc }
     * 
     * @param currentPhase 
     */
    public void setPhase(Phase currentPhase) {
        
        switch (currentPhase) {
            case session:
                if (callIsFromAvesViewer) {
                    factory.setRequestedItem(mvSelected);
                    currentPhase = Phase.route;
                    System.out.println("from session to route phase");
                    callIsFromAvesViewer = false;
                }
                break;
            case route:
                factory.setRequestedItem(mvSelected);
                System.out.println("from route to data phase");
                currentPhase = Phase.data;
                break;
            case data:
                System.out.println("from data to route phase");
                currentPhase = Phase.route;
                break;
        }
        produceAndShow(currentPhase);
        
    }
    
    /**
     * 
     * {@inheritDoc } 
     */
    public void produceAndShow(Phase currentPhase) {
        ArrayList<AvesObject> avesObjectList = new ArrayList<AvesObject>();
        makeObjectsInFactory(currentPhase);
        avesObjectList = (ArrayList)requestObjectsFromFactory();
        displayObjectsInViewer(currentPhase, avesObjectList);      
    }

    /**
     * 
     * {@inheritDoc }
     */
    public void makeObjectsInFactory(Phase currentPhase) {
        //creates a FactoryImpl that maintains a list of AvesObjects
        //AvesObjects are of a certain type (session, place, document)
        switch (currentPhase) {
            case session:   //request FactoryImpl to maintain list of sessions
                mode = ProductionMode.sessionMode;
                break;
            case route:     //request FactoryImpl to maintain list of locations
                //for a specific session
                mode = ProductionMode.locationMode;
                break;
            case data:      //request FactoryImpl to maintain list of Data 
                //for a specific location
                mode = ProductionMode.docMode;
                break;
        }
        factory.setProductionMode(mode);
        factory.produceObjects();
    }

    /**
     * 
     * {@inheritDoc }
     */
    public List<? extends AvesObject> requestObjectsFromFactory() {
        
        ArrayList<AvesObject> avesObjectList = new ArrayList<AvesObject>();

        avesObjectList = (ArrayList) factory.listOfObjects();
        
        return avesObjectList;
    }

    /**
     * {@inheritDoc }
     */
    public void displayObjectsInViewer(Phase phase, List<? extends AvesObject> avesObjects) {
    switch (phase) {
            case session: //I believe I should remove the case and only invoke the avesviewer; the avesviewer will decide what actions to take 
                mv.selectSpecializedViewer(ViewerType.worldWindSessions);
                break;
            case route:
                mv.selectSpecializedViewer(ViewerType.worldWindPlaces);
                break;
            case data:
                mv.selectSpecializedViewer(ViewerType.dataViewer);
                break;
        }
        mv.setAvesObjectsList(avesObjects);
        mv.runSpecializedViewers();
    }
}
