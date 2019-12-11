package com.dmk78.weather.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentWeather {

    private Main main;
    private List<Weather> weather;
    private Wind wind;

    @SerializedName("name")
    private String cityName;

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public String getCityName() {
        return cityName;
    }
}