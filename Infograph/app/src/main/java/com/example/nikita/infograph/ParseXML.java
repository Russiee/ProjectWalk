package com.example.nikita.infograph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Nikita on 26/11/2015.
 */
public class ParseXML {

    private List<Country> countriesList;
    private URL url;

    public ParseXML() {
        countriesList = new ArrayList<Country>();
    }

    public void parseXML(String url) {
        try {
            this.url = new URL(url);
            URLConnection connection = this.url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(connection.getInputStream());
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String energyID = elem.getElementsByTagName("wb:indicator").item(0).getChildNodes().item(0).getNodeValue();
                    String countryID = elem.getElementsByTagName("wb:country").item(0).getChildNodes().item(0).getNodeValue();
                    String yearString = elem.getElementsByTagName("wb:date").item(0).getChildNodes().item(0).getNodeValue();
                    if(yearString.length() >= 6 && yearString.substring(0, 6).equals("Period")) {
                        yearString = "1000";
                    }
                    int year = Integer.valueOf(yearString);
                    String value = "0";
                    NodeList nodeValue = elem.getElementsByTagName("wb:value").item(0).getChildNodes();
                    Boolean exists = false;
                    if (nodeValue.getLength() != 0) {
                        value = elem.getElementsByTagName("wb:value").item(0).getChildNodes().item(0).getNodeValue();
                    } else {
                        value = "0";
                    }
                    for (Country c : countriesList) {
                        if (c.getName().equals(countryID)) {
                            c.addValue(year, value, energyID);
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        Country country = new Country(countryID, year, value, energyID);
                        countriesList.add(country);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("THERE WAS AN ERROR!");
        }
    }

    public List<Country> getCountriesList() {
        return countriesList;
    }
}
