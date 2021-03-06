/**
 * World Wind is licensed under the NASA Open Source Agreement {@link http://worldwind.arc.nasa.gov/worldwind-nosa-1.3.html}
 * Aves' extensions to the NASA Worldwind core fall under the GNU GPL V3 {@link http://www.gnu.org/licenses/gpl-3.0.txt}
 */
 
package aves.dpt.impl.production;

import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.*;

import aves.dpt.intf.production.XMLDataReader;
import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * 
 * Basic implementation of the {@link aves.dpt.intf.production.XMLDataReader}
 * that maintains a {@link javax.xml.parsers.SAXParser}.
 *
 * @author svlieffe
 * 2012/03/29
 */
public class XMLDataReaderImpl implements XMLDataReader{

    private ArrayList<String> valueList = new ArrayList<String>();
    private String groupName;
    private AvesObjectType objectType;

    /**
     * 
     * parses the DOCUMENTS source using a 
     * {@link javax.xml.parsers.SAXParser}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void startParse() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            SAXParser parser = factory.newSAXParser();

            XMLSaxHandlerImpl xmlHandler = new XMLSaxHandlerImpl();
            
            xmlHandler.setType(objectType);
            
            xmlHandler.setGroup(groupName);

//			getClassLoader instead of FileInputStream
//          parser.parse(getClass().getClassLoader().getResourceAsStream("avesJourneys.xml"), xmlHandler);

            FileInputStream inStream = new FileInputStream("Journeys/avesJourneys.xml");
            parser.parse(inStream, xmlHandler);

            valueList = (ArrayList)xmlHandler.valueList();

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     * @return valueList
     */
    public List<String> valueList() {
        return valueList;
    }
    
    /**
     * 
     * {@inheritDoc }
     *  
     */
    public void read() {
        startParse();
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param avesObjectType 
     */
    public void setType(AvesObjectType avesObjectType) {
        objectType = avesObjectType;        
    }
    
    /**
     * 
     * {@inheritDoc }
     * @param itemGroupName 
     */
    public void setGroup(String itemGroupName) {
        groupName = itemGroupName;
    }
}
