package edu.kit.gik.STGIS.xml;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class XMLHandler extends DefaultHandler {

    public String elementValue = null;
    Boolean elementOn = false;
    public String searchTag;
    public String currentName;
    public List<String> searchTags;
    public ArrayList<String> data = new ArrayList<String>();
    public HashMap<String, String> data1 = new HashMap<String, String>();


    public XMLHandler(String[] tags) {
        // TODO Auto-generated constructor stub
        searchTags = Arrays.asList(tags);
    }

    /**
     * This will be called when the tags of the XML starts.
     **/
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (searchTags.contains(qName)) {
            elementOn = true;
        }
    }

    /**
     * This will be called when the tags of the XML end.
     **/
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        elementOn = false;
    }

    /**
     * This is called to get the tags value
     **/
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (elementOn) {
            elementValue = new String(ch, start, length);
            elementOn = false;
            if (elementValue != null) {
                data.add(elementValue);
                Log.d("elementValue ", elementValue);
            }
        }
    }
}