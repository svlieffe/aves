package aves.dpt.impl.production;

import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.*;

import aves.dpt.intf.production.XMLDataReader;
import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * Basic implementation of the {@link aves.dpt.intf.production.XMLDataReader}
 * that maintains a {@link javax.xml.parsers.SAXParser}.
 *
 * @author svlieffe
 */
public class XMLDataReaderImpl implements XMLDataReader{

    private ArrayList<String> valueList = new ArrayList<String>();
    private String groupName;
    private AvesObjectType objectType;

    /**
     * parses the DATA source using a 
     * {@link javax.xml.parsers.XMLSaxHandlerImpl}
     */
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
            valueList = (ArrayList)xmlHandler.valueList();

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @return 
     */
    public List<String> valueList() {
        return valueList;
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param avesObjectType 
     */
    public void read() {
        startParse();
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param avesObjectType 
     */
    public void setType(AvesObjectType avesObjectType) {
        objectType = avesObjectType;
        
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param itemGroupName 
     */
    public void setGroup(String itemGroupName) {
        groupName = itemGroupName;
    }
}
