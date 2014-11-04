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
    private String mvSelectedType;
    private boolean callIsFromAvesViewer;
    private Phase currentPhase;
    
    public AvesManagerImpl() {

        fullScreen = false;             // toggle fullscreen
        callIsFromAvesViewer = false;

        factory = new FactoryImpl();

        mv = new AvesViewerImpl(this);
        mv.setFullScreen(fullScreen);
        mv.getContentPane().setBackground(Color.black);
        this.setPhase(Phase.session); // the first phase is to show the sessions
//        this.produceAndShow(currentPhase);
        
    }

    /**
     * {@inheritDoc }
     * <p>
     *  
     */
    @Override
    public void viewerEvent() {
        currentPhase = Phase.session;
        callIsFromAvesViewer = true;
        mvSelected = mv.getSelectedItem();
        mvSelectedType = mv.getSelectedItemType();
        System.out.println("Manager receives:" + mvSelected);
        System.out.println("Manager receives type:" + mvSelectedType);
        if ("spot".equals(mvSelectedType)) {
            currentPhase = Phase.LOCATIONS;
        }
        if (currentPhase != null) {
            this.setPhase(currentPhase);
        }
        if(mvSelectedType == "key") {
           	this.setPhase(Phase.session);
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
                if (callIsFromAvesViewer && mvSelectedType == "sessionButton") {
                    factory.setRequestedItem(mvSelected);
                    currentPhase = Phase.LOCATIONS;
                    System.out.println("from session to LOCATIONS phase");
                    callIsFromAvesViewer = false;
                } else {
                    currentPhase = Phase.session;                	
                }
                break;
            case LOCATIONS:
                if (callIsFromAvesViewer && mvSelectedType == "key") {
//                  factory.setRequestedItem(mvSelected);
                  currentPhase = Phase.session;
                  System.out.println("from LOCATIONS to sesion phase");
                  if (mv.getKeyEventType().getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                	  
                  }
                  callIsFromAvesViewer = false;
              } else {
            	  factory.setRequestedItem(mvSelected);
            	  System.out.println("from LOCATIONS to DATA phase");
            	  currentPhase = Phase.DATA;
              }
                break;
            case DATA:
                System.out.println("from DATA to LOCATIONS phase");
                currentPhase = Phase.LOCATIONS;
                break;
        }
        this.requestShow(currentPhase);
    }
    
    /**
     * 
     * {@inheritDoc } 
     */
    public void requestShow(Phase currentPhase) {
        ArrayList<AvesObject> avesObjectList = new ArrayList<AvesObject>();
        makeObjectsInFactory(currentPhase);
        avesObjectList = (ArrayList)requestObjectsFromFactory();
        requestDisplayObjectsInViewer(currentPhase, avesObjectList);      
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
                mode = ProductionMode.SESSIONSMODE;
                break;
            case LOCATIONS:     //request FactoryImpl to maintain list of locations
                //for a specific session
                mode = ProductionMode.LOCATIONSMODE;
                break;
            case DATA:      //request FactoryImpl to maintain list of Data 
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
     */
    public void requestDisplayObjectsInViewer(Phase phase, List<? extends AvesObject> avesObjects) {
        mv.setAvesObjectsList(avesObjects);
        mv.requestObjectsInViewer(phase);
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
            	System.out.println("escape pressed");
                this.setPhase(Phase.session);
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
