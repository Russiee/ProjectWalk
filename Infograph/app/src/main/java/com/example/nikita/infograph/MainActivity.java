package com.example.nikita.infograph;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    List<Country> countriesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new parseXML().execute("");
        System.out.println(countriesList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class parseXML extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://api.worldbank.org/countries/ALB;ARM;AUT;BLR;BEL;BIH;BGR;CHI;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;IMY;ITA;KSV;LVA;LIE;LTU;LUX;MKD;MCO;MNE;NLD;NOR;POL;PRT;ROM;RUS;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR;MLT/indicators/NY.GDP.MKTP.KD?per_page=3000&MRV=50&Gapfill=Y&format=xml&date=1960:2015");
                URLConnection connection = url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc = builder.parse(connection.getInputStream());
                NodeList nodeList = doc.getDocumentElement().getChildNodes();
                countriesList = new ArrayList<Country>();
                for(int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) node;
                        String countryID = elem.getElementsByTagName("wb:country").item(0).getChildNodes().item(0).getNodeValue();
                        String year = elem.getElementsByTagName("wb:date").item(0).getChildNodes().item(0).getNodeValue();
                        String value = "";
                        NodeList nodeValue = elem.getElementsByTagName("wb:value").item(0).getChildNodes();
                        Boolean exists = false;
                        if(nodeValue.getLength() != 0) {
                            value = elem.getElementsByTagName("wb:value").item(0).getChildNodes().item(0).getNodeValue();
                        } else {
                            value = "";
                        }
                        for(Country c: countriesList) {
                            if(c.getName().equals(countryID)) {
                                c.addGDP(year, value);
                                exists = true;
                                break;
                            }
                        }
                        if(!exists) {
                            Country country = new Country(countryID, year, value);
                            countriesList.add(country);
                        }
                    }
                }
                Country country = countriesList.get(10);
                System.out.println(country.getName());
                System.out.println("Testing!");
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("THERE WAS AN ERROR!");
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
