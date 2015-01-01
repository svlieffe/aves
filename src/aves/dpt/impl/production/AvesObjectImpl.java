/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.AvesObject;

/**
 * 
 * Implements a {@link aves.dpt.intf.production.AvesObject}
 * @author svlieffe
 * 2012/03/29
 */
public class AvesObjectImpl implements AvesObject {
    
    private ArrayList<String> dataValue = new ArrayList<String>();;
    private AvesObjectType objectType;
    private ObjectDataType type;
    
    /**
     * 
     * {@inheritDoc }
     * @param avesObjectType 
     */
    @Override
    public void setObjectType(AvesObjectType avesObjectType) {
        objectType = avesObjectType;
    }
    
    /**
     * 
     * {@inheritDoc }
     * @return objectType
     */
    @Override
    public AvesObjectType getObjectType() {
        return objectType;
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param dataType 
     */
    @Override
    public void setDataType(ObjectDataType dataType) {
        type = dataType;
    }
    
    /**
     * 
     * {@inheritDoc }
     * @return type
     */
    public ObjectDataType getDataType() {
        return type;
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param value 
     */
    public void addDataValue(String value) {
        dataValue.add(value);
    }
    
    /**
     * 
     * {@inheritDoc }
     * @return dataValue
     */
    public List<String> getDataValues() {
        return dataValue;
    }

}
