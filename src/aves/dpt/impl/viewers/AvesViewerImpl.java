/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aves.dpt.impl.viewers;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import aves.dpt.impl.production.AvesObjectImpl;
import aves.dpt.intf.production.AvesObject;
import aves.dpt.intf.production.AvesObject.AvesObjectType;
import aves.dpt.intf.viewers.AvesViewer;
//import aves.dpt.intf.viewers.DataViewer.DataViewerEvent;
import aves.dpt.intf.viewers.AvesViewer.EventItemType;
import aves.dpt.intf.viewers.DataViewer.DataViewerEvent;
import aves.dpt.intf.ctrl.AvesEventManager;
import aves.dpt.intf.viewers.AvesViewer.ViewerType;
import aves.dpt.intf.ctrl.*;
import aves.dpt.intf.ctrl.AvesManager.Phase;

import java.awt.event.*;

import javax.swing.JFrame;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.FlowLayout;
import java.awt.Dimension;

import javax.swing.JButton;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.Color;
import java.awt.Point;

import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.animation.RotateToAngleAnimator;
import gov.nasa.worldwind.animation.MoveToPositionAnimator;
import gov.nasa.worldwind.animation.AnimationController;
import gov.nasa.worldwind.util.PropertyAccessor.PositionAccessor;
import gov.nasa.worldwind.view.ViewPropertyAccessor;
import gov.nasa.worldwind.util.PropertyAccessor.AngleAccessor;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
/**
 * Basic implementation of a {@link aves.dpt.intf.viewers.AvesViewer}. Manages
 * the content-specific viewers.
 * @author svlieffe
 * 
 * @version $Id: AvesViewerImpl.java,v a1656ba63334 2012/03/31 20:52:12 svlieffe $
 */
public class AvesViewerImpl extends JFrame implements AvesViewer, AvesEventManager, ActionListener, SelectListener, KeyListener  {

	private ViewerType type;
    private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private GraphicsDevice gd = ge.getDefaultScreenDevice();
    boolean isFullScreen = gd.isFullScreenSupported();
    boolean needGraphics = true;
    private ArrayList<AvesObjectImpl> avesObjects = new ArrayList<AvesObjectImpl>();
    private Iterator<AvesObjectImpl> e;
//    private ArrayList<Integer> buttonIndexes = new ArrayList<Integer>();
    private ArrayList<JButton> buttonList;
    private Dimension dim = new Dimension(720, 540);
    private String selectedItem;
    private EventItemType eventItemType;
    private KeyEvent keyEvent;
    private AvesEventManager avesEventMgr;
    private Integer count = 0;
    private WorldWindViewerImpl wwv;
    private DataViewerImpl dv;
    private final Integer borderFraction;
    private Phase currentPhase;
//    private AvesManager parent;
   
    public AvesViewerImpl(AvesEventManager avesEventMgr) {
    	this.avesEventMgr = avesEventMgr;
        borderFraction = 10;
        addKeyListener(this);
        setFocusable(true);
        // avoid shifting focus by pressing for example the TAB key
        setFocusTraversalKeysEnabled(false);
    }
    
    /**
     * {@inheritDoc }
     * <p>
     *  
     */
    public void setCurrentPhase(Phase phase) {
    	currentPhase = phase;
    }
    
    /**
     * {@inheritDoc }
     * <p>
     *  
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
	public Object getAvesEventSubject() {
		return null;
	}
	
    /**
     * {@inheritDoc }
     * <p>
     * unused 
     */
    @Override
	public void setAvesEventSubject(Object subject){ }
	
    /**
     * {@inheritDoc }
     * <p>
     * unused 
     */
    @Override
    public void avesViewerEvent() { }

    /**
     * {@inheritDoc }
     * <p>
     *  
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
     * {@inheritDoc }
     * <p>
     * @param ae 
     */
	@Override
    public void actionPerformed(ActionEvent ae) {
        // JOURNEYS selection takes place once only //20141104 removed restriction because this is deprecated
    	System.out.println("action performed button clicked in Avesviewer");
//        if (count == 0) {
            //JButton source = (JButton) ae.getSource();
            //selectedSession = source.getText();
            selectedItem = ae.getActionCommand();
            eventItemType = EventItemType.JOURNEYBUTTON;
            avesEventMgr.avesViewerEvent();
    		setCurrentPhase(Phase.PLACES);
//            count = 1;
//        }
        
    }
    
