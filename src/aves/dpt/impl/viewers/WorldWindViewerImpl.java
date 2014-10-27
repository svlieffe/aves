/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.viewers;

import aves.dpt.intf.viewers.WorldWindViewer;

import javax.swing.JPanel;

import java.awt.Dimension;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;

/**
 * Implementation of the {@link aves.dpt.intf.viewers.WorldWindViewer}
 * 
 * @author stefaanvanliefferinge
 * @version $Id: WorldWindViewerImpl.java,v e7e78df9ca90 2012/03/30 19:53:39 svl $ 
 * 
 */
public class WorldWindViewerImpl extends JPanel implements WorldWindViewer {

    private FlatGlobe flatGlobe;
    private BasicModel bm;
    private WorldWindowGLJPanel wwp;
    //private Layer layer;
    private LayerList layers;
//    private ViewControlsLayer viewControlsLayer;

    //private HotSpotController controller;
    /**
     * {@inheritDoc}
     * <p>
     */
    public void initComponents(Dimension dim) {
        wwp = new WorldWindowGLJPanel();

        // Remove unneeded layers

        this.bm = new BasicModel();
        this.flatGlobe = new EarthFlat();

        this.layers = this.bm.getLayers();

        for (int i = 0; i < layers.size(); i++) {
            String name = layers.get(i).getName();
            System.out.println(name);
        }
        for (int i = 0; i < layers.size(); i++) {
            String name = layers.get(i).getName();
            if ("Place Names".equals(name)) {
                layers.remove(layers.get(i));
            }
        }
        for (int i = 0; i < layers.size(); i++) {
            String name = layers.get(i).getName();
            if ("World Map".equals(name)) {
                layers.remove(layers.get(i));
            }
        }
        for (int i = 0; i < layers.size(); i++) {
            String name = layers.get(i).getName();
            if ("Compass".equals(name)) {
                layers.remove(layers.get(i));
            }
        }
        for (int i = 0; i < layers.size(); i++) {
            String name = layers.get(i).getName();
            if ("Scale bar".equals(name)) {
                layers.remove(layers.get(i));
            }
        }
        
        // add some layers
        ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
        layers.add(viewControlsLayer);
        wwp.addSelectListener(new ViewControlsSelectListener(wwp, viewControlsLayer));

        viewControlsLayer.setShowHeadingControls(false);
        viewControlsLayer.setShowFovControls(false);
        viewControlsLayer.setShowHeadingControls(false);
        viewControlsLayer.setShowPanControls(false);
        viewControlsLayer.setShowVeControls(true);
        viewControlsLayer.setShowZoomControls(false);

        wwp.setPreferredSize(dim);//new java.awt.Dimension(750, 600));
        bm.setLayers(layers);
//        bm.setGlobe(flatGlobe);
        wwp.setModel(bm);

        this.getwwPanel().getView().setEyePosition(
                Position.fromDegrees(48.8567, 2.3508, 5.0e7));  //Position.fromDegrees(47.0844, 2.3964, 1.5e5));//1.5e7)); // for mathilde 2.50e9

        this.setLayout(new java.awt.BorderLayout());
        this.add(wwp, java.awt.BorderLayout.CENTER);
    }

    public WorldWindowGLJPanel getwwPanel() {
        return wwp;
    }
}
