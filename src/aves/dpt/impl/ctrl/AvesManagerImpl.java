/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private String mvSelected;
    private Phase currentPhase;
	private FileInputStream cfg = null;
	private File config = new File("./config/config.txt");
	private String fullscreen;
    private boolean fullScreen;
    
    public AvesManagerImpl() {

        // toggle fullscreen
        fullscreen = ReadConfig("fullscreen");
        if (fullscreen.equals("Y")) {
        	fullScreen = true;  
        } else {
        	fullScreen = false;
        }
        
        factory = new FactoryImpl();
        mv = new AvesViewerImpl(this);

        // the first phase is to show the sessions (=JOURNEYS)
        this.setPhase(Phase.JOURNEYS);
        this.requestShow(currentPhase);
    }
    
    /**
     * read the config.txt file (very basic)
     */
    private String ReadConfig(String element) {
    	BufferedReader br = null;
    	String value = "";
		try {
			br = new BufferedReader(new FileReader(config));
		} catch (FileNotFoundException e) {
			System.out.println("error reading config.txt");
			e.printStackTrace();
		}
    	String line;
    	try {
			while ((line = br.readLine()) != null) {
			   // process the line.
			    String [] words = line.split("\\s+");
			    if (words[0].equals(element)) {
			    	value = words[1].toString();
			    };
			    }
		} catch (IOException e) {
			e.printStackTrace();
		}
    	try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return value;
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
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            try {
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