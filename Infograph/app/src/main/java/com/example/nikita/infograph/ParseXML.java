package com.example.nikita.infograph;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This class is used to access data from the World Bank APIs,
 * display them in the application and store them
 * Created by Nikita on 26/11/2015.
 */
public class ParseXML {

    private List<Country> countriesList;
    private List<String> countriesData;
    private URL url;
    private String energyID, countryID, yearString, value;

    NodeList nodeList;

    public ParseXML() {
        countriesList = new ArrayList<Country>();
        countriesData = new ArrayList<String>();
    }

    /**
     * This is the constructor of the parseXML class
     * @param url the address from where the data are fetched
     */
    public void parseXML(String url, boolean fileIsAvailable, File tempFile) {
        if(!fileIsAvailable)
        {
            try
            {
                this.url = new URL(url);
                Log.d("Internet",url);
                URLConnection connection = this.url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc = builder.parse(connection.getInputStream());
                nodeList = doc.getDocumentElement().getChildNodes();

                if(nodeList.getLength() != 0)
                {
                    for (int i = 0; i < nodeList.getLength(); i++)
                    {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element elem = (Element) node;
                            energyID = elem.getElementsByTagName("wb:indicator").item(0).getChildNodes().item(0).getNodeValue();
                            countryID = elem.getElementsByTagName("wb:country").item(0).getChildNodes().item(0).getNodeValue();
                            yearString = elem.getElementsByTagName("wb:date").item(0).getChildNodes().item(0).getNodeValue();

                            if(yearString.length() >= 6 && yearString.substring(0, 6).equals("Period"))
                            {
                                yearString = "1000";
                            }

                            int year = Integer.valueOf(yearString);
                            value = "0";
                            NodeList nodeValue = elem.getElementsByTagName("wb:value").item(0).getChildNodes();
                            Boolean exists = false;

                            if (nodeValue.getLength() != 0)
                            {
                                value = elem.getElementsByTagName("wb:value").item(0).getChildNodes().item(0).getNodeValue();
                            } else {
                                value = "0";
                            }

                            String countryData = countryID + ";" + year + ";" + value + ";" + energyID;
                            countriesData.add(countryData);

                            for (Country c : countriesList)
                            {
                                if (c.getName().equals(countryID))
                                {
                                    c.addValue(year, value, energyID);
                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists)
                            {
                                Country country = new Country(countryID, year, value, energyID);
                                countriesList.add(country);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("THERE WAS AN ERROR!");
            }
        }
        else {
            try {
                FileReader fReader = new FileReader(tempFile);
                BufferedReader bReader = new BufferedReader(fReader);

                String dataLine = "";

                while ((dataLine = bReader.readLine()) != null) {
                    String[] data = dataLine.split(";");
                    countryID = data[0];
                    yearString = data[1];
                    value = data[2];
                    energyID = data[3];

                    Boolean exists = false;

                    for (Country c : countriesList) {
                        if (c.getName().equals(countryID)) {
                            c.addValue(Integer.parseInt(yearString), value, energyID);
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        Country country = new Country(countryID, Integer.parseInt(yearString), value, energyID);
                        countriesList.add(country);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Internet", "There's an error");
            }
        }
    }

    /**
     * This method is used to get a list with the countries that are displayed in the application
     * @return the list of all the countries that are displayed in the program
     */
    public List<Country> getCountriesList() {
        return countriesList;
    }

    public List<String> getCountriesData() {
        return countriesData;
    }
}
