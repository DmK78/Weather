package com.dmk78.weather.di;

import android.app.Application;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;

import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.PlacePreferences;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.dmk78.weather.weather.WeatherFragment;
import com.dmk78.weather.weather.WeatherViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkServiceModule.class, PlacePreferencesModule.class, LocationModule.class})
public interface AppComponent {
    void inject(WeatherViewModel weatherViewModel);




}
