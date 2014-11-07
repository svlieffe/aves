package aves.dpt.impl.viewers;

//TODO add button to close DOCUMENTS viewer

import javax.swing.JPanel;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;
import java.awt.event.*;

import aves.dpt.impl.production.AvesObjectImpl;
import aves.dpt.intf.ctrl.AvesEventManager;
import aves.dpt.intf.production.AvesObject.ObjectDataType;
import aves.dpt.intf.viewers.DataNotFoundException;
import aves.dpt.intf.viewers.DataViewer;
import aves.dpt.intf.viewers.DisplaySlideException;

import java.io.IOException;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;


/**
 *
 * Implements the {@link aves.dpt.intf.viewers.DataViewer}. 
 * The current implementation is ready to display DOCUMENTS based on the 
 * {@link aves.dpt.intf.production.AvesObject.ObjectDataType}
 * but can actually only handle to display images as a slide show by using a 
 * {@link aves.dpt.intf.viewers.ImageViewer}.
 * <p>
 * In future versions this interface will invoke a {@link aves.dpt.intf.viewers.ImageViewer}, a 
 * {@link aves.dpt.intf.viewers.TextViewer}, and additional specialized viewers.
 * <p>
 * This implementation can only move forward by pressing the right arrow key.
 * <p>
 * @author svlieffe
 */
public class DataViewerImpl extends JPanel implements DataViewer {//, KeyListener {
    
    private DataViewerType type;
    private DataViewerEvent dataViewerEvent;
    private ImageViewerImpl imV;
    private WebViewerImpl webV;
    private GridBagConstraints gbc;
    private BufferedImage bi;
    private String source;
    private AvesObjectImpl object;
    private List<AvesObjectImpl> objects = new ArrayList<AvesObjectImpl>();
    private ListIterator<AvesObjectImpl> mOIt;
    private List<String> values = new ArrayList<String>();
    private Iterator<String> s;
    private AvesEventManager avesViewer;
    boolean last = false;
    boolean firstIm = true;
    boolean movingBackwards = false;
    
    public DataViewerImpl(AvesEventManager avesViewer) {
        
    	this.avesViewer = avesViewer;
        //set most frequent event
        setEvent(DataViewerEvent.UPDATE);
//        addKeyListener(this);
//        setFocusable(true);
        setLayout(new GridBagLayout());

                    //        setLayout(new java.awt.BorderLayout());

    }

    
    /*	
    section to manage events
    *
    **/    
    
    /**
     * {@inheritDoc }
     * 
    */
    @Override
    public void setEvent(DataViewerEvent event) {
    	dataViewerEvent = event;
    }
    
