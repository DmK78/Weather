package com.dmk78.weather.Data;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FiveDaysWeather {

    @SerializedName("list")
    private List<WeatherDay> list = null;

    private City city;

    public List<WeatherDay> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    public void setList(List<WeatherDay> list) {
        this.list = list;
    }

    public void setCity(City city) {
        this.city = city;
    }
}