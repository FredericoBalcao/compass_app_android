package com.example.asus.bussola.API;

import android.location.Location;
import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ASUS on 14/06/2017.
 */

/**
 * Classe Helper que instancia a classe OpenWeatherMap para os dados recebidos
 * Baseada no link da API e extras (APPID)
 */
public class Helper {
    static String stream = null;
    private OpenWeatherMap dados = null;
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static String EXTRAS = "&appid=ad430dcc825cbd64fe41d6fa87bdaafd&lang=pt&units=metric";

    public Helper() {

    }

    public OpenWeatherMap getHTTPData(Location location) {
        //politicas de segurança
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Double lat, lon;
        lat = location.getLatitude();
        lon = location.getLongitude();

        String urlString = BASE_URL + "lat=" + lat.toString() + "&lon=" + lon.toString() + EXTRAS;

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(100000);
            httpURLConnection.setConnectTimeout(100000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null)
                sb.append(line);
            stream = sb.toString();
            httpURLConnection.disconnect();
            JSONObject json = new JSONObject(stream);
            parser(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dados;
    }

    public void parser(JSONObject jo) {
        dados = new OpenWeatherMap();
        try {
            dados.setName(jo.getString("name"));
            JSONObject jo1 = jo.getJSONObject("main");
            dados.getMain().setTemp(jo1.getDouble("temp"));
            dados.getMain().setTemp_min(jo1.getDouble("temp_min"));
            dados.getMain().setTemp_max(jo1.getDouble("temp_max"));

            JSONArray ja = jo.getJSONArray("weather");
            dados.getWeather().setDescription(ja.getJSONObject(0).getString("description"));
            dados.getWeather().setIcon(ja.getJSONObject(0).getString("icon"));

            JSONObject jo2 = jo.getJSONObject("sys");
            dados.getSys().setSunrise(jo2.getString("sunrise"));
            dados.getSys().setSunset(jo2.getString("sunset"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
