package com.example.asus.bussola.API;

import java.util.Date;

/**
 * Created by ASUS on 14/06/2017.
 */

/**
 * Classe Sys que define o a hora em que o sol nasce e se pôe (sunrise e sunset)
 */
public class Sys {
    private String sunrise;
    private String sunset;

    private String convertTimeToString(String epochString){
        long epoch = Long.parseLong( epochString );
        Date expiry = new Date( epoch * 1000 );
        Integer Hours = expiry.getHours();
        Integer Min = expiry.getMinutes();
        String zero_hours="",zero_min="";
        if(Hours <10){
            zero_hours ="0";
        }
        if(Min <10){
            zero_min ="0";
        }

        String ret = zero_hours+Hours.toString()+"h:"+zero_min+Min.toString()+"m";
        return ret;
    }

    public String getSunrise() {
        String str = convertTimeToString(sunrise);
        return str;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        String str = convertTimeToString(sunset);
        return str;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }
}
