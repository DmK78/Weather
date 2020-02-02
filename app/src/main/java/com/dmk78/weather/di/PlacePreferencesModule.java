package com.dmk78.weather.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dmk78.weather.utils.Constants;
import com.dmk78.weather.utils.PlacePreferences;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.google.android.libraries.places.api.model.Place;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlacePreferencesModule {
     private final Context context;

    public PlacePreferencesModule(Context context) {
        this.context = context;
    }



    @Singleton
    @Provides
    public PlacePreferences providesPlacePreferences(Context context) {
        return new PlacePreferencesImpl(context);


    }

    @Provides
    SharedPreferences provideSharedPreferences() {
        return context.getSharedPreferences(Constants.MY_SETTINGS,Context.MODE_PRIVATE);
    }



}
