package aves.dpt.impl.ctrl;

import java.util.ArrayList;

//import aves.dpt.impl.production.MappaObjectImpl;
//import aves.dpt.intf.production.MappaObject.MappaObjectType;

import aves.dpt.impl.production.FactoryImpl;
import aves.dpt.impl.viewers.MappaViewerImpl;
import aves.dpt.intf.ctrl.MappaManager;
import aves.dpt.intf.production.MappaObject;
import aves.dpt.intf.production.Factory.ProductionMode;
import aves.dpt.intf.viewers.ViewerEvent;
import aves.dpt.intf.viewers.MappaViewer.ViewerType;

//import gov.nasa.worldwind.geom.LatLon;
import java.awt.Color;
import java.util.List;

/**
 *
 * This class implements a {@link aves.dpt.intf.ctrl.MappaManager}. It
 * organizes requests for {@link aves.dpt.intf.production.MappaObject}s 
 * from the {@link aves.dpt.intf.production.Factory}
 * and delegates the display of these
 * {@link aves.dpt.intf.production.MappaObject}s to 
 * the {@link aves.dpt.intf.viewers.MappaViewer}. It
 * receives user input as a callback from the {@link aves.dpt.intf.viewers.MappaViewer} 
 * and analyzes this input to make further decisions.
 * 
 * @author svlieffe
 * 
 * @version $Id: MappaManagerImpl.java,v 649d54af3d47 2012/03/29 17:18:33 svlieffe $
 */
public class MappaManagerImpl implements MappaManager, ViewerEvent {

    private MappaViewerImpl mv;//
    private ProductionMode mode;
    private FactoryImpl factory;
    private boolean fullScreen;
    private String mvSelected;
    private String mvSelectedType;
    private boolean callIsFromMappaViewer;

    public MappaManagerImpl() {

        fullScreen = true;             // toggle fullscreen
        callIsFromMappaViewer = false;

        factory = new FactoryImpl();

        mv = new MappaViewerImpl(this);
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
        callIsFromMappaViewer = true;
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
                if (callIsFromMappaViewer) {
                    factory.setRequestedItem(mvSelected);
                    currentPhase = Phase.route;
                    System.out.println("from session to route phase");
                    callIsFromMappaViewer = false;
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
        ArrayList<MappaObject> mappaObjectList = new ArrayList<MappaObject>();
        makeObjectsInFactory(currentPhase);
        mappaObjectList = (ArrayList)requestObjectsFromFactory();
        displayObjectsInViewer(currentPhase, mappaObjectList);      
    }

    /**
     * 
     * {@inheritDoc }
     */
    public void makeObjectsInFactory(Phase currentPhase) {
        //creates a FactoryImpl that maintains a list of MappaObjects
        //MappaObjects are of a certain type (session, place, document)
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
    public List<? extends MappaObject> requestObjectsFromFactory() {
        
        ArrayList<MappaObject> mappaObjectList = new ArrayList<MappaObject>();

        mappaObjectList = (ArrayList) factory.listOfObjects();
        
        return mappaObjectList;
    }

    /**
     * {@inheritDoc }
     */
    public void displayObjectsInViewer(Phase phase, List<? extends MappaObject> mappaObjects) {
    switch (phase) {
            case session: //I believe I should remove the case and only invoke the mappaviewer; the mappaviewer will decide what actions to take 
                mv.selectSpecializedViewer(ViewerType.worldWindSessions);
                break;
            case route:
                mv.selectSpecializedViewer(ViewerType.worldWindPlaces);
                break;
            case data:
                mv.selectSpecializedViewer(ViewerType.dataViewer);
                break;
        }
        mv.setMappaObjectsList(mappaObjects);
        mv.runSpecializedViewers();
    }
}
