package com.dmk78.weather;

import android.app.Application;


import com.dmk78.weather.di.AppComponent;
import com.dmk78.weather.di.DaggerAppComponent;
import com.dmk78.weather.di.LocationModule;
import com.dmk78.weather.di.NetworkServiceModule;
import com.dmk78.weather.di.PlacePreferencesModule;


public class App extends Application {
    private static AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().
                networkServiceModule(new NetworkServiceModule()).
                placePreferencesModule(new PlacePreferencesModule(getApplicationContext())).
                locationModule(new LocationModule(getApplicationContext())).
                build();


    }

    public static AppComponent getComponent() {
        return appComponent;
    }

}
