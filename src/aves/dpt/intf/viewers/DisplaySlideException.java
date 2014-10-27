/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.intf.viewers;

/**
 *
 * @author stefaanvanliefferinge
 */
public class DisplaySlideException extends Exception {

    public DisplaySlideException(String message) {
        super("Error displaying next slide: " + message);
    }
}
