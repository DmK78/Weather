package com.dmk78.weather.weather;

import android.app.Application;

import android.graphics.Movie;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.dmk78.weather.R;
import com.dmk78.weather.databinding.ActivityWeatherBinding;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.MyLocation;
import com.dmk78.weather.utils.MyLocationImpl;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WeatherViewModel extends AndroidViewModel implements Observable {
    private PlacePreferencesImpl placePreferences;
    private NetworkService networkService;
    private LiveData<CurrentWeather> currentWeatherLiveData;
    private LiveData<FiveDaysWeather> fiveDaysWeatherLiveData;
    private Place currentPlace;
    private CurrentWeatherObserver currentWeatherObserver;
    private FiveDaysWeatherObserver fiveDaysWeatherObserver;
    private MyLocationImpl locationService;
    private String cityName;

    //private MyLocation locationService;


    public WeatherViewModel(@NonNull Application application) {
        super(application);
        currentWeatherObserver = new CurrentWeatherObserver();
        locationService = new MyLocationImpl(application);
        //locationService.checkLocPermissions();

        fiveDaysWeatherObserver = new FiveDaysWeatherObserver();
        networkService = new NetworkService();
        placePreferences = new PlacePreferencesImpl(getApplication().getApplicationContext());
        updateWeather(placePreferences.loadPlace());


    }

    public void onClickGeo(View view){
        Location location = locationService.getLocation();
        if(location!=null) {
            Place place = Place.builder().setLatLng(new LatLng(location.getLatitude(), location.getLongitude())).build();
            updateWeather(place);
        }

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




    public LiveData<CurrentWeather> getCurWeather(Place place) {
        return networkService.getCurWeather(place);


    }


    public LiveData<FiveDaysWeather> getFiveDaysWeather(Place place) {
        return networkService.getFiveDaysWeather(place);
    }

    public void updateWeather(Place place) {
        placePreferences.savePlace(place);

        currentWeatherLiveData = networkService.getCurWeather(place);
        fiveDaysWeatherLiveData = networkService.getFiveDaysWeather(place);


    }

    @Bindable
    public String getCityName() {
        return currentWeatherLiveData.getValue().getCityName();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }


    class CurrentWeatherObserver implements Observer<CurrentWeather> {

        @Override
        public void onChanged(CurrentWeather currentWeather) {

        }
    }

    class FiveDaysWeatherObserver implements Observer<FiveDaysWeather> {

        @Override
        public void onChanged(FiveDaysWeather fiveDaysWeather) {

        }
    }


}
