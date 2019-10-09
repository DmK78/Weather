package com.dmk78.weather.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherDay {

    private CurrentWeatherMain main;
    private List<Weather> weather;
    private CurrentWeatherWind wind;

    public CurrentWeatherMain getMain() {
        return main;
    }

    public void setMain(CurrentWeatherMain main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public CurrentWeatherWind getWind() {
        return wind;
    }

    public void setWind(CurrentWeatherWind wind) {
        this.wind = wind;
    }
}
