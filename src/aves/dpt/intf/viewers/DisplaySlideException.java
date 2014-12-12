package aves.dpt.intf.viewers;

/**
 *
 * @author svlieffe
 * 2012/03/29
 */

public class DisplaySlideException extends Exception {

    public DisplaySlideException(String message) {
        super("Error displaying next slide: " + message);
    }
}
