package com.dmk78.weather;

import android.util.Log;
import android.widget.Toast;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;
import com.google.android.libraries.places.api.model.Place;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherModel implements WeatherContract.WeatherModel {
    NetworkService networkService = NetworkService.getInstance();

    @Override
    public CurrentWeather getCurWeather(Place place) {
        final CurrentWeather[] result = {null};
        networkService.getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude, place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    result[0] = response.body();
                    if (place.getName().equals("")) {
                    } else {
                        result[0].setCityName(place.getName());
                    }
                    getFiveFaysWeather(place);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
            }
        });
        return result[0];
    }

    @Override
    public FiveDaysWeather getFiveFaysWeather(Place place) {
        final FiveDaysWeather[] result = {null};
        networkService.getJSONApi().getFiveDaysWeather(place.getLatLng().latitude,
                place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang)
                .enqueue(new Callback<FiveDaysWeather>() {
                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        if (response.isSuccessful()) {
                            result[0] = response.body();
                            result[0].calculateDateTime();

                        } else {
                            Log.i("MyError", "" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                        Log.i("MyError", "" + t.getMessage());
                    }
                });


        return null;
    }


}
