package aves.dpt.impl.ctrl;

import java.util.ArrayList;

//import aves.dpt.impl.production.AvesObjectImpl;
//import aves.dpt.intf.production.AvesObject.AvesObjectType;



import aves.dpt.impl.production.FactoryImpl;
import aves.dpt.impl.viewers.AvesViewerImpl;
import aves.dpt.intf.ctrl.AvesManager;
import aves.dpt.intf.ctrl.AvesManager.Phase;
import aves.dpt.intf.production.AvesObject;
import aves.dpt.intf.production.Factory.ProductionMode;
import aves.dpt.intf.viewers.ViewerEvent;
import aves.dpt.intf.viewers.AvesViewer.ViewerType;



//import gov.nasa.worldwind.geom.LatLon;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class AvesManagerImpl implements AvesManager, ViewerEvent, KeyListener {
//  System.exit(0);

    private AvesViewerImpl mv;//
    private ProductionMode mode;
    private FactoryImpl factory;
    private boolean fullScreen;
    private String mvSelected;
//    private String mvSelectedType;
//    private boolean callIsFromAvesViewer;
    private Phase currentPhase;
    
    public AvesManagerImpl() {

        fullScreen = false;             // toggle fullscreen
//        callIsFromAvesViewer = false;

        factory = new FactoryImpl();
        mv = new AvesViewerImpl(this);

        // the first phase is to show the sessions
        this.setPhase(Phase.JOURNEYS);
        this.requestShow(currentPhase);

        //        this.produceAndShow(currentPhase);
        
    }

    /**
     * {@inheritDoc }
     * <p>
     *  
     */
    @Override
    public void viewerEvent() {
//        currentPhase = Phase.JOURNEYS;
//        System.out.println("Phase I:" + currentPhase);
//        callIsFromAvesViewer = true;
        mvSelected = mv.getSelectedItem();
        switch(mv.getSelectedItemType()) {
        case JOURNEYBUTTON:
            factory.setRequestedItem(mvSelected);
            this.setPhase(Phase.PLACES);
        	break;
        case LOCATIONSPOT:
        	factory.setRequestedItem(mvSelected);
            this.setPhase(Phase.DOCUMENTS);
        	break;
        case ESCKEY:
        	// dispose the AvesViewer JFrame and restart from scratch
        	mv.dispose();
           	this.setPhase(Phase.JOURNEYS);
            mv = new AvesViewerImpl(this);
        	break;
        default:
        	break;
        }
        this.requestShow(currentPhase);
/*        mvSelectedType = mv.getSelectedItemType();
        System.out.println("Manager receives:" + mvSelected);
        System.out.println("Manager receives type:" + mvSelectedType);
        if ("spot".equals(mvSelectedType)) {
            currentPhase = Phase.PLACES;
        }
        if (currentPhase != null) {
            this.setPhase(currentPhase);
        }
        if(mvSelectedType == "key") {
           	this.setPhase(Phase.JOURNEYS);
        }*/
    }

    /**
     * {@inheritDoc }
     * 
     * @param phase 
     */
    public void setPhase(Phase phase) {
    	currentPhase = phase;
    }
    
    /**
     * 
     * {@inheritDoc } 
     */
    public void requestShow(Phase currentPhase) {
    	// placeholder for objects
        ArrayList<AvesObject> avesObjectList = new ArrayList<AvesObject>();
        // produce the objects in the factory
        makeObjectsInFactory(currentPhase);
        // put the objects in the placeholder
        avesObjectList = (ArrayList)requestObjectsFromFactory();
        // now the objects are ready, request to display them
        requestDisplayObjectsInViewer(currentPhase, avesObjectList);      
    }

    /**
     * {@inheritDoc }
     */
    public void requestDisplayObjectsInViewer(Phase phase, List<? extends AvesObject> avesObjects) {
    	mv.invalidate();
        mv.setFullScreen(fullScreen);
        mv.getContentPane().setBackground(Color.black);
        mv.setAvesObjectsList(avesObjects);
        // tell the viewer the current phase
        mv.setCurrentPhase(phase);
        // delegates the display of the objects of the current phase to the AvesViewer
        mv.requestObjectsInViewer();

/*        switch (phase) {
        case JOURNEYS:
            if (callIsFromAvesViewer) {// && mvSelectedType == "sessionButton") {
                factory.setRequestedItem(mvSelected);
                phase = Phase.PLACES;
                System.out.println("from JOURNEYS to PLACES phase");
                callIsFromAvesViewer = false;
            } else {
                phase = Phase.JOURNEYS;                	
            }
            break;
        case PLACES:
            if (callIsFromAvesViewer && mvSelectedType == "key") {
//              factory.setRequestedItem(mvSelected);
              phase = Phase.JOURNEYS;
              System.out.println("from PLACES to sesion phase");
              callIsFromAvesViewer = false;
          } else {
        	  factory.setRequestedItem(mvSelected);
        	  System.out.println("from PLACES to DOCUMENTS phase");
        	  phase = Phase.DOCUMENTS;
          }
            break;
        case DOCUMENTS:
            System.out.println("from DOCUMENTS to PLACES phase");
            phase = Phase.PLACES;
            break;
        default:
        	break;
    }*/
    }

    /**
     * 
     * {@inheritDoc }
     */
    public void makeObjectsInFactory(Phase currentPhase) {
        //creates a FactoryImpl that maintains a list of AvesObjects
        //AvesObjects are of a certain type (JOURNEYS, place, document)
        switch (currentPhase) {
            case JOURNEYS:   //request FactoryImpl to maintain list of sessions
                mode = ProductionMode.SESSIONSMODE;
                break;
            case PLACES:     //request FactoryImpl to maintain list of locations
                //for a specific JOURNEYS
                mode = ProductionMode.LOCATIONSMODE;
                break;
            case DOCUMENTS:      //request FactoryImpl to maintain list of Data 
                //for a specific location
                mode = ProductionMode.DATAMODE;
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
     * <p>
     * Unused.
     * 
     */
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * {@inheritDoc }
     * 
     * Left and right arrow to navigate forward and backward. Escape key to return to general view.
     * <p>
     */
    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();
        System.out.println("key pressedin dataviewr:" + keyCode);
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) { //escape
            try {
            	System.out.println("escape pressed in Sessions");
                System.exit(0);
            } catch (Exception e) {
                
            }     
        }
    }

    /**
     * {@inheritDoc }
     * 
     * <p>
     * 
     * Unused
     * 
     */
    public void keyReleased(KeyEvent e) {
    }
    
}
