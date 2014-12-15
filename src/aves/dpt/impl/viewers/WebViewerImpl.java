package aves.dpt.impl.viewers;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BalloonAttributes;
import gov.nasa.worldwind.render.BasicBalloonAttributes;
import gov.nasa.worldwind.render.Size;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwindx.examples.util.HotSpotController;
import aves.dpt.intf.viewers.AvesViewer;
import aves.dpt.intf.viewers.WebViewer;

import java.net.URI;
import java.awt.Dimension;
import java.io.InputStream;

import javax.swing.JPanel;
import javax.media.opengl.awt.*;

/**
 *
 * @author svlieffe
 * 2012/03/09
 */                                 
public class WebViewerImpl extends JPanel implements WebViewer {

    URI uri;
    Dimension dim;
    String htmlString = null;
    InputStream contentStream = null;
    static GLCanvas canvas = new GLCanvas();
    protected HotSpotController hotSpotController;
    AvesBrowser avesBrowser;
    WorldWindowGLJPanel wwp;
    private AvesViewer avesViewer;

    public WebViewerImpl(AvesViewer avesViewer) {
    	this.avesViewer = avesViewer; 
    }
    
    public AvesBrowser getAvesBrowser() {
        return avesBrowser;
    }

    @Override
    public void setCurrentSource(String source) {
        System.out.println("source in webviewerimple= " + source);
        System.out.println(source);
        dim = new Dimension(this.getWidth(), this.getHeight());

        String htmlString = null;
        InputStream contentStream = null;

        try {
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

        avesBrowser = new AvesBrowser(htmlString, this.getWidth(), this.getHeight(), this.avesViewer);
        BasicModel bm;
        bm = new BasicModel();
        LayerList layers;
        wwp = new WorldWindowGLJPanel();
        wwp.setSize(this.getWidth(), this.getHeight());
        layers = bm.getLayers();

        layers.removeAll();

        RenderableLayer newlayer = new RenderableLayer();
        newlayer.setName("Web Browser");
        newlayer.addRenderable(avesBrowser);

        // Add the newlayer to the ApplicationTemplate's newlayer panel.
        bm.getLayers().add(0, newlayer);

        for (int i = 0; i < layers.size(); i++) {
            String name = layers.get(i).getName();
            System.out.println(name);
        }

        wwp.setModel(bm);
        newlayer.setPickEnabled(true);
        wwp.addSelectListener(avesBrowser);
        hotSpotController = new HotSpotController(wwp);
        this.add(wwp, java.awt.BorderLayout.CENTER);
        System.out.println("yeoloooo");
    }

}
