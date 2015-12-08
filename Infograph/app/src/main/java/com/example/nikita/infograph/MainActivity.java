package com.example.nikita.infograph;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    String currentEnergy;

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

        renewableBtn.setOnCheckedChangeListener(checkChange);
        totalEnergyBtn.setOnCheckedChangeListener(checkChange);
        industrialBtn.setOnCheckedChangeListener(checkChange);
        savingsBtn.setOnCheckedChangeListener(checkChange);

        currentEnergy = renewableBtn.getText().toString();
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

        //Executes parsing the XML in another Thread
            new parseXML().execute(renewableURL, industrialURL, totalEnergyURL, savingsURL);
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
            renewableBtn.setChecked(true);
        }
    }

    public void loadChartData(WebView webview, int year, String energyType) {
        mediumText = "";
        for(int i = 0; i < countriesList.size(); i++) {
            Country c = countriesList.get(i);
            if(i == countriesList.size()-1) {
                switch(energyType) {
                    case "Renewable energy \n" +
                            " share of TFEC (%)": mediumText += c.getRenewable(year);
                        break;
                    case "Total final energy consumption (TFEC) (TJ)": mediumText += c.getFinalConsumption(year);
                        break;
                    case "Energy intensity of industrial sector \n" +
                            " (MJ/2011 USD PPP)": mediumText += c.getIndustrial(year);
                        break;
                    default: mediumText += c.getSavings(year);
                }
            } else {
                switch(energyType) {
                    case "Renewable energy \n" +
                            " share of TFEC (%)": mediumText += c.getRenewable(year) + ", \n";;
                        break;
                    case "Total final energy consumption (TFEC) (TJ)": mediumText += c.getFinalConsumption(year) + ", \n";;
                        break;
                    case "Energy intensity of industrial sector \n" +
                            " (MJ/2011 USD PPP)": mediumText += c.getIndustrial(year) + ", \n";;
                        break;
                    default: mediumText += c.getSavings(year) + ", \n";;
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
                    currentEnergy = renewableBtn.getText().toString();
                    renewableBtn.setBackgroundColor(Color.parseColor("#9EFF0044"));
                    industrialBtn.setChecked(false);
                    industrialBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    totalEnergyBtn.setChecked(false);
                    totalEnergyBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    savingsBtn.setChecked(false);
                    savingsBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    loadChartData(chartView, selectedYear, currentEnergy);

                }
                if(button == savingsBtn) {
                    currentEnergy = savingsBtn.getText().toString();
                    System.out.println(savingsBtn.getText().toString());
                    savingsBtn.setBackgroundColor(Color.parseColor("#9EFF0044"));
                    industrialBtn.setChecked(false);
                    industrialBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    totalEnergyBtn.setChecked(false);
                    totalEnergyBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    renewableBtn.setChecked(false);
                    renewableBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
                if(button == industrialBtn) {
                    currentEnergy = industrialBtn.getText().toString();
                    industrialBtn.setBackgroundColor(Color.parseColor("#9EFF0044"));
                    savingsBtn.setChecked(false);
                    savingsBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    totalEnergyBtn.setChecked(false);
                    totalEnergyBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    renewableBtn.setChecked(false);
                    renewableBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
                if(button == totalEnergyBtn) {
                    currentEnergy = totalEnergyBtn.getText().toString();
                    totalEnergyBtn.setBackgroundColor(Color.parseColor("#9EFF0044"));
                    industrialBtn.setChecked(false);
                    industrialBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    savingsBtn.setChecked(false);
                    savingsBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    renewableBtn.setChecked(false);
                    renewableBtn.setBackgroundColor(Color.parseColor("#96C41C9A"));
                    loadChartData(chartView, selectedYear, currentEnergy);
                }
            }
        }
    };

    public void updateHeaderText(String energyType) {
        headerText = "<html> <head> <meta name='viewport' content='width=device-width, height=device-height' /> <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script> <script type=\"text/javascript\"> " +
                "google.load(\"visualization\", \"1\", {packages:[\"geochart\"]}); google.setOnLoadCallback(drawRegionsMap); function drawRegionsMap() { var data = google.visualization.arrayToDataTable([ ['Country', '" + energyType + "'], ";
    }
}
