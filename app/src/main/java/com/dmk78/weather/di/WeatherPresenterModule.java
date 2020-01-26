package com.dmk78.weather.di;

import androidx.fragment.app.Fragment;


import com.dmk78.weather.weather.WeatherContract;
import com.dmk78.weather.weather.WeatherFragment;
import com.dmk78.weather.weather.WeatherPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WeatherPresenterModule {

    @Singleton
    @Provides
    public WeatherPresenter providesWeatherPresenter() {
        return new WeatherPresenter();
    }
}
