package com.dmk78.weather.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class FiveDaysWeather {


    @SerializedName("list")
    private List<Day> days = null;
    private City city;

    public List<Day> getDays() {
        return days;
    }

    public City getCity() {
        return city;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiveDaysWeather that = (FiveDaysWeather) o;
        return Objects.equals(days, that.days) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(days, city);
    }


    public void calculateDateTime() {
        for (Day day : days) {
            day.setTime(day.getDt_txt().substring(10, day.getDt_txt().length()));
            //day.setDate(day.getDt_txt().substring(0,10));
            day.setDate(day.getDt_txt().substring(8, 10) + "-" + day.getDt_txt().substring(5, 7) + "-" + day.getDt_txt().substring(0, 4));
        }
    }
}