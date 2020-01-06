package com.dmk78.weather;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherUpdater {
    private static String key = "8f99535cdea446be868e707ba8062fc0";
    private static String units = "metric";
    private static String lang = "ru";




    public static CurrentWeather updateCurrentWeatherByCityName(final String city) {
    NetworkService networkService = NetworkService.getInstance();
        final CurrentWeather[] result = {null};
        networkService.getJSONApi().getCurrentWeatherByCity(city, key, units, lang)
                .enqueue(new Callback<CurrentWeather>() {
                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        if (response.isSuccessful()) {
                            result[0] = response.body();


                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {

                    }
                });
        return result[0];
    }
}
