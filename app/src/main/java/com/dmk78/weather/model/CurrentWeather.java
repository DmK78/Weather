package com.dmk78.weather.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class CurrentWeather {

    private Main main;
    private List<Weather> weather;
    private Wind wind;
    private Sys sys;
    private Integer dt;
    private LatLng latLng;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentWeather that = (CurrentWeather) o;
        return Objects.equals(main, that.main) &&
                Objects.equals(weather, that.weather) &&
                Objects.equals(wind, that.wind) &&
                Objects.equals(sys, that.sys) &&
                Objects.equals(dt, that.dt) &&
                Objects.equals(cityName, that.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(main, weather, wind, sys, dt, cityName);
    }
}