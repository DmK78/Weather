package com.dmk78.weather.Data;

import com.google.gson.annotations.SerializedName;

public class WeatherDay {

    private CurrentWeatherMain main;
    private Weather weather;
    private CurrentWeatherWind wind;

    public CurrentWeatherMain getMain() {
        return main;
    }

    public void setMain(CurrentWeatherMain main) {
        this.main = main;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public CurrentWeatherWind getWind() {
        return wind;
    }

    public void setWind(CurrentWeatherWind wind) {
        this.wind = wind;
    }
}
