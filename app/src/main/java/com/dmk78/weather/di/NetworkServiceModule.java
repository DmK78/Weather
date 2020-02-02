package com.dmk78.weather.di;

import androidx.fragment.app.Fragment;

import com.dmk78.weather.network.NetworkService;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Module
public class NetworkServiceModule {
    @Singleton
    @Provides
    public NetworkService providesNetworkService() {
        return new NetworkService();
    }
}