    @SuppressWarnings({"StringEquality"})
    public void selected(SelectEvent e) {
        if (e == null) {
            return;
        }

        PickedObject topPickedObject = e.getTopPickedObject();

        if (e.getEventAction() == SelectEvent.LEFT_CLICK) {
            if (topPickedObject != null && topPickedObject.getObject() instanceof NamedSpot) {
                NamedSpot selected = (NamedSpot) topPickedObject.getObject();
                System.out.println(selected.getName());
                selectedItem = selected.getName();
                eventItemType = selected.getType();
                avesEventMgr.avesViewerEvent();
                //NamedSpot spotName = (NamedSpot) e.getSource();
                //System.out.println(spotName.getName());
           } else {
                System.out.println("brol");
            }
        }
    }
    
    public String getSelectedItem() {
        return selectedItem;
    }
    
    public EventItemType getSelectedItemType() {
        return eventItemType;
    }
    
    public KeyEvent getKeyEventType() {
        return keyEvent;
    }
    
    /**
     * {@inheritDoc }
     * <p>
     * @param viewertype 
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

    public void setAvesObjectsList(List<? extends AvesObject> objectList) {
        avesObjects = (ArrayList)objectList;
    }
    
    /**
     * {@inheritDoc }
     * <p>
     */
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
                getLayeredPane().add(wwv, java.awt.BorderLayout.CENTER, new Integer(-1));// new Integer(0));
//                validate();
//                pack();
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
//                this.wwv.getwwPanel().setFocusable(true);
                this.wwv.getwwPanel().getModel().getLayers().add(0, layer);                
 
                Iterator<JButton> jb = buttonList.iterator();
                while (jb.hasNext()) {
                    getLayeredPane().remove(jb.next());
                }

                boolean first = true;
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
                                //spot.setAttributes(attrs);
 /*                               spot.setAttributes(attrs);
                                layer.addRenderable(spot);
                                 */
                                if (first) {
                                    firstSpot = spot;
                                    first = false;
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
                            //this.wwv.getwwPanel().getView().goTo(new Position(firstSpot.getPos(), 1.5e6), 1.5e6);
                            
//                            AnimationController animationCtrl = new AnimationController();
//                            
//                            animationCtrl.put(new Integer(1), mpa);
                            
                            /*                                   this.wwv.getwwPanel().getView().setEyePosition(
                            new Position(spot.getPos(), 1.5e6));  //Position.fromDegrees(47.0844, 2.3964, 1.5e5));//1.5e7)); // for mathilde 2.50e9
                            System.out.println(spot.getName());
                             */
                            RotateToAngleAnimator mraa;
                            final AngleAccessor angleAccessor = ViewPropertyAccessor.createPitchAccessor((this.wwv.getwwPanel().getView()));

                            mraa = new RotateToAngleAnimator(Angle.fromDegrees(0.0), Angle.fromDegrees(80.0), 0.91, angleAccessor);
//                            mraa.start();
//                            this.wwv.getwwPanel().getView().stopAnimations();
//                            this.wwv.getwwPanel().getView().addAnimator(mraa);

//                            animationCtrl.put(new Integer(2), mraa);
                            
//                            animationCtrl.startAnimation(1);
                            
                            //while(animationCtrl.hasActiveAnimation());
                            
                            //animationCtrl.startAnimation(2);
                            
//                            validate();
//                            pack();
                            break;
                    }

                    }
                break;
            }
            case dataViewer: {
                AvesObjectType objectType = AvesObjectType.DOCUMENTS;
                switch (objectType) { //this switch is unused but can be used for other types than documents (?)
                    case DOCUMENTS:
                        switch (objectType) {
                            case DOCUMENTS:
                                //wwv.setVisible(false);
                                this.getLayeredPane().moveToBack(wwv);
                                dv = new DataViewerImpl(this, this);
                                if (isFullScreen) {
                                    this.validate();
                                } else {
                                    this.pack();
                                    this.setVisible(true);
                                }
//                                dv.repaint();
                                JPanel left = new JPanel();
                                JPanel right= new JPanel();
                                JPanel bottom = new JPanel();
                                JPanel top = new JPanel();
                                
                                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

                                // add transparent border
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
                                                                                            
                                System.out.println("DataViewer width: " + this.getWidth());
                                System.out.println("DataViewer height: " + this.getHeight());
                                
                                dv.setObjectsToDisplay((List) avesObjects);
                                //getLayeredPane().setLayout(new java.awt.FlowLayout());
                                //getLayeredPane().add(dv, java.awt.FlowLayout.LEFT);
                                getLayeredPane().add(dv, java.awt.BorderLayout.CENTER, new Integer(300));
//                                dv.requestFocusInWindow();                                
                                this.getLayeredPane().moveToFront(dv);
                                dv.setSize(new Dimension(this.getWidth(), this.getHeight()));
                                dv.setOpaque(true);
                                dv.setBackground(new Color(0, 0, 0));
                                try {
                                    dv.displayNext();
                                } catch (Exception ex) {
                                    System.out.println("error opening DataViewer:" + ex);
                                 //   Logger.getLogger(AvesViewerImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                //validate(); //blacks out dataviewer??
                                //pack(); //blacks out dataviewer??
                                break;
                        }
                }
            }
            break;
        }
    }
    

    /**
     * Sets the viewer full screen to true or false
     * @param val 
     */
    public void setFullScreen(boolean val) {
        isFullScreen = val;
    }
    
    /**
     * {@inheritDoc }
     * 
     */
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
        
