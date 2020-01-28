package com.dmk78.weather;

import android.app.Application;


import com.dmk78.weather.di.DaggerWeatherPresenterComponent;
import com.dmk78.weather.di.WeatherPresenterComponent;

import dagger.internal.DaggerCollections;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class App extends Application {
    private static WeatherPresenterComponent weatherPresenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        weatherPresenterComponent = DaggerWeatherPresenterComponent.create();
    }





    public static WeatherPresenterComponent getWeatherPresenter() {
        return weatherPresenterComponent;
    }
}
