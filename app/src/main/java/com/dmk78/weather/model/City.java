package com.dmk78.weather.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(id, city.id) &&
                Objects.equals(name, city.name) &&
                Objects.equals(coord, city.coord) &&
                Objects.equals(country, city.country) &&
                Objects.equals(population, city.population) &&
                Objects.equals(timeZone, city.timeZone) &&
                Objects.equals(sunrise, city.sunrise) &&
                Objects.equals(sunset, city.sunset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coord, country, population, timeZone, sunrise, sunset);
    }
}
