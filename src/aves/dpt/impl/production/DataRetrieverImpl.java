/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.DataRetriever;
import aves.dpt.intf.production.AvesObject.AvesObjectType;


/**
 *
 * Basic implementation of the 
 * {@link aves.dpt.intf.production.DataRetriever}. It maintains an
 * object implementing {@link aves.dpt.intf.production.XMLDataReader}.
 * 
 * @author svlieffe
 * 2012/03/29
 */
public class DataRetrieverImpl implements DataRetriever{
    
    private AvesObjectType objectType;
    private String groupName;
    private XMLDataReaderImpl dr = new XMLDataReaderImpl();
    private ArrayList<String> dataList = new ArrayList<String>();
    
    /**
     * 
     * {@inheritDoc }
     * @return dataList
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> getData() {
        dr.setType(objectType);
        dr.setGroup(groupName);
        dr.read();
        dataList = (ArrayList)dr.valueList();
        return dataList;       
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param avesObjectType 
     */
    @Override
    public void setDataType(AvesObjectType avesObjectType) {
        objectType = avesObjectType;        
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param name 
     */
    @Override
    public void setGroupName(String name) {
        groupName = name;
    }

}
