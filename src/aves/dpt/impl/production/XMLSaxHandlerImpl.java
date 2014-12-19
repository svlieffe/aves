package aves.dpt.impl.production;


import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.AvesObject.AvesObjectType;

/**
 * 
 * Hander for the {@link javax.xml.parsers.SAXParser}.
 *
 * @author svlieffe
 * 2012/03/29
 */
public class XMLSaxHandlerImpl extends DefaultHandler {
    
    private AvesObjectType type;
    private String group;
    private String journeyDate = "";
    private String placeName = "";
    private ArrayList<String> dataList = new ArrayList<String>();

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void startDocument() throws SAXException {
        
        System.out.println("Document processing started");
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void endDocument() throws SAXException {
        System.out.println("Document processing finished");
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attrs) throws SAXException {

        switch (type) {
            case JOURNEYS:
                if (qName.equals("p:journey")) {
                    dataList.add(attrs.getValue("sessiondate"));
                } else {
                    break;
                }
                break;
            case PLACES:
                if (qName.equals("p:journey")) {
                    journeyDate = attrs.getValue("sessiondate");
                }
                if (journeyDate.equals(group)) {
                    if (qName.equals("p:pplace")) {
                        dataList.add(attrs.getValue("pname"));
                        dataList.add(attrs.getValue("latitude"));
                        dataList.add(attrs.getValue("longitude"));

                    } else {
                        break;
                    }
                }
                break;
            case DOCUMENTS:
                if (qName.equals("p:pplace")) {
                    placeName = attrs.getValue("pname");
                }
                if (placeName.equals(group)) {    
                    if (qName.equals("p:document")) {
                        dataList.add(attrs.getValue("type"));
                        dataList.add(attrs.getValue("uri"));
                    } else {
                        break;
                    }
                }
                break;
        }
    }

    /**
     * 
     * {@inheritDoc }
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // do nothing;
    }
    
    public List<String> valueList() {
        return dataList;
    }
    
    public void setType(AvesObjectType avesObjectType) {
        type = avesObjectType;
    }
    
    public void setGroup(String groupName) {
        group = groupName;
    }
}
