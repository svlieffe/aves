/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// TODO change source from xml
  
 
package aves.dpt.impl.viewers;

import gov.nasa.worldwind.BasicModel;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import aves.dpt.intf.viewers.WebViewer;

import java.net.URI;

import javax.swing.JPanel;

import gov.nasa.worldwind.util.webview.MacWebView;
import gov.nasa.worldwind.util.webview.WebView;
import gov.nasa.worldwind.util.webview.WebViewFactory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.WWTexture;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwindx.examples.util.HotSpotController;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;

import java.lang.Class;

import javax.swing.JFrame;

/**
 *
 * @author stefaanvanliefferinge
 */                                 //GLCanvas
public class WebViewerImpl extends JPanel implements WebViewer { //, KeyListener {//, GLEventListener {

    URI uri;
    Dimension dim;
    protected static final String BROWSER_CONTENT_PATH = "data/BrowserBalloonExampleMurray.html";//gov/nasa/worldwindx/examples/data/BrowserBalloonExample.html";
    // debug Class thisClass;
    String htmlString = null;
    InputStream contentStream = null;
    static GLCanvas canvas = new GLCanvas();
    protected HotSpotController hotSpotController;
    AvesBrowser ogb;
    WorldWindowGLJPanel wwp;
//    DataViewerImpl calling_dv;
    
    public WebViewerImpl() {
        
    }
    
    public AvesBrowser getAvesBrowser() {
        return ogb;
    }
/*
    public WebViewerImpl(DataViewerImpl dv) {
        this.calling_dv = dv;
    }
*/
    public void setCurrentSource(String source) {
        //try {
        System.out.println("source in webviewerimple= " + source);
        System.out.println(source);
        //uri = new URI(source);
        //} catch (URISyntaxException ex) {
        //    Logger.getLogger(WebViewerImpl.class.getName()).log(Level.SEVERE, null, ex);
        //}
        dim = new Dimension(this.getWidth(), this.getHeight());
        //DrawContext dc = null;

            String htmlString = null;
            InputStream contentStream = null;

            try {
                // debug thisClass = this.getClass();
                // Read the URL content into a String using the default encoding (UTF-8).
                contentStream = WWIO.openFileOrResourceStream(source, this.getClass());
                htmlString = WWIO.readStreamToString(contentStream, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                WWIO.closeStream(contentStream, source);
            }

            if (htmlString == null) {
                htmlString = Logging.getMessage("generic.ExceptionAttemptingToReadFile", source);
            }

            ogb = new AvesBrowser(htmlString, this.getWidth(), this.getHeight());
//            JFrame frame = new JFrame();


//            frame.setSize(600, 700);
            //frame.setExtendedState(Frame.MAXIMIZED_BOTH);
  
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            BasicModel bm;
            // Add a controller to send input events to BrowserBalloons.

            bm = new BasicModel();
            LayerList layers;
            wwp = new WorldWindowGLJPanel();
            wwp.setSize(this.getWidth(), this.getHeight());
            layers = bm.getLayers();

            layers.removeAll();

            RenderableLayer newlayer = new RenderableLayer();
            newlayer.setName("Web Browser");
            newlayer.addRenderable(ogb);

            // Add the newlayer to the ApplicationTemplate's newlayer panel.
            bm.getLayers().add(0, newlayer);

            for (int i = 0; i < layers.size(); i++) {
                String name = layers.get(i).getName();
                System.out.println(name);
            }

            wwp.setModel(bm);
            newlayer.setPickEnabled(true);
            wwp.addSelectListener(ogb);
            hotSpotController = new HotSpotController(wwp);
            // Add a controller to handle link and navigation events in BrowserBalloons.
//Commented out 120611        
//            balloonController = new BalloonController(wwp);
//            highlightController = new HighlightController(wwp, SelectEvent.ROLLOVER);
            this.add(wwp, java.awt.BorderLayout.CENTER);
            System.out.println("yeoloooo");
//            frame.setVisible(true);
//            frame.pack();
        
//        revalidate();
//        repaint();
        
    }

    /**
     * {@inheritDoc }
     * 
     * <p>
     * Unused
     * 
     */
/*    public void keyTyped(KeyEvent ke) {
    }

    
    
    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();
        System.out.println("key pressed in webviewer:" + keyCode);
        if (keyCode == 39) { //right arrow
            try {
                ogb.dispose();
                wwp.setVisible(false);
//                calling_dv.keyPressed(ke);
            } catch (Exception e) {
                
            }     
        }
        if (keyCode == 37) { //left arrow
            try {
                ogb.dispose();
                wwp.setVisible(false);
                calling_dv.keyPressed(ke);
            } catch (Exception e) {
                
            }     
        }
    }
*/
        /**
     * {@inheritDoc }
     * 
     * <p>
     * 
     * Unused
     * 
     */
//    public void keyReleased(KeyEvent e) {
//    }
    

    
/*
    public void init(GLAutoDrawable gLDrawable) {
    }

    public void display(GLAutoDrawable gLDrawable) {
        final GL gl = gLDrawable.getGL();
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glScalef(1f / this.getWidth(), 1f / this.getHeight(), 1f);
        //gl.glTranslatef(this.screenRect.x - this.webViewRect.x, this.screenRect.y - this.webViewRect.y, 0f);
        // Restore the matrix mode.
        gl.glMatrixMode(GL.GL_MODELVIEW);

    }

    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
    }
*/
}
