package com.example.nikita.infograph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to match all the countries for which there
 * are data to the years to which the data refer,
 * the types of the energies and their values
 * Created by Nikita on 26/11/2015.
 */
public class Country {

    private String name;
    private List<Integer> years = new ArrayList<Integer>();
    private Map<Integer, String> renewableEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> industrialEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> energyConsumption = new HashMap<Integer, String>();
    private Map<Integer, String> energySavings = new HashMap<Integer, String>();
    private Map<Integer, String> thermalEnergy = new HashMap<Integer, String>();
    private Map<Integer, String> agriculturalEnergy = new HashMap<Integer, String>();

    private static final String RENEWABLE_ID = "Renewable energy share of TFEC (%)";
    private static final String INDUSTRIAL_ID = "Energy intensity of industrial sector (MJ/2011 USD PPP)";
    private static final String ENERGYCONSUMP_ID = "Total final energy consumption (TFEC) (TJ)";
    private static final String THERMAL_ID = "Thermal efficiency in power supply (%)";
    private static final String AGRICULTURAL_ID = "Energy intensity of agricultural sector (MJ/2011 USD PPP)";


    /**
     * This is the first constructor of the Country class,
     * it is used when there are data that can be displayed
     * @param name the name of the country
     * @param year the year that the data refer to
     * @param value the value of the option the user wants to observe
     * @param energyType the option which the user wants to observe
     */
    public Country(String name, int year, String value, String energyType) {
        this.name = name;
        years.add(year);
        addToEnergyType(energyType, year, value);
    }

    /**
     * This is the second constructor of the Country class
     * it is used when there are no data except for the name of
     * the country
     * @param name the name of the country
     */
    public Country(String name) {
        this.name = name;
    }

    /**
     * This method is used to add the value of the option
     * the user wants to observe
     * @param year the year that the data refer to
     * @param value the value of the option the user wants to observe
     * @param energyType the option which the user wants to observe
     */
    public void addValue(int year, String value, String energyType) {
        years.add(year);
        addToEnergyType(energyType, year, value);
    }

    /**
     * This method is used to match the year that the data refer to
     * and the value of the option the user wants to observe
     * to the right type of energy
     * @param energy the type of the energy
     * @param year the year that the data refer to
     * @param value the value of the option the user wants to observe
     */
    private void addToEnergyType(String energy, int year, String value) {
        switch(energy) {
            case RENEWABLE_ID:
                renewableEnergy.put(year, value);
                break;
            case INDUSTRIAL_ID: industrialEnergy.put(year, value);
                break;
            case ENERGYCONSUMP_ID: energyConsumption.put(year, value);
                break;
            case THERMAL_ID: thermalEnergy.put(year, value);
                break;
            case AGRICULTURAL_ID: agriculturalEnergy.put(year, value);
                break;
            default: energySavings.put(year, value);
        }
    }

    /**
     * This method is used to get the name of the country
     * @return the name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * This method is used to get the value of the
     * renewable energy in a specific year
     * @param year the year that the data refer to
     * @return the value of the renewable energy in the specific year
     */
    public String getRenewableEnergyForYear(int year) {
        return renewableEnergy.get(year);
    }

    /**
     * This method is used to get the value of the
     * industrial energy in a specific year
     * @param year the year that the data refer to
     * @return the value of the industrial energy in the specific year
     */
    public String getIndustrialEnergyForYear(int year) { return industrialEnergy.get(year); }

    /**
     * This method is used to get the value of the
     * energy consumption in a specific year
     * @param year the year that the data refer to
     * @return the value of the energy consumption in the specific year
     */
    public String getEnergyConsumptionForYear(int year) { return energyConsumption.get(year); }

    /**
     * This method is used to get the value of the
     * energy savings in a specific year
     * @param year the year that the data refer to
     * @return the value of the energy savings in the specific year
     */
    public String getEnergySavingsForYear(int year) { return energySavings.get(year); }

    /**
     * This method is used to get a string, which the user can observe,
     * with the value of the renewable energy in a specific country in a specific year
     * @param year the year that the data refer to
     * @return a string which the user can observe with the name of the country their interested in and the value of the renewable energy in the specific year in the specific country
     */
    public String getRenewable(int year) {
        return "[ '" + name + "', " + renewableEnergy.get(year) + "]";
    }

    /**
     * This method is used to get a string, which the user can observe,
     * with the value of the industrial energy in a specific country in a specific year
     * @param year the year that the data refer to
     * @return a string which the user can observe with the name of the country their interested in and the value of the industrial energy in the specific year in the specific country
     */
    public String getIndustrial(int year) {
        return "[ '" + name + "', " + industrialEnergy.get(year) + "]";
    }

    /**
     * This method is used to get a string, which the user can observe,
     * with the value of the energy consumption in a specific country in a specific year
     * @param year the year that the data refer to
     * @return a string which the user can observe with the name of the country their interested in and the value of the energy consumption in the specific year in the specific country
     */
    public String getFinalConsumption(int year) {
        return "[ '" + name + "', " + energyConsumption.get(year) + "]";
    }

    /**
     * This method is used to get a string, which the user can observe,
     * with the value of the energy savings in a specific country in a specific year
     * @param year the year that the data refer to
     * @return a string which the user can observe with the name of the country their interested in and the value of the energy savings in the specific year in the specific country
     */
    public String getSavings(int year) {
        return "[ '" + name + "', " + energySavings.get(year) + "]";
    }

    /**
     * This method is used to get a string, which the user can observe,
     * with the value of the thermal energy in a specific country in a specific year
     * @param year the year that the data refer to
     * @return a string which the user can observe with the name of the country their interested in and the value of the thermal energy in the specific year in the specific country
     */
    public String getThermal(int year) {
        return "[ '" + name + "', " + thermalEnergy.get(year) + "]";
    }

    /**
     * This method is used to get a string, which the user can observe,
     * with the value of the agricultural energy in a specific country in a specific year
     * @param year the year that the data refer to
     * @return a string which the user can observe with the name of the country their interested in and the value of the agricultural energy in the specific year in the specific country
     */
    public String getAgricultural(int year) {
        return "[ '" + name + "', " + agriculturalEnergy.get(year) + "]";
    }

    public Map getRenewebleMap(){
        return renewableEnergy;
    }
    public Map getIndustryMap(){
        return industrialEnergy;
    }
    public Map getFinalConsunptionMap(){
        return energyConsumption;
    }
    public Map getSavingsMap(){
        return energySavings;
    }

    public Map getThermalMap() {
        return thermalEnergy;
    }

    public Map getAgriculturalMap() {
        return agriculturalEnergy;
    }
}
