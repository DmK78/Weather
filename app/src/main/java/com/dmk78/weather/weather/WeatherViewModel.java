package com.dmk78.weather.weather;

import android.app.Application;

import android.location.Location;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.dmk78.weather.App;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.MyLocationImpl;

import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import javax.inject.Inject;


public class WeatherViewModel extends AndroidViewModel {
    @Inject
    PlacePreferencesImpl placePreferences;
    @Inject
    NetworkService networkService;
    @Inject
    MyLocationImpl locationService;
    private final MutableLiveData<CurrentWeather> currentWeatherLiveData = new MutableLiveData<>();
    private final MutableLiveData<FiveDaysWeather> fiveDaysWeatherLiveData = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        App.getComponent().inject(this);
        updateWeather(placePreferences.loadPlace());
    }

    public void onClickGeo() {
        Location location = locationService.getLocation();
        if (location != null) {
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
        return currentWeatherLiveData;
    }


    public LiveData<FiveDaysWeather> getFiveDaysWeather(Place place) {
        return fiveDaysWeatherLiveData;
    }

    public void updateWeather(Place place) {
        placePreferences.savePlace(place);

        networkService.getCurWeather(place, currentWeatherLiveData);
        networkService.getFiveDaysWeather(place, fiveDaysWeatherLiveData);


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