    /**
     * {@inheritDoc }
     * 
    */
    @Override
    public DataViewerEvent getEvent() {
    	return dataViewerEvent;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param avesObjects 
     */
    public void setObjectsToDisplay(List<? extends AvesObjectImpl> avesObjects) {
        
        objects = (ArrayList) avesObjects;
        mOIt = objects.listIterator();
        
    }
    
    /**
     * {@inheritDoc }
     * 
     */
    public void setViewerType(ObjectDataType objectType) {
        
        switch (objectType) {
//            case text:
                //if (imV != null)
                //    remove(imV);
                //if (webV != null)
                //    remove(webV);
//                type = DataViewerType.textViewer;
//                break;
            case image:
                //if (txtV != null
                //    remove(txtV);
                if (webV != null) {
                    //not working
                    webV.getAvesBrowser().dispose();               
                    remove(webV);
                }
                DataViewerType prevtype = type;
                type = DataViewerType.imageViewer;
                if (prevtype != DataViewerType.imageViewer) {
                    if(firstIm) {
                    imV = new ImageViewerImpl();
                    
                    System.out.println("dataviewe width: " + this.getWidth());
                    System.out.println("dataVieweHeight: " + this.getHeight());
                    
                    //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                    
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
//                    c.insets = new Insets( - dim.height, - dim.width, 0, 0);
                    gbc.insets = new Insets( - this.getHeight(), - this.getWidth(), 0, 0);
                    }
                    add(imV, gbc);
                    avesViewer.dataViewerEvent();
                    
                    System.out.println("dataviewe width post validate: " + this.getWidth());
                    System.out.println("dataVieweHeight: " + this.getHeight());

//                    imV.setSize(dim.width, dim.height);// - dim.width / 20, dim.height - dim.height / 20);
                    imV.setSize(this.getWidth(), this.getHeight());// - dim.width / 20, dim.height - dim.height / 20);
                    
                    firstIm = false;
                    // local variable with no use after exiting function 
//                    prevtype = DataViewerType.imageViewer;
                }
                break;
            case web:
                //if (txtV != null
                //    remove(txtV);
                if (imV != null)
                    remove(imV);
                type = DataViewerType.webViewer;
                // callback to notify DataViewer -> not needed
//                webV = new WebViewerImpl(this);
                webV = new WebViewerImpl();
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.insets = new Insets(-this.getHeight(), -this.getWidth(), 0, 0);
                add(webV, c);

                avesViewer.dataViewerEvent();

                System.out.println("dataviewer uri width post validate: " + this.getWidth());
                System.out.println("dataVieweHeight: " + this.getHeight());

                webV.setSize(this.getWidth(), this.getHeight());
        }

    }
    
    /**
     * {@inheritDoc }
     * 
     */
    public void displayNext() throws DisplaySlideException {
        try {
            if (mOIt.hasNext()) {
                AvesObjectImpl currentObject;
                currentObject = mOIt.next();
                //because of implementation of ListIterator
                if (movingBackwards) {
                    currentObject = mOIt.next();
                    movingBackwards = false;
                }
                values = (ArrayList) currentObject.getDataValues();
                setViewerType(currentObject.getDataType());
                showData();
            } else {
            	// this is not an UPDATE, it ends the presentation
                setEvent(DataViewerEvent.ENDSHOW);//parent.closeDataViewer();
                avesViewer.dataViewerEvent();
            }
        } catch (Exception e) {
            throw new DisplaySlideException("Error displaying slide: ");
        }
    }

    /**
     * {@inheritDoc }
     * 
     */
    public void displayPrev() throws DisplaySlideException {
        try {
            if (mOIt.hasPrevious()) {
                AvesObjectImpl currentObject;
                currentObject = mOIt.previous();
                //because of implementation of ListIterator
                if (!movingBackwards) {
                    currentObject = mOIt.previous();
                    movingBackwards = true;
                }
                values = (ArrayList) currentObject.getDataValues();
                setViewerType(currentObject.getDataType());
                showData();
            } else {
            	//this is not an UPDATE, it ends the presentaion
            	setEvent(DataViewerEvent.ENDSHOW);//parent.closeDataViewer();
                avesViewer.dataViewerEvent();
            }
        } catch (Exception e) {
            throw new DisplaySlideException("Error displaying slide: ");
        }
    }

    /**
     * {@inheritDoc }
     * 
     */
    public void showData() throws DataNotFoundException {
        try {
            switch (type) {
                case imageViewer:
                    if (bi != null)
                        bi.flush();
                    bi = getImage();
                    break;
                case webViewer:
                    source = getSource();
                    break;                   
            }
        } catch (Exception e) {
            throw new DataNotFoundException("Error showing DOCUMENTS: ");
        }
    }
   
    /**
     * TBI
     * Temporary solution to show an image.
     * 
     * @return BufferedImage
     */
    private BufferedImage getImage() {
        BufferedImage bIm = null;
        try {
            s = values.iterator();

            String uri = s.next();
//			use classloader instead of File            
//          bIm = ImageIO.read(getClass().getClassLoader().getResource(uri));

            java.io.File file = new java.io.File(uri);
            bIm = ImageIO.read(file);
            if (bIm == null) {
                throw new DataNotFoundException("Image is null");
            }
            imV.setCurrentImage(bIm);
        } catch (IllegalArgumentException illArge) {
            System.err.println(illArge);
        } catch (IOException ioE) {
            System.err.println(ioE);
        } catch (DataNotFoundException dNFE) {
            System.err.println(dNFE);
            try {
                displayNext(); 
            } catch(Exception e) {
                System.out.println(e);
            }
        }
        return bIm;
    }
    
    /**
     * TBI
     * Temporary solution to show a web resource
     * 
     * @return String
     */    
    private String getSource() {
        String source = null;
        try {
            s = values.iterator();

            source = s.next();

            if (source == null) {
                throw new DataNotFoundException("error reading uri");
            }
            webV.setCurrentSource(source);
        } catch (IllegalArgumentException illArge) {
            System.err.println(illArge);
        } catch (DataNotFoundException dNFE) {
            System.err.println(dNFE);
            try {
                displayNext();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return source;
    }

    /**
     * {@inheritDoc }
     * 
     * Currently disabled.
     * <p>
     * 
     */
    public void keyTyped(KeyEvent ke) {
        /*        int keyCode = ke.getKeyCode();
        System.out.println("key typed:" + keyCode);
            try {
                displayNext();
            } catch (Exception e) {
                
            }     */
    }

    /**
     * {@inheritDoc }
     * 
     * Left and right arrow to navigate forward and backward. Escape key to return to general view.
     * <p>
     */
/*    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();
        System.out.println("key pressedin dataviewr:" + keyCode);
        if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) { //right arrow
            try {
                displayNext();
            } catch (Exception e) {
                
            }     
        }
        if (keyCode == java.awt.event.KeyEvent.VK_LEFT) { //left arrow
            try {
                displayPrev();
            } catch (Exception e) {
                
            }     
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) { //escape
            try {
            	System.out.println("escape pressed in dataviewer");
            	//this is not an UPDATE, it ends the presentation
            	setEvent(DataViewerEvent.ENDSHOW);//parent.closeDataViewer();
                avesViewer.dataViewerEvent();
            } catch (Exception e) {
                
            }     
        }
    }*/

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

   
