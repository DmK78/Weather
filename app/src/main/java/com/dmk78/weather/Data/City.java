package com.dmk78.weather.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {


    private Integer id;

    private String name;

    private Coord coord;

    private String country;

    private Integer population;
    @SerializedName("timezone")

    private Integer timeZone;

    private Integer sunrise;

    private Integer sunset;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public String getCountry() {
        return country;
    }

    public Integer getPopulation() {
        return population;
    }

    public Integer getTimeZone() {
        return timeZone;
    }

    public Integer getSunrise() {
        return sunrise;
    }

    public Integer getSunset() {
        return sunset;
    }
}
