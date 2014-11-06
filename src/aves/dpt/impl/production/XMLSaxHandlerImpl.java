/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aves.dpt.impl.production;


import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.List;
import java.util.ArrayList;

import aves.dpt.intf.production.AvesObject.AvesObjectType;
/**
 * Hander for the {@link javax.xml.parsers.SAXParser}.
 *
 * @author svlieffe
 */
public class XMLSaxHandlerImpl extends DefaultHandler {
    
    private AvesObjectType type;
    private String group;
    private String sessionDate = "";
    private String placeName = "";
    private ArrayList<String> dataList = new ArrayList<String>();

    @Override
    public void startDocument() throws SAXException {
        
        System.out.println("Document processing started");
    }

    @Override
    public void endDocument() throws SAXException {
        System.out.println("Document processing finished");
    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attrs) throws SAXException {

        switch (type) {
            case JOURNEYS:
                if (qName.equals("journey")) {
                    dataList.add(attrs.getValue("date"));
                } else {
                    break;
                }
                break;
            case PLACES:
                if (qName.equals("journey")) // && sessionDate.equals(group)) {
                {
                    sessionDate = attrs.getValue("date");
                }
                if (sessionDate.equals(group)) {
                    if (qName.equals("place")) {
                        dataList.add(attrs.getValue("name"));
                        dataList.add(attrs.getValue("latitude"));
                        dataList.add(attrs.getValue("longitude"));

                    } else {
                        break;
                    }
                }

                break;
            case DOCUMENTS:
                if (qName.equals("place")) {
                    placeName = attrs.getValue("name");
                }
                if (placeName.equals(group)) {    
                    if (qName.equals("document")) {
                        dataList.add(attrs.getValue("type"));
                        dataList.add(attrs.getValue("uri"));
                    } else {
                        break;
                    }
                }
                break;
        }
    }

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
