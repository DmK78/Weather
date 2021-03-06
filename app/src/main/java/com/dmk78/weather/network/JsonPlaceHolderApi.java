package com.dmk78.weather.network;

import androidx.lifecycle.LiveData;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public interface JsonPlaceHolderApi {
    @GET("forecast")
    Call<FiveDaysWeather> getFiveDaysWeather(
            @Query("lat") double latittude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    @GET("weather")
    Call<CurrentWeather> getCurrentWeatherByCoord(
            @Query("lat") double latittude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    @GET("weather")
    Single<CurrentWeather> getCurrentWeatherByCoordRX(
            @Query("lat") double latittude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    @GET("forecast")
    Single<FiveDaysWeather> getFiveDaysWeatherRX(
            @Query("lat") double latittude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );



}
