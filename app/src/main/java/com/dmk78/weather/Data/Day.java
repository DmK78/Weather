package com.dmk78.weather.Data;

import java.util.List;

public class Day {

/*    public Day() {
        String tmp = dt_txt;
        dt_txt=tmp.substring(9, 10) + "." + tmp.substring(6, 7) + "." + tmp.substring(0, 4);
    }*/

    private Main main;
    private List<Weather> weather;
    private Wind wind;
    private String dt_txt;

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
}
