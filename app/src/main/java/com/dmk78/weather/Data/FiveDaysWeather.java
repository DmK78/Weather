package com.dmk78.weather.Data;



import com.google.gson.annotations.SerializedName;

import java.util.List;

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
}