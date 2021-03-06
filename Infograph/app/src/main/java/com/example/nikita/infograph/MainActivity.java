package com.example.nikita.infograph;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String RENEWABLE_ID = "Renewable energy share of TFEC (%)";
    private static final String INDUSTRIAL_ID = "Energy intensity of industrial sector (MJ/2011 USD PPP)";
    private static final String ENERGYCONSUMP_ID = "Total final energy consumption (TFEC) (TJ)";
    private static final String THERMAL_ID = "Thermal efficiency in power supply (%)";
    private static final String AGRICULTURAL_ID = "Energy intensity of agricultural sector (MJ/2011 USD PPP)";
    private static final String SAVINGS_ID = "Energy savings of primary energy (TJ)";

    List<Country> countriesList;

    WebView chartView; //The main view for the chart
    String chartText; //The total text to be submitted to the chart

    SeekBar yearSeek;
    TextView yearText;
    int selectedYear;

    ToggleButton renewableBtn;
    ToggleButton industrialBtn;
    ToggleButton totalEnergyBtn;
    ToggleButton savingsBtn;
    ToggleButton thermalBtn;
    ToggleButton agriculturalBtn;



    /*
    Header, Body and Footer of text to be submitted to the webview to create an HTML Page with a JS Script to retrieve a chart from the Google APIs
     */
    String headerText = "";
    String mediumText = "";
    String endText;

    String initialURL = "http://api.worldbank.org/countries/ALB;ARM;AUT;BLR;BEL;BIH;BGR;CHI;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;MD;DEU;GRC;TR;HUN;ISL;IRL;IMY;ITA;KSV;LVA;LIE;LTU;LUX;MKD;MCO;MNE;NLD;NOR;POL;PRT;ROM;RUS;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR;MLT/indicators/";
    String renewableURL = initialURL + "2.1_SHARE.TOTAL.RE.IN.TFEC?per_page=700&date=2000%3A2012";
    String industrialURL = initialURL + "13.1_INDUSTRY.ENERGY.INTENSITY?per_page=700&date=2000%3A2012";
    String totalEnergyURL = initialURL + "1.1_TOTAL.FINAL.ENERGY.CONSUM?per_page=700&date=2000%3A2012";
    String savingsURL = initialURL + "10.1_ENERGY.SAVINGS?per_page=700&date=2000:2012";
    String thermalURL = initialURL + "11.1_THERMAL.EFFICIENCY?per_page=700&date=2000%3A2012";
    String agriculturalURL = initialURL + "14.1_AGR.ENERGY.INTENSITY?per_page=700&date=2000%3A2012";

    int currentEnergy;

    private final String tempFileName = "data.txt";
    public File tempFile;
    boolean isFileAvailable;

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

        renewableBtn = (ToggleButton) findViewById(R.id.renewableButton);
        totalEnergyBtn = (ToggleButton) findViewById(R.id.totalButton);
        industrialBtn = (ToggleButton) findViewById(R.id.industrialButton);
        savingsBtn = (ToggleButton) findViewById(R.id.savingsBtn);
        thermalBtn = (ToggleButton) findViewById(R.id.thermalButton);
        agriculturalBtn = (ToggleButton) findViewById(R.id.agriculturalButton);

        renewableBtn.setOnCheckedChangeListener(checkChange);
        totalEnergyBtn.setOnCheckedChangeListener(checkChange);
        industrialBtn.setOnCheckedChangeListener(checkChange);
        savingsBtn.setOnCheckedChangeListener(checkChange);
        thermalBtn.setOnCheckedChangeListener(checkChange);
        agriculturalBtn.setOnCheckedChangeListener(checkChange);

        currentEnergy = renewableBtn.getId();
        updateHeaderText(currentEnergy);

        /*
        Initialises the Seekbar and text associated with the year - Sets it to a range of 1990-2012
         */
        yearSeek = (SeekBar) findViewById(R.id.yearSeek);
        yearText = (TextView) findViewById(R.id.yearTextView);
        selectedYear = 2011;
        yearSeek.setMax(12);
        yearText.setText(String.valueOf(selectedYear));
        yearSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                selectedYear = progress + 2000;
                yearText.setText(Integer.toString(selectedYear));
                if(countriesList != null && countriesList.size() != 0)
                    loadChartData(chartView, selectedYear, currentEnergy);
            }
        });

        /*
        Edits the properties of the HTML file to be injected into the WebView to retrieve the chart using the screens dimensions.
         */
        endText = "]);\n" +
                "\n" +
                "        var options = {region: '150', backgroundColor: '#008080', colorAxis: {colors: ['#00853f', '#ffff00', '#e31b23']}, datalessRegionColor: '#f8bbd0'};\n" +
                "\n" +
                "        var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));\n" +
                "\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body style='background-color: #008080;'>\n" +
                "    <div id=\"regions_div\" style=\"width: 1178px; height: 600px;\"></div>\n" +
                "  </body>\n" +
                "</html>";
        chartView = (WebView) findViewById(R.id.chartView);
        chartView.getSettings().setJavaScriptEnabled(true);

        /** Getting Cache Directory */
        File cDir = getBaseContext().getFilesDir();
        Log.d("Cache", getFilesDir().toString());
        tempFile = new File(cDir.getPath() + "/data.txt");

        if(tempFile.exists())
        {
            isFileAvailable = true;
        }
        Log.d("Internet", "Cache file exists? " + isFileAvailable);

        //Executes parsing the XML in another Thread
            new parseXML().execute(renewableURL, industrialURL, totalEnergyURL, savingsURL, thermalURL, agriculturalURL);
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

            if(!isFileAvailable)
            {
                for(int i = 0; i < params.length; i++) {
                    parse.parseXML(params[i],false,null);
                }
                countriesList = parse.getCountriesList();

                List<String> countriesData = parse.getCountriesData();

                FileOutputStream fos;
                try
                {
                    fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                    for(String countryData : countriesData)
                    {
                        fos.write((countryData + "\n").getBytes());
                    }
                    fos.flush();
                    fos.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("Internet","There is an error!");
                }
            }
            else
            {
                for(int i = 0; i < params.length; i++) {
                    parse.parseXML(params[i],true,tempFile);
                }
                countriesList = parse.getCountriesList();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            renewableBtn.setChecked(true);
        }
    }

    /**
     * This method is used to load the map of the application
     * and change its colour based on the user's option
     * @param webview
     * @param year the year that the data refer to
     * @param energyType the option which the user wants to observe
     */
    public void loadChartData(WebView webview, int year, int energyType) {
        mediumText = "";
        for(int i = 0; i < countriesList.size(); i++) {
            Country c = countriesList.get(i);
            if(i == countriesList.size()-1) {
                if(renewableBtn.getId() == energyType) {
                    mediumText += c.getRenewable(year);
                } else if(totalEnergyBtn.getId() == energyType) {
                    mediumText += c.getFinalConsumption(year);
                } else if(industrialBtn.getId() == energyType) {
                    mediumText += c.getIndustrial(year);
                } else if(savingsBtn.getId() == energyType) {
                    mediumText += c.getSavings(year);
                } else if(thermalBtn.getId() == energyType) {
                    mediumText += c.getThermal(year);
                } else {
                    mediumText += c.getAgricultural(year);
                }
            } else {
                if(renewableBtn.getId() == energyType) {
                    mediumText += c.getRenewable(year) + ", \n";
                } else if(totalEnergyBtn.getId() == energyType) {
                    mediumText += c.getFinalConsumption(year) + ", \n";
                } else if(industrialBtn.getId() == energyType) {
                    mediumText += c.getIndustrial(year) + ", \n";
                } else if(savingsBtn.getId() == energyType) {
                    mediumText += c.getSavings(year) + ", \n";
                } else if(thermalBtn.getId() == energyType) {
                    mediumText += c.getThermal(year) + ", \n";
                } else {
                    mediumText += c.getAgricultural(year) + ", \n";
                }
            }
        }
        updateHeaderText(energyType);
        chartText = headerText + mediumText + endText;
        webview.loadData(chartText, "text/html", null);
        webview.setVisibility(View.VISIBLE);
    }

    CompoundButton.OnCheckedChangeListener checkChange = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if(isChecked) {
                if(button == renewableBtn) {
                    currentEnergy = renewableBtn.getId();
                    System.out.println(currentEnergy);
                    industrialBtn.setChecked(false);
                    totalEnergyBtn.setChecked(false);
                    savingsBtn.setChecked(false);
                    agriculturalBtn.setChecked(false);
                    thermalBtn.setChecked(false);
                    loadChartData(chartView, selectedYear, currentEnergy);

                }
                if(button == savingsBtn) {
                    currentEnergy = savingsBtn.getId();
                    industrialBtn.setChecked(false);
                    totalEnergyBtn.setChecked(false);
                    renewableBtn.setChecked(false);
                    agriculturalBtn.setChecked(false);
                    thermalBtn.setChecked(false);
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
                if(button == industrialBtn) {
                    currentEnergy = industrialBtn.getId();
                    savingsBtn.setChecked(false);
                    totalEnergyBtn.setChecked(false);
                    renewableBtn.setChecked(false);
                    agriculturalBtn.setChecked(false);
                    thermalBtn.setChecked(false);
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
                if(button == totalEnergyBtn) {
                    currentEnergy = totalEnergyBtn.getId();
                    industrialBtn.setChecked(false);
                    savingsBtn.setChecked(false);
                    renewableBtn.setChecked(false);
                    agriculturalBtn.setChecked(false);
                    thermalBtn.setChecked(false);
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
                if(button == thermalBtn) {
                    currentEnergy = thermalBtn.getId();
                    industrialBtn.setChecked(false);
                    savingsBtn.setChecked(false);
                    renewableBtn.setChecked(false);
                    totalEnergyBtn.setChecked(false);
                    agriculturalBtn.setChecked(false);
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
                if(button == agriculturalBtn) {
                    currentEnergy = agriculturalBtn.getId();
                    industrialBtn.setChecked(false);
                    savingsBtn.setChecked(false);
                    renewableBtn.setChecked(false);
                    totalEnergyBtn.setChecked(false);
                    thermalBtn.setChecked(false);
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
            }
        }
    };

    /**
     * This method is used to update the Header
     * of text which is submitted to the webview
     * to create an HTML Page with a JS Script to retrieve a chart from the Google APIs
     * @param energyType the option which the user wants to observe
     */
    public void updateHeaderText(int energyType) {
        String energyText = "";
        if(renewableBtn.getId() == energyType) {
           energyText = RENEWABLE_ID;
        } else if(totalEnergyBtn.getId() == energyType) {
            energyText = ENERGYCONSUMP_ID;
        } else if(industrialBtn.getId() == energyType) {
            energyText = INDUSTRIAL_ID;
        } else if(savingsBtn.getId() == energyType) {
            energyText = SAVINGS_ID;
        } else if(thermalBtn.getId() == energyType) {
            energyText = THERMAL_ID;
        } else {
            energyText = AGRICULTURAL_ID;
        }
        headerText = "<html> <head> <meta name='viewport' content='width=device-width, height=device-height' /> <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script> <script type=\"text/javascript\"> " +
                "google.load(\"visualization\", \"1\", {packages:[\"geochart\"]}); google.setOnLoadCallback(drawRegionsMap); function drawRegionsMap() { var data = google.visualization.arrayToDataTable([ ['Country', '" + energyText + "'], ";
    }
}
