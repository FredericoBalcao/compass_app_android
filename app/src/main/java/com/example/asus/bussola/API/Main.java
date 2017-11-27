package com.example.asus.bussola.API;

/**
 * Created by ASUS on 14/06/2017.
 */

/**
 * Classe Main para obter as temperaturas (actual, minima, maxima)
 */
public class Main {
    private double temp;
    private double temp_min;
    private double temp_max;

    public String getTemp() {
        String str = Double.toString(temp)+"ºC";
        return str;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getTemp_min() {
        String str = Double.toString(temp_min)+"ºC";
        return str;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public String getTemp_max() {
        String str = Double.toString(temp_max)+"ºC";
        return str;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }
}
