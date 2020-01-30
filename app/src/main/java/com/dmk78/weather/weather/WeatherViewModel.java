package com.dmk78.weather.weather;

import android.app.Application;

import android.graphics.Movie;
import android.os.AsyncTask;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dmk78.weather.R;
import com.dmk78.weather.databinding.ActivityWeatherBinding;
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

public class WeatherViewModel extends AndroidViewModel  {
    private PlacePreferencesImpl placePreferences;
    private NetworkService networkService;
    private LiveData<CurrentWeather> currentWeatherLiveData;
    private LiveData<FiveDaysWeather> fiveDaysWeatherLiveData;
    private Place currentPlace;

    //private MyLocation locationService;


    public WeatherViewModel(@NonNull Application application) {
        super(application);

        networkService = new NetworkService();
        placePreferences = new PlacePreferencesImpl(getApplication().getApplicationContext());
        currentPlace = placePreferences.loadPlace();
        currentWeatherLiveData = networkService.getCurWeather(currentPlace);
        fiveDaysWeatherLiveData = networkService.getFiveDaysWeather(currentPlace);


    }
/*public void updateData(){
    currentPlace = placePreferences.loadPlace();
    currentWeatherLiveData = networkService.getCurWeather(currentPlace);
    fiveDaysWeatherLiveData = networkService.getFiveDaysWeather(currentPlace);
}*/

    public Place getSavedPlace() {
        return placePreferences.loadPlace();
    }

    public LiveData<CurrentWeather> getCurrentWeatherLiveDataResponse() {
        return currentWeatherLiveData;
    }

    public LiveData<FiveDaysWeather> getFiveDaysWeatherLiveDataResponse() {
        return fiveDaysWeatherLiveData;
    }

    @NonNull
    @Override
    public <T extends Application> T getApplication() {
        return super.getApplication();
    }


    public LiveData<CurrentWeather> getCurWeather(Place place) {
        return networkService.getCurWeather(place);


    }


    public void getFiveDaysWeather(Place place) {
        fiveDaysWeatherLiveData= networkService.getFiveDaysWeather(place);
    }

    public void updateWeather(Place place) {

        currentWeatherLiveData = networkService.getCurWeather(place);
        fiveDaysWeatherLiveData = networkService.getFiveDaysWeather(place);


    }
}
