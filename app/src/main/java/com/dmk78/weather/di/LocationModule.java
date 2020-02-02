package com.dmk78.weather.di;

import android.content.Context;

import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.MyLocationImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {
    private final Context context;

    public LocationModule(Context context) {
        this.context = context;
    }


    @Singleton
    @Provides
    public MyLocationImpl providesLocationService() {
        return new MyLocationImpl(context);
    }
}
