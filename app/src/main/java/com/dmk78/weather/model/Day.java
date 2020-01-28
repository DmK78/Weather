package com.dmk78.weather.model;

import java.util.List;
import java.util.Objects;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class Day {


    private Main main;
    private List<Weather> weather;
    private Wind wind;
    private String dt_txt;
    private String date;
    private String time;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return Objects.equals(main, day.main) &&
                Objects.equals(weather, day.weather) &&
                Objects.equals(wind, day.wind) &&
                Objects.equals(dt_txt, day.dt_txt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(main, weather, wind, dt_txt);
    }
}
