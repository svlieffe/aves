package aves.dpt.intf.viewers;

/**
 *
 * Generates an error when the program cannot find the source DOCUMENTS
 * of the requested {@link aves.dpt.intf.production.AvesObject}
 * 
 * @author svlieffe
 * 2012/03/29
 */
public class DataNotFoundException extends Exception {
    private static final long serialVerstionUID = 1L;
    
    public DataNotFoundException(String message) {
        super(message);
    }
    
}
