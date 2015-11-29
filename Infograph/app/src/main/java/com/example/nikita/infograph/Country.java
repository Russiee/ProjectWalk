package com.example.nikita.infograph;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Nikita on 26/11/2015.
 */
public class Country {

    private String name;
    private List<String> years = new ArrayList<String>();
    private List<String> values = new ArrayList<String>();

    public Country(String name, String year, String value) {
        this.name = name;
        years.add(year);
        values.add(value);
    }

    public Country(String name) {
        this.name = name;
    }

    public void addGDP(String year, String value) {
        years.add(year);
        values.add(value);
    }

    public String toString() {
        String toReturn = name + ": ";
        for(int i = 0; i < years.size(); i++) {
            toReturn += "\nYear: " + years.get(i) + "\nValue: " + values.get(i) + "\n";
        }
        return toReturn;
    }

    public String getName() {
        return name;
    }
}
