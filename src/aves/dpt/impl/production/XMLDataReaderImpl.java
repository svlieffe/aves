/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.production;

import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.*;

import aves.dpt.intf.production.XMLDataReader;
import aves.dpt.intf.production.MappaObject.MappaObjectType;

/**
 * Basic implementation of the {@link aves.dpt.intf.production.XMLDataReader}
 * that maintains a {@link javax.xml.parsers.SAXParser}.
 *
 * @author svlieffe
 */
public class XMLDataReaderImpl implements XMLDataReader{

    private ArrayList<String> valueList = new ArrayList<String>();
    private String groupName;
    private MappaObjectType objectType;

    /**
     * parses the data source using a 
     * {@link javax.xml.parsers.XMLSaxHandlerImpl}
     */
    public void startParse() {
        try {
            // returns the Class object associated with this class
            Class cls = Class.forName("XMLDataReaderImpl");

            // returns the ClassLoader object associated with this Class.
            ClassLoader cLoader = cls.getClassLoader();

            if (cLoader == null) {
               System.out.println("The default system class was used.");
            }
            else {
               // returns the class loader
               Class loaderClass = cLoader.getClass();

               System.out.println("Class associated with ClassLoader = " +
               loaderClass.getName());
            }
         }
         catch (ClassNotFoundException e) {
            System.out.println(e.toString());
         }
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            SAXParser parser = factory.newSAXParser();

            XMLSaxHandlerImpl xmlHandler = new XMLSaxHandlerImpl();
            
            xmlHandler.setType(objectType);
            
            xmlHandler.setGroup(groupName);

            parser.parse(getClass().getClassLoader().getResourceAsStream("mappadatasrc.xml"), xmlHandler);
//debug
            Class loaderClass = getClass();
            System.out.println("Class associated with ClassLoader = " +
            loaderClass.getName());
            System.out.println("resoures path = " + loaderClass.getClassLoader().getResource("mappadatasrc.xml"));
//enddebug
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
     * @param mappaObjectType 
     */
    public void read() {
        startParse();
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param mappaObjectType 
     */
    public void setType(MappaObjectType mappaObjectType) {
        objectType = mappaObjectType;
        
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
