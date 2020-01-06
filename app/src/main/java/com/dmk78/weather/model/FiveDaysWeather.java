package com.dmk78.weather.model;



import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class FiveDaysWeather {

    @SerializedName("list")
    private List<Day> list = null;
    private City city;

    public List<Day> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    public void setList(List<Day> list) {
        this.list = list;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiveDaysWeather that = (FiveDaysWeather) o;
        return Objects.equals(list, that.list) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list, city);
    }
}