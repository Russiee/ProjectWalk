package com.example.nikita.infograph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikita on 26/11/2015.
 */
public class Country {

    private String name;
    private List<String> years = new ArrayList<String>();
    private Map<String, String> solarEnergy = new HashMap<String, String>();
    private Map<String, String> windEnergy = new HashMap<String, String>();
    private Map<String, String> biofuelEnergy = new HashMap<String, String>();
    private Map<String, String> hydroEnergy = new HashMap<String, String>();
    private Map<String, String> wasteEnergy = new HashMap<String, String>();

    private static final String SOLAR_ID = "Solar energy share of TFEC (%)";
    private static final String WIND_ID = "Wind energy share of TFEC (%)";
    private static final String BIO_ID = "Liquid biofuels share of TFEC (%)";
    private static final String HYDRO_ID = "Hydro energy share of TFEC (%)";

    public Country(String name, String year, String value, String energyType) {
        this.name = name;
        years.add(year);
        addToEnergyType(energyType, year, value);
    }

    public Country(String name) {
        this.name = name;
    }

    public void addValue(String year, String value, String energyType) {
        years.add(year);
        addToEnergyType(energyType, year, value);
    }

    private void addToEnergyType(String energy, String year, String value) {
        switch(energy) {
            case SOLAR_ID: solarEnergy.put(year, value);
                break;
            case WIND_ID: windEnergy.put(year, value);
                break;
            case BIO_ID: biofuelEnergy.put(year, value);
                break;
            case HYDRO_ID: hydroEnergy.put(year, value);
                break;
            default: wasteEnergy.put(year, value);
        }
    }

    public String getName() {
        return name;
    }

    public String getSolarEnergyForYear(String year) {
        return solarEnergy.get(year);
    }

    public String getWindEnergyForYear(String year) { return windEnergy.get(year); }

    public String getBioEnergyForYear(String year) { return biofuelEnergy.get(year); }

    public String getHydroEnergyForYear(String year) { return hydroEnergy.get(year); }

    public String getWasteEnergyForYear(String year) { return wasteEnergy.get(year); }

    public String getSolar(String year) {
        return "[ '" + name + "', " + solarEnergy.get(year) + "]";
    }

    public String getWind(String year) {
        return "[ '" + name + "', " + windEnergy.get(year) + "]";
    }

    public String getBiofuel(String year) {
        return "[ '" + name + "', " + biofuelEnergy.get(year) + "]";
    }

    public String getHydro(String year) {
        return "[ '" + name + "', " + hydroEnergy.get(year) + "]";
    }

    public String getWaste(String year) {
        return "[ '" + name + "', " + wasteEnergy.get(year) + "]";
    }
}
