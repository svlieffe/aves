package aves.dpt.impl.viewers;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import aves.dpt.impl.production.AvesObjectImpl;
import aves.dpt.intf.production.AvesObject;
import aves.dpt.intf.production.AvesObject.AvesObjectType;
import aves.dpt.intf.viewers.AvesViewer;
import aves.dpt.intf.viewers.DataViewer.DataViewerEvent;
import aves.dpt.intf.ctrl.AvesEventManager;
import aves.dpt.intf.ctrl.AvesManager.Phase;

import java.awt.event.*;

import javax.swing.JFrame;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.FlowLayout;
import java.awt.Dimension;

import javax.swing.JButton;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;

import java.awt.Color;

import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.animation.RotateToAngleAnimator;
import gov.nasa.worldwind.animation.MoveToPositionAnimator;
import gov.nasa.worldwind.util.PropertyAccessor.PositionAccessor;
import gov.nasa.worldwind.view.ViewPropertyAccessor;
import gov.nasa.worldwind.util.PropertyAccessor.AngleAccessor;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

/**
 * 
 * Basic implementation of a {@link aves.dpt.intf.viewers.AvesViewer}. Manages
 * the content-specific viewers.
 * @author svlieffe
 * 2012/03/29
 */
public class AvesViewerImpl extends JFrame implements AvesViewer, AvesEventManager, ActionListener, SelectListener, KeyListener  {

	private ViewerType type;
    private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private GraphicsDevice gd = ge.getDefaultScreenDevice();
    boolean isFullScreen = gd.isFullScreenSupported();
    boolean needGraphics = true;
    private ArrayList<AvesObjectImpl> avesObjects = new ArrayList<AvesObjectImpl>();
    private ArrayList<JButton> buttonList;
    private Dimension dim = new Dimension(720, 540);
    private String selectedItem;
    private EventItemType eventItemType;
    private KeyEvent keyEvent;
    private AvesEventManager avesEventMgr;
    private WorldWindViewerImpl wwv;
    private DataViewerImpl dv;
    private final Integer borderFraction;
    private Phase currentPhase;
   
    public AvesViewerImpl(AvesEventManager avesEventMgr) {
    	this.avesEventMgr = avesEventMgr;
        borderFraction = 10;
        addKeyListener(this);
        setFocusable(true);
        // avoid shifting focus by pressing for example the TAB key
        setFocusTraversalKeysEnabled(false);
    }
    
    /**
     * 
     * {@inheritDoc }
     */
    public void setCurrentPhase(Phase phase) {
    	currentPhase = phase;
    }
    
    /**
     * 
     * {@inheritDoc }
     */
    public Phase getCurrentPhase() {
    	return currentPhase;
    }

