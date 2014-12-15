package aves.dpt.impl.ctrl;

import aves.dpt.impl.production.FactoryImpl;
import aves.dpt.impl.viewers.AvesViewerImpl;
import aves.dpt.intf.ctrl.AvesManager;
import aves.dpt.intf.ctrl.AvesEventManager;
import aves.dpt.intf.production.AvesObject;
import aves.dpt.intf.production.Factory.ProductionMode;

import java.util.ArrayList;
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
 * 2012/03/29
 */
public class AvesManagerImpl implements AvesManager, AvesEventManager, KeyListener {

    private AvesViewerImpl mv;//
    private ProductionMode mode;
    private FactoryImpl factory;
    private boolean fullScreen;
    private String mvSelected;
    private Phase currentPhase;
    
    public AvesManagerImpl() {

        fullScreen = true;             // toggle fullscreen

        factory = new FactoryImpl();
        mv = new AvesViewerImpl(this);

        // the first phase is to show the sessions (=JOURNEYS)
        this.setPhase(Phase.JOURNEYS);
        this.requestShow(currentPhase);
    }

    /**
     * 	
     * section to manage events
     *
     **/

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void dataViewerEvent() { }

    /**
     * 
     * {@inheritDoc } 
     */
    @Override
    public void avesViewerEvent() {
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
    }

	/**
     * 
     * {@inheritDoc }
     */
    @Override
    public void setPhase(Phase phase) {
    	currentPhase = phase;
    }
    
    /**
     * 
     * {@inheritDoc } 
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void requestShow(Phase currentPhase) {
    	// placeholder for objects
        ArrayList<AvesObject> avesObjectList = new ArrayList<AvesObject>();
        // produce the objects in the factory
        makeObjectsInFactory(currentPhase);
        // put the objects in the placeholder
        avesObjectList = (ArrayList) requestObjectsFromFactory();
        // now the objects are ready, request to display them
        requestDisplayObjectsInViewer(currentPhase, avesObjectList);      
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void requestDisplayObjectsInViewer(Phase phase, List<? extends AvesObject> avesObjects) {
    	mv.invalidate();
        mv.setFullScreen(fullScreen);
        mv.getContentPane().setBackground(Color.black);
        mv.setAvesObjectsList(avesObjects);
        // tell the viewer the current phase
        mv.setCurrentPhase(phase);
        // delegates the display of the objects of the current phase to the AvesViewer
        mv.requestObjectsInViewer();

    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void makeObjectsInFactory(Phase currentPhase) {
        //creates a FactoryImpl that maintains a list of AvesObjects
        //AvesObjects are of a certain type (JOURNEYS, PLACES, DOCUMENTS)
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
    @Override
    public List<? extends AvesObject> requestObjectsFromFactory() {
        
        ArrayList<AvesObject> avesObjectList = new ArrayList<AvesObject>();

        avesObjectList = (ArrayList<AvesObject>) factory.listOfObjects();
        
        return avesObjectList;
    }
    
    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * 
     * {@inheritDoc }
     * <p>
     * Left and right arrow to navigate forward and backward. Escape key to return to general view.
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();
//        System.out.println("key pressedin dataviewr:" + keyCode);
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            try {
//            	System.out.println("escape pressed in Sessions");
                System.exit(0);
            } catch (Exception e) {
                System.out.println(e);
            }     
        }
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

}