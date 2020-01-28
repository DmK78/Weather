package com.dmk78.weather.weather;

import android.app.Application;

import android.graphics.Movie;
import android.os.AsyncTask;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.MyLocation;
import com.dmk78.weather.utils.MyLocationImpl;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WeatherViewModel extends AndroidViewModel {
    private PlacePreferencesImpl placePreferences;
    private NetworkService networkService;
    private LiveData<CurrentWeather> currentWeatherLiveData;
    private LiveData<FiveDaysWeather> fiveDaysWeatherLiveData;
    private Place currentPlace;
    //private MyLocation locationService;



    public WeatherViewModel(@NonNull Application application) {
        super(application);

      //  locationService = new MyLocationImpl(getApplication().getApplicationContext(), fragment);
        networkService = new NetworkService(null);
        placePreferences = new PlacePreferencesImpl(getApplication().getApplicationContext());
        currentPlace = placePreferences.loadPlace();
        /*if (!TextUtils.isEmpty(currentPlace.getName())) {*/
            currentWeatherLiveData = networkService.getCurWeather(currentPlace);
            fiveDaysWeatherLiveData = networkService.getFiveDaysWeather(currentPlace);
        /*} else {
            LatLng latLng = new LatLng(locationService.getLocation().getLatitude(), locationService.getLocation().getLongitude());
            currentPlace = Place.builder().setLatLng(latLng).build();
            currentWeatherLiveData = networkService.getCurWeather(currentPlace);
            fiveDaysWeatherLiveData = networkService.getFiveDaysWeather(currentPlace);

        }*/

    }


    public Place getSavedPlace() {
        return placePreferences.loadPlace();
    }

    public LiveData<CurrentWeather> getCurrentWeatherLiveDataResponse() {
        return currentWeatherLiveData;
    }

    public LiveData<FiveDaysWeather> getFiveDaysWeatherLiveDataResponse() {
        return fiveDaysWeatherLiveData;
    }


}