    /**
     * {@inheritDoc }
     * <p>
     * unused 
     */ 
    @Override
    public void avesViewerEvent() { }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void dataViewerEvent() {
    	switch(dv.getEvent()) {
    		case UPDATE:
    			this.validate();
    			break;
    		case ENDSHOW:
    			this.closeDataViewer();
    			setCurrentPhase(Phase.PLACES);
    		default:
    			break;    		
    	}
    }

    
    /**
     * 
     * {@inheritDoc }
     * @param actionEvent 
     */
	@Override
    public void actionPerformed(ActionEvent actionEvent) {
            selectedItem = actionEvent.getActionCommand();
            eventItemType = EventItemType.JOURNEYBUTTON;
            avesEventMgr.avesViewerEvent();
    		setCurrentPhase(Phase.PLACES);
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param selectEvent 
     */
	@Override
    @SuppressWarnings({})
    public void selected(SelectEvent selectEvent) {
        if (selectEvent == null) {
            return;
        }

        PickedObject topPickedObject = selectEvent.getTopPickedObject();

        if (selectEvent.getEventAction() == SelectEvent.LEFT_CLICK) {
            if (topPickedObject != null && topPickedObject.getObject() instanceof NamedSpot) {
                NamedSpot selected = (NamedSpot) topPickedObject.getObject();
                selectedItem = selected.getName();
                eventItemType = selected.getType();
                avesEventMgr.avesViewerEvent();
           } else {
                return;
            }
        }
    }

    /**
     * 
     * {@inheritDoc }
     */
	@Override
    public String getSelectedItem() {
        return selectedItem;
    }
    
    /**
     * 
     * {@inheritDoc }
     */
	@Override
    public EventItemType getSelectedItemType() {
        return eventItemType;
    }
    
    /**
     * 
     * {@inheritDoc }
     */
	@Override
    public KeyEvent getKeyEventType() {
        return keyEvent;
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param phase 
     */
    public void selectSpecializedViewer(Phase phase) {
        switch (phase) {
        	case JOURNEYS: 
        		type = ViewerType.worldWindSessions;
        		break;
        	case PLACES:
        		type = ViewerType.worldWindPlaces;
        		break;
        	case DOCUMENTS:
        		type = ViewerType.dataViewer;
        		break;
        }
    }

    /**
     * 
     * {@inheritDoc }
     * @param objectList 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void setAvesObjectsList(List<? extends AvesObject> objectList) {
        avesObjects = (ArrayList)objectList;
    }
    
    /**
     * 
     * {@inheritDoc }
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "incomplete-switch" })
	@Override
    public void requestObjectsInViewer() {
    	selectSpecializedViewer(currentPhase);
        switch (type) {
            case worldWindSessions: {
            	buttonList = new ArrayList<JButton>();
                getLayeredPane().setLayout(new FlowLayout());

                for (AvesObject mo : avesObjects) {
                    AvesObjectType objectType = mo.getObjectType();
                    ArrayList<String> values = new ArrayList<String>();
                    values = (ArrayList) mo.getDataValues();
                    switch (objectType) {
                        case JOURNEYS:
                            for (String vi : values) {
                                JButton button;
                                button = new JButton(vi);
                                button.addActionListener(this);
                                getLayeredPane().add(button, new Integer(1));
                                buttonList.add(button);
                            }
                            break;
                    }
                }
                if (!isDisplayable()) {
                    displayViewer();
                }
                wwv = new WorldWindViewerImpl();
                wwv.initComponents(dim);
                getLayeredPane().setLayout(new java.awt.BorderLayout());
                getLayeredPane().add(wwv, java.awt.BorderLayout.CENTER, new Integer(-1));
                break;
            }
            case worldWindPlaces: {
                NamedSpot spot, firstSpot = null;
                
                String spotName;
                BasicShapeAttributes attrs = new BasicShapeAttributes();
                initBasicShapeAttrs(attrs);

                RenderableLayer layer;
                layer = new RenderableLayer();
                layer.setPickEnabled(true);
                this.wwv.getwwPanel().addSelectListener(this);
                this.wwv.getwwPanel().addKeyListener(this);
                this.wwv.getwwPanel().getModel().getLayers().add(0, layer);                
 
                Iterator<JButton> jb = buttonList.iterator();
                while (jb.hasNext()) {
                    getLayeredPane().remove(jb.next());
                }

                boolean firstspt = true;
                for (AvesObject mo : avesObjects) {
                    AvesObjectType objectType = mo.getObjectType();
                    ArrayList<String> values = new ArrayList<String>();
                    values = (ArrayList) mo.getDataValues();
                    switch (objectType) {
                        case PLACES:
                            Iterator vi = values.iterator();
                            while (vi.hasNext()) {
                                spotName = (String) vi.next();
                                spot = new NamedSpot(spotName, LatLon.fromDegrees(new Double((String) vi.next()),
                                        new Double((String) vi.next())), 100.0);
                                if (firstspt) {
                                    firstSpot = spot;
                                    firstspt = false;
                                }
                                layer.addRenderable(spot);

                            }
                            MoveToPositionAnimator mpa;
                            final PositionAccessor eyePositionAccessor = ViewPropertyAccessor.createEyePositionAccessor((this.wwv.getwwPanel().getView()));

                            mpa = new MoveToPositionAnimator(
                                    this.wwv.getwwPanel().getView().getCurrentEyePosition(),
                                    new Position(firstSpot.getPos(), 1.5e6), 0.91, eyePositionAccessor);

                            mpa.start();
                            this.wwv.getwwPanel().getView().stopAnimations();
                            this.wwv.getwwPanel().getView().addAnimator(mpa);

                            final AngleAccessor angleAccessor = ViewPropertyAccessor.createPitchAccessor((this.wwv.getwwPanel().getView()));

                            @SuppressWarnings("unused")
							RotateToAngleAnimator mraa = new RotateToAngleAnimator(Angle.fromDegrees(0.0), Angle.fromDegrees(80.0), 0.91, angleAccessor);
                            requestFocusInWindow();                                

                            break;
                    }
                }
                break;
            }
            case dataViewer: {
                AvesObjectType objectType = AvesObjectType.DOCUMENTS;
                switch (objectType) { //this switch is unused but can be used for other types than documents
                    case DOCUMENTS:
                        switch (objectType) {
                            case DOCUMENTS:
                                this.getLayeredPane().moveToBack(wwv);
                                dv = new DataViewerImpl(this, this);
                                if (isFullScreen) {
                                    this.validate();
                                } else {
                                    this.pack();
                                    this.setVisible(true);
                                }
                                JPanel left = new JPanel();
                                JPanel right= new JPanel();
                                JPanel bottom = new JPanel();
                                JPanel top = new JPanel();
                                
                                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

                                left.setSize(dim.width / borderFraction, dim.height);
                                right.setSize(dim.width / borderFraction, dim.height);
                                top.setSize(dim.width, dim.height / borderFraction);
                                bottom.setSize(dim.width, dim.height / borderFraction);
                                left.setOpaque(false);
                                right.setOpaque(false);
                                top.setOpaque(false);
                                bottom.setOpaque(false);
                                getLayeredPane().setLayout(new java.awt.BorderLayout());
                                getLayeredPane().add(left, java.awt.BorderLayout.WEST, new Integer(300));
                                getLayeredPane().add(right, java.awt.BorderLayout.EAST, new Integer(300));
                                getLayeredPane().add(top, java.awt.BorderLayout.NORTH, new Integer(300));
                                getLayeredPane().add(bottom, java.awt.BorderLayout.SOUTH, new Integer(300));
                                                                                                                            
                                dv.setObjectsToDisplay((List) avesObjects);
                                getLayeredPane().add(dv, java.awt.BorderLayout.CENTER, new Integer(300));
                                this.getLayeredPane().moveToFront(dv);
                                dv.setSize(new Dimension(this.getWidth(), this.getHeight()));
                                dv.setOpaque(true);
                                dv.setBackground(new Color(0, 0, 0));
                                try {
                                    dv.displayNext();
                                } catch (Exception ex) {
                                    System.out.println("error opening DataViewer:" + ex);
                                }
                                break;
                        }
                }
            }
            break;
        }
    }
    

    /**
     * 
     * {@inheritDoc }
     * @param val 
     */
    @Override
    public void setFullScreen(boolean val) {
        isFullScreen = val;
    }
    
    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void closeDataViewer() {

        dv.setVisible(false);
        this.getLayeredPane().moveToBack(dv);

        wwv.setVisible(true);
        this.getLayeredPane().moveToFront(wwv);
        
        if (isFullScreen) {
            this.validate();
        } else {
            this.pack();
            this.setVisible(true);
        }
    }
   
    /**
     * 
     * Shows the {@link aves.dpt.intf.viewers.AvesViewer}, 
     * full screen or in a window.
     */
    private void displayViewer() {
        setUndecorated(isFullScreen);
        setResizable(!isFullScreen);
        this.setDefaultCloseOperation(AvesViewerImpl.EXIT_ON_CLOSE);
        if (isFullScreen) {
            // Full-screen mode
            if (needGraphics) {
                gd.setFullScreenWindow(this);
                needGraphics = false;
            }
            validate();
        } else {
            // Windowed mode
            this.setPreferredSize(dim);
            pack();
            setVisible(true);
        }
    }
         
    /**
     * 
     * The object used to mark a specific location in the 
     * WorldWind RenderableLayer.
     */
    private class NamedSpot extends PointPlacemark {
        
        String placeName;
        EventItemType itemType = EventItemType.LOCATIONSPOT;
        LatLon spotPos;
        
        public NamedSpot(String name, LatLon latLon, Double diam) {
            super(new Position(latLon, 1.5e5));
            this.placeName = name;
            this.spotPos = latLon;
            super.setLabelText(name);
            super.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
            PointPlacemarkAttributes ppattrs = new PointPlacemarkAttributes();
            ppattrs = new PointPlacemarkAttributes();
            ppattrs.setLabelScale(0.8);
            ppattrs.setLabelColor("ffffffff");
            ppattrs.setLineColor("ff0000ff");
            ppattrs.setUsePointAsDefaultImage(true);
            ppattrs.setScale(5d);
            super.setAttributes(ppattrs);
        }
        
        public String getName() {
            return placeName;
        }
        
        public EventItemType getType() {
            return itemType;
        }
        
        public LatLon getPos() {
            return spotPos;
        }
        
    }
    
    /**
     * 
     * Initializes {@link aves.dpt.impl.viewers.AvesViewerImpl.NamedSpot} 
     * object used to mark a specific location in the 
     * WorldWind RenderableLayer.
     * @param attrs 
     */
    private void initBasicShapeAttrs(BasicShapeAttributes attrs) {
        attrs.setInteriorMaterial(Material.WHITE);
        attrs.setOutlineMaterial(Material.RED);//new Material(WWUtil.makeColorBrighter(Color.RED)));
        attrs.setInteriorOpacity(1.0);
        attrs.setOutlineOpacity(0.8);
        attrs.setOutlineWidth(2d);
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public List<LatLon> makeRoute() {
        ArrayList<LatLon> listOfPlaces = new ArrayList<LatLon>();

        return listOfPlaces;
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void displayRoute(List<? extends LatLon> listOfCoords) {
    }
    
    /**
     * 
     * {@inheritDoc }
     * <p>
     * unused.
     */
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * 
     * {@inheritDoc }
     * Left and right arrow to navigate forward and backward. Escape key to return to general view.
     * 
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();
        if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
            try {
                dv.displayNext();
            } catch (Exception e) {
                System.out.println(e);
            }     
        }
        if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
            try {
                dv.displayPrev();
            } catch (Exception e) {
                
            }     
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            try {
                switch (currentPhase) {
                case JOURNEYS:
                	System.exit(0);
                	break;
                case PLACES:
                	eventItemType = EventItemType.ESCKEY;
                	avesEventMgr.avesViewerEvent();
                	break;
                case DOCUMENTS:
                	closeDataViewer();
                	dv.setEvent(DataViewerEvent.ENDSHOW);
                    this.dataViewerEvent();                	
				default:
					break;                	
                }
            } catch (Exception e) {
            	System.out.print(e);              
            }     
        }
    }

    /**
     * 
     * {@inheritDoc }
     * <p>
     * unused
     * 
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
