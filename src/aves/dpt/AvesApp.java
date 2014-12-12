package aves.dpt;

import aves.dpt.impl.ctrl.*;
import gov.nasa.worldwind.Configuration;


/**
 * Main application
 * @author svlieffe
 * 
 * 2012/03/09
 */
public class AvesApp {

    private static final class AppWin {

        private static void start() {
            new AvesManagerImpl();
        }
    }

    public static void main(String[] args) {
        if (Configuration.isMacOS()) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Aves");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                AppWin.start();
            }
        });

    }
}
