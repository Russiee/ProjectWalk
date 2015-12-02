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
    private List<Integer> years = new ArrayList<Integer>();
    private Map<Integer, String> solarEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> windEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> biofuelEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> hydroEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> wasteEnergy = new HashMap<Integer, String>();

    private static final String SOLAR_ID = "Solar energy share of TFEC (%)";
    private static final String WIND_ID = "Wind energy share of TFEC (%)";
    private static final String BIO_ID = "Liquid biofuels share of TFEC (%)";
    private static final String HYDRO_ID = "Hydro energy share of TFEC (%)";

    public Country(String name, int year, String value, String energyType) {
        this.name = name;
        years.add(year);
        addToEnergyType(energyType, year, value);
    }

    public Country(String name) {
        this.name = name;
    }

    public void addValue(int year, String value, String energyType) {
        years.add(year);
        addToEnergyType(energyType, year, value);
    }

    private void addToEnergyType(String energy, int year, String value) {
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

    public String getSolarEnergyForYear(int year) {
        return solarEnergy.get(year);
    }

    public String getWindEnergyForYear(int year) { return windEnergy.get(year); }

    public String getBioEnergyForYear(int year) { return biofuelEnergy.get(year); }

    public String getHydroEnergyForYear(int year) { return hydroEnergy.get(year); }

    public String getWasteEnergyForYear(int year) { return wasteEnergy.get(year); }

    public String getSolar(int year) {
        return "[ '" + name + "', " + solarEnergy.get(year) + "]";
    }

    public String getWind(int year) {
        return "[ '" + name + "', " + windEnergy.get(year) + "]";
    }

    public String getBiofuel(int year) {
        return "[ '" + name + "', " + biofuelEnergy.get(year) + "]";
    }

    public String getHydro(int year) {
        return "[ '" + name + "', " + hydroEnergy.get(year) + "]";
    }

    public String getWaste(int year) {
        return "[ '" + name + "', " + wasteEnergy.get(year) + "]";
    }
}
