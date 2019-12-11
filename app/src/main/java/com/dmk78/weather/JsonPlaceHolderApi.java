package com.dmk78.weather;

import com.dmk78.weather.Data.CurrentWeather;
import com.dmk78.weather.Data.FiveDaysWeather;
import com.dmk78.weather.Data.Day;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("weather")
    Call<CurrentWeather> getCurrentWeather(

            @Query("q") String cityName,

            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    @GET("forecast")
    Call<FiveDaysWeather> getFiveDaysWeather(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    @GET("forecast/list")
    Call<List<Day>> getDaysWeather(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

}