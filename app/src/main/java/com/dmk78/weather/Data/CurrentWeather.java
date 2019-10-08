package com.dmk78.weather.Data;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {

    private CurrentWeatherMain main;

    private CurrentWeatherWind wind;

    @SerializedName("name")
    private String cityName;


    public CurrentWeatherMain getMain() {
        return main;
    }

    public CurrentWeatherWind getWind() {
        return wind;
    }

    public String getCityName() {
        return cityName;
    }
}