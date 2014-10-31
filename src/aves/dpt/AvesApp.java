package aves.dpt;

import aves.dpt.impl.ctrl.*;
import gov.nasa.worldwind.Configuration;


/**
 * Main application
 * @author svlieffe
 * @version $Id: AvesApp.java,v 4cff031e2196 2012/03/29 14:49:23 svlieffe $
 */
public class AvesApp {

    private static final class AppWin {

        private static void start() {
            AvesManagerImpl mm = new AvesManagerImpl();
        }
    }

    public static void main(String[] args) {
        if (Configuration.isMacOS()) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Aves");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {  //If I understand correctly: this corresponds to an instance of a anonymous class that implements the interface runnable

            @Override
            public void run() {
                AppWin.start();
            }
        });

    }
}
