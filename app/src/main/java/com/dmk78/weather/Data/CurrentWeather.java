package com.dmk78.weather.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentWeather {

    private Main main;
    private List<Weather> weather;
    private Wind wind;
    private Sys sys;
    private Integer dt;

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

    public Sys getSys() {
        return sys;
    }

    public Integer getDt() {
        return dt;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}