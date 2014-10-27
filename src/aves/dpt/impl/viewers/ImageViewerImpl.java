/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.viewers;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

import aves.dpt.intf.viewers.ImageViewer;

import java.awt.Image;
import java.awt.Toolkit;



/**
 * Implementation of {@link aves.dpt.intf.viewers.ImageViewer}. This
 * implementation scales and centers the image, and invokes {@link javax.swing.JComponent}
 * paint method to display it.
 * 
 * @author svlieffe
 */
public class ImageViewerImpl extends JPanel implements ImageViewer {

    Image imScaled;
    Dimension dim;
    Integer linset = 0;
    Integer vinset = 0;

    /**
     * {@inheritDoc }
     * 
     * @param bi 
     */
    public void setCurrentImage(BufferedImage bi) {

//        dim = Toolkit.getDefaultToolkit().getScreenSize();
        //System.out.println(this.getWidth());
        dim = new Dimension(this.getWidth(), this.getHeight());
        System.out.println("Img V w: " + dim.width);
        System.out.println("Img V h: " + dim.height);
        
        this.setBackground(Color.yellow);
        
        //Dimension sdim = Toolkit.getDefaultToolkit().getScreenSize();
        
        this.setOpaque(false);

        System.out.println("bufferedImage width in imviewer= " + bi.getWidth());
        System.out.println("bufferedImage height = " + bi.getHeight());
        
        if (bi.getHeight() <= bi.getWidth() && dim.height < dim.width) {
            imScaled = bi.getScaledInstance(-1 , dim.height, Image.SCALE_SMOOTH);
            linset = (dim.width - imScaled.getWidth(null)) / 2;
            vinset = 0;
        } else if (bi.getHeight() >= bi.getWidth() && dim.height > dim.width) {
            imScaled = bi.getScaledInstance(dim.width, -1, Image.SCALE_SMOOTH);
            linset = 0;
            vinset = (dim.height - imScaled.getHeight(null)) / 2;
        } else if (bi.getHeight() <= bi.getWidth() && dim.height > dim.width) {
            imScaled = bi.getScaledInstance(dim.width, -1, Image.SCALE_SMOOTH);
            linset = 0;
            vinset = (dim.height - imScaled.getHeight(null)) / 2;          
        } else { //(bi.getHeight() >= bi.getWidth() && sdim.height < sdim.width) 
            imScaled = bi.getScaledInstance(-1, dim.height, Image.SCALE_SMOOTH);
            linset = (dim.width - imScaled.getWidth(null)) / 2;
            vinset = 0;

        }

        repaint();
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(imScaled, linset, vinset, null);
    }
}
