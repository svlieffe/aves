package aves.dpt.impl.viewers;

import javax.swing.JPanel;

import java.awt.image.BufferedImage;

import aves.dpt.impl.production.AvesObjectImpl;
import aves.dpt.intf.ctrl.AvesEventManager;
import aves.dpt.intf.production.AvesObject.ObjectDataType;
import aves.dpt.intf.viewers.AvesViewer;
import aves.dpt.intf.viewers.DataNotFoundException;
import aves.dpt.intf.viewers.DataViewer;
import aves.dpt.intf.viewers.DisplaySlideException;

import javax.imageio.ImageIO;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;
import java.io.IOException;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

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
 * 2012/03/29
 */
public class DataViewerImpl extends JPanel implements DataViewer {
    
    private DataViewerType type;
    private DataViewerEvent dataViewerEvent;
    private ImageViewerImpl imV;
    private WebViewerImpl webV;
    private GridBagConstraints gbc;
    private BufferedImage bi;
    private List<AvesObjectImpl> objects = new ArrayList<AvesObjectImpl>();
    private ListIterator<AvesObjectImpl> mOIt;
    private List<String> values = new ArrayList<String>();
    private Iterator<String> s;
    private AvesEventManager AvesEventMgr;
    private AvesViewer avesViewer;
    private String source;
    boolean last = false;
    boolean firstIm = true;
    boolean movingBackwards = false;
    
    public DataViewerImpl(AvesEventManager AvesEventMgr, AvesViewer avesViewer) {
        
    	this.AvesEventMgr = AvesEventMgr;
    	this.avesViewer = avesViewer;
        //set most frequent event
        setEvent(DataViewerEvent.UPDATE);
        setLayout(new GridBagLayout());
    }

    
    /**
     * 	
     * Section to manage events
     *
     */    
    
    /**
     * 
     * {@inheritDoc }
    */
    @Override
    public void setEvent(DataViewerEvent event) {
    	dataViewerEvent = event;
    }
    
    /**
     * 
     * {@inheritDoc }
    */
    @Override
    public DataViewerEvent getEvent() {
    	return dataViewerEvent;
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param avesObjects 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void setObjectsToDisplay(List<? extends AvesObjectImpl> avesObjects) {
        
        objects = (ArrayList) avesObjects;
        mOIt = objects.listIterator();
        
    }
    
    /**
     * 
     * {@inheritDoc }
     */
	@Override
    public void setViewerType(ObjectDataType objectType) {
        
        switch (objectType) {
           //textviewer is not implemented
           //case text:
                //if (imV != null)
                //    remove(imV);
                //if (webV != null)
                //    remove(webV);
                //type = DataViewerType.textViewer;
                //break;
            case image:
                //if (txtV != null
                //    remove(txtV);
                if (webV != null) {
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
                    
                    gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.insets = new Insets( - this.getHeight(), - this.getWidth(), 0, 0);
                    }
                    add(imV, gbc);
                    AvesEventMgr.dataViewerEvent();
                    
                    System.out.println("dataviewe width post validate: " + this.getWidth());
                    System.out.println("dataVieweHeight: " + this.getHeight());

                    imV.setSize(this.getWidth(), this.getHeight());
                    
                    firstIm = false;
                }
                break;
            case web:
                //if (txtV != null
                //    remove(txtV);
                if (imV != null)
                    remove(imV);
                type = DataViewerType.webViewer;
                webV = new WebViewerImpl(this.avesViewer);
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.insets = new Insets(-this.getHeight(), -this.getWidth(), 0, 0);
                add(webV, c);

                AvesEventMgr.dataViewerEvent();

                System.out.println("dataviewer uri width post validate: " + this.getWidth());
                System.out.println("dataVieweHeight: " + this.getHeight());

                webV.setSize(this.getWidth(), this.getHeight());
        }

    }
    
    /**
     * {@inheritDoc }
     * 
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
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
                setEvent(DataViewerEvent.ENDSHOW);
                AvesEventMgr.dataViewerEvent();
            }
        } catch (Exception e) {
            throw new DisplaySlideException("Error displaying slide: ");
        }
    }

    /**
     * 
     * {@inheritDoc }
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
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
            	//this is not an UPDATE, it ends the presentation
            	setEvent(DataViewerEvent.ENDSHOW);//parent.closeDataViewer();
            	AvesEventMgr.dataViewerEvent();
            }
        } catch (Exception e) {
            throw new DisplaySlideException("Error displaying slide: ");
        }
    }

    /**
     * 
     * {@inheritDoc }
     */
	@Override
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
     * 
     * @return BufferedImage
     */
    private BufferedImage getImage() {
        BufferedImage bIm = null;
        try {
            s = values.iterator();

            String uri = s.next();
			//use classloader instead of File            
            //bIm = ImageIO.read(getClass().getClassLoader().getResource(uri));

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
     * 
     * @return URI
     */    
    private String getSource() {
        String sourceURI = null;
        try {
            s = values.iterator();

            sourceURI = s.next();

            if (sourceURI == null) {
                throw new DataNotFoundException("error reading uri");
            }
            webV.setCurrentSource(sourceURI);
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
        return sourceURI;
    }

}

   
