package com.example.asus.bussola.API;

/**
 * Created by ASUS on 14/06/2017.
 */

/**
 * Classe OpenWeatherMap que instancia as outras classes definidas no projeto
 */
public class OpenWeatherMap {
    private Weather weather = new Weather();
    private Main main = new Main();
    private Sys sys = new Sys();
    private String name;

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
