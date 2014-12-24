/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt;

import aves.dpt.impl.ctrl.*;
import gov.nasa.worldwind.Configuration;


/**
 * 
 * Main application
 * 
 * @author svlieffe
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
