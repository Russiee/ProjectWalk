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
import android.widget.SeekBar;
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

    SeekBar yearSeek;
    TextView yearText;
    int selectedYear;

    /*
    Header, Body and Footer of text to be submitted to the webview to create an HTML Page with a JS Script to retrieve a chart from the Google APIs
     */
    String headerText = "<html> <head> <meta name='viewport' content='width=device-width, height=device-height' /> <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script> <script type=\"text/javascript\"> " +
                        "google.load(\"visualization\", \"1\", {packages:[\"geochart\"]}); google.setOnLoadCallback(drawRegionsMap); function drawRegionsMap() { var data = google.visualization.arrayToDataTable([ ['Country', 'GDP'], ";
    String mediumText = "";
    String endText;

    String initialURL = "http://api.worldbank.org/countries/ALB;ARM;AUT;BLR;BEL;BIH;BGR;CHI;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;MD;DEU;GRC;TR;HUN;ISL;IRL;IMY;ITA;KSV;LVA;LIE;LTU;LUX;MKD;MCO;MNE;NLD;NOR;POL;PRT;ROM;RUS;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR;MLT/indicators/";
    String solarURL = initialURL + "2.1.6_SHARE.SOLAR?per_page=2000&date=1990%3A2015&MRV=25&Gapfill=Y";
    String windURL = initialURL + "2.1.5_SHARE.WIND?per_page=2000&date=1990%3A2015&MRV=25&Gapfill=Y";
    String biofuelURL = initialURL + "2.1.4_SHARE.BIOFUELS?per_page=2000&date=1990%3A2015&MRV=25&Gapfill=Y";
    String hydroURL = initialURL + "2.1.3_SHARE.HYDRO?per_page=2000&date=1990%3A2015&MRV=25&Gapfill=Y";
    String wasteURL = initialURL + "2.1.8_SHARE.WASTE?per_page=2000&date=1990%3A2015&MRV=25&Gapfill=Y";

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
        yearSeek = (SeekBar) findViewById(R.id.yearSeek);
        yearText = (TextView) findViewById(R.id.yearTextView);
        selectedYear = 2011;
        yearSeek.setMax(22);
        yearText.setText(String.valueOf(selectedYear));
        yearSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                selectedYear = progress + 1990;
                yearText.setText(Integer.toString(selectedYear));
                loadChartData(chartView, selectedYear);
            }
        });

        /*
        Edits the properties of the HTML file to be injected into the WebView to retrieve the chart using the screens dimensions.
         */
        endText = "]);\n" +
                "\n" +
                "        var options = {region: '150', backgroundColor: '#3F424F', datalessRegionColor: '#3F524F'};\n" +
                "\n" +
                "        var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));\n" +
                "\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body style='background-color: #3F423F;'>\n" +
                "    <div id=\"regions_div\" style=\"width: 1178px; height: 600px;\"></div>\n" +
                "  </body>\n" +
                "</html>";
        chartView = (WebView) findViewById(R.id.chartView);
        chartView.getSettings().setJavaScriptEnabled(true);

        //Executes parsing the XML in another Thread
            new parseXML().execute(solarURL, windURL, biofuelURL, hydroURL, wasteURL);
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
            ParseXML parse = new ParseXML();
            for(int i = 0; i < params.length; i++) {
                parse.parseXML(params[i]);
            }
            countriesList = parse.getCountriesList();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            loadChartData(chartView, selectedYear);
        }
    }

    public void loadChartData(WebView webview, int year) {
        mediumText = "";
        for(int i = 0; i < countriesList.size(); i++) {
            Country c = countriesList.get(i);
            if(i == countriesList.size()-1) {
                mediumText += c.getBiofuel(year);
            } else {
                mediumText += c.getBiofuel(year) + ", \n";
            }
        }
        chartText = headerText + mediumText + endText;
        webview.loadData(chartText, "text/html", null);
        webview.setVisibility(View.VISIBLE);
    }
}
