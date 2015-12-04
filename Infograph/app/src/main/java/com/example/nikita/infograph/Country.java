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
    private Map<Integer, String> renewableEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> industrialEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> energyConsumption = new HashMap<Integer, String>();
    private Map<Integer, String> energySavings = new HashMap<Integer, String>();

    private static final String RENEWABLE_ID = "Renewable energy share of TFEC (%)";
    private static final String INDUSTRIAL_ID = "Energy intensity of industrial sector (MJ/2011 USD PPP)";
    private static final String ENERGYCONSUMP_ID = "Total final energy consumption (TFEC) (TJ)";

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
            case RENEWABLE_ID:
                renewableEnergy.put(year, value);
                break;
            case INDUSTRIAL_ID: industrialEnergy.put(year, value);
                break;
            case ENERGYCONSUMP_ID: energyConsumption.put(year, value);
                break;
            default: energySavings.put(year, value);
        }
    }

    public String getName() {
        return name;
    }

    public String getRenewableEnergyForYear(int year) {
        return renewableEnergy.get(year);
    }

    public String getIndustrialEnergyForYear(int year) { return industrialEnergy.get(year); }

    public String getEnergyConsumptionForYear(int year) { return energyConsumption.get(year); }

    public String getEnergySavingsForYear(int year) { return energySavings.get(year); }

    public String getRenewable(int year) {
        return "[ '" + name + "', " + renewableEnergy.get(year) + "]";
    }

    public String getIndustrial(int year) {
        return "[ '" + name + "', " + industrialEnergy.get(year) + "]";
    }

    public String getFinalConsumption(int year) {
        return "[ '" + name + "', " + energyConsumption.get(year) + "]";
    }

    public String getSavings(int year) {
        return "[ '" + name + "', " + energySavings.get(year) + "]";
    }
}
