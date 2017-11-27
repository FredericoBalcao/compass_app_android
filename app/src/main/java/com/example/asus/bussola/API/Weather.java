package com.example.asus.bussola.API;

/**
 * Created by ASUS on 14/06/2017.
 */

/**
 * Classe Weather que define a descricao e o icone do estado do tempo da localizacao obtida
 */
public class Weather {
    private String description;
    private String icon;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
