package com.example.nikita.infograph;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    WebView chartView; //The main view for the chart
    String chartText; //The total text to be submitted to the chart

    /*
    Header, Body and Footer of text to be submitted to the webview to create an HTML Page with a JS Script to retrieve a chart from the Google APIs
     */
    String headerText = "<html> <head> <meta name='viewport' content='width=device-length, height=device-height' /> <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script> <script type=\"text/javascript\"> " +
            "google.load(\"visualization\", \"1\", {packages:[\"geochart\"]}); google.setOnLoadCallback(drawRegionsMap); function drawRegionsMap() { var data = google.visualization.arrayToDataTable([ ['Country', 'GDP'], ";
    String mediumText = "";
    String endText;

    /**
     * Creates the Activity - without a title
     * Retrieves screen size - Adjusts webview size to fit screen
     * Executes XMLparsing in another thread
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes TitleBar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Calculates display size of the device it is used on and fits the WebView to be full screen around it
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int width = displayMetrics.widthPixels/2;
        int height = displayMetrics.heightPixels/2;

        /*
        Edits the properties of the HTML file to be injected into the WebView to retrieve the chart using the screens dimensions.
         */
        endText = "]);\n" + "\n" + "        var options = {region: '150', backgroundColor: '#81d4fa', datalessRegionColor: '#f8bbd0', height: "+height+", width: "+ width + "};\n" + "\n" + "        var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));\n" + "\n" +
                "        chart.draw(data, options);\n" + "      }\n" + "    </script>\n" + "  </head>\n" + "  <body>\n" + "    <div id=\"regions_div\" style=\"width: "+width+"px; height: "+height+"px;\"></div>\n" + "  </body>\n" + "</html>";
        chartView = (WebView) findViewById(R.id.chartView);

        //Executes parsing the XML in another Thread
            new parseXML().execute("");
    }

    private class parseXML extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading XML Data...");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
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
                return true;
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("THERE WAS AN ERROR!");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            loadChartData(chartView, "2014");
        }
    }

    public void loadChartData(WebView webview, String year) {
        for(int i = 0; i < countriesList.size(); i++) {
            Country c = countriesList.get(i);
            if(i == countriesList.size()-1) {
                mediumText += c.getNameAndValue(year);
            } else {
                mediumText += c.getNameAndValue(year) + ", \n";
            }
        }
        chartText = headerText + mediumText + endText;
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadData(chartText, "text/html", null);
        webview.setVisibility(View.VISIBLE);
    }
}