//        ve.setPhase(Phase.PLACES);
  
    }
   
    /**
     * Shows the {@link aves.dpt.intf.viewers.AvesViewer}, 
     * full screen or in a window.
     * 
     * <p>
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
            //super.setValue(AVKey.DISPLAY_NAME, "Clamp to ground, White label, Red point, Scale 5");
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
     * {@inheritDoc }
     * <p>
     */
    public List<LatLon> makeRoute() {
        ArrayList<LatLon> listOfPlaces = new ArrayList<LatLon>();

        return listOfPlaces;
    }

    /**
     * {@inheritDoc }
     * <p>
     */
    public void displayRoute(List<? extends LatLon> listOfCoords) {
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
        System.out.println("key pressedin avasviewerimpl__:" + keyCode);
//    	eventItemType = EventItemType.ESCKEY;
        if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) { //right arrow
            try {
                dv.displayNext();
            } catch (Exception e) {
                
            }     
        }
        if (keyCode == java.awt.event.KeyEvent.VK_LEFT) { //left arrow
            try {
                dv.displayPrev();
            } catch (Exception e) {
                
            }     
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) { //escape
            try {
            	System.out.println("escape AvesViewerImpl pressed:" + keyCode);
                System.out.println(currentPhase);
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
                	dv.setEvent(DataViewerEvent.ENDSHOW);//parent.closeDataViewer();
                    this.dataViewerEvent();                	
				default:
					break;                	
                }
/*                if (currentPhase == Phase.JOURNEYS) {
                	System.exit(0);
                } else {
                	eventItemType = EventItemType.ESCKEY;
                	ve.viewerEvent();
//                	removeAll();
//                	revalidate();
//                	repaint();
                }*/
//                dispose();
                //invalidate();
//                getLayeredPane().remove(wwv);
//            	keyEvent = ke;
/*                selectedItem = ae.getActionCommand();
                eventItemType = "sessionButton";*/
//                ve.viewerEvent();
/*                ve.setPhase(Phase.JOURNEYS);
                ve.produceAndShow(Phase.JOURNEYS);*/
//                selectSpecializedViewer(ViewerType.worldWindSessions);
//                runSpecializedViewers();
            } catch (Exception e) {
            	System.out.print(e);              
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
