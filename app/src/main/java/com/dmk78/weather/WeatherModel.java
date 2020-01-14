package com.dmk78.weather;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.Constants;
import com.dmk78.weather.utils.PlacePreferences;
import com.google.android.libraries.places.api.model.Place;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherModel implements WeatherContract.WeatherModel {
    private NetworkService networkService;
    private ModelInterface callback;
    private Context context;
    private static WeatherModel instance;
    private PlacePreferences placePreferences;

    private WeatherModel(NetworkService networkService, ModelInterface callback) {
        this.networkService = networkService;
        this.callback = callback;

    }

    public static WeatherModel getInstance(ModelInterface modelInterface) {
        if (instance == null) {
            instance = new WeatherModel(NetworkService.getInstance(), modelInterface);

        }
        return instance;
    }

    @Override
    public void getCurWeather(Place place) {

        networkService.getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude,
                place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    CurrentWeather result = response.body();
                    if (!TextUtils.isEmpty(place.getName())) {
                        result.setCityName(place.getName());

                    }
                    result.setLatLng(place.getLatLng());
                    callback.getCurWeather(result);
                    getFiveFaysWeather(place);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
            }
        });

    }

    @Override
    public void getFiveFaysWeather(Place place) {

        networkService.getJSONApi().getFiveDaysWeather(place.getLatLng().latitude,
                place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang)
                .enqueue(new Callback<FiveDaysWeather>() {
                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        if (response.isSuccessful()) {
                            FiveDaysWeather result = response.body();
                            result.calculateDateTime();
                            callback.getFiveDaysWeather(result);

                        } else {
                            Log.i("MyError", "" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                        Log.i("MyError", "" + t.getMessage());
                    }
                });


    }

    public interface ModelInterface {
        public void getCurWeather(CurrentWeather currentWeather);

        public void getFiveDaysWeather(FiveDaysWeather fiveDaysWeather);
    }


}
