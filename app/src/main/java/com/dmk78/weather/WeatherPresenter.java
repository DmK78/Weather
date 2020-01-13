package com.dmk78.weather;

import com.dmk78.weather.network.NetworkService;
import com.google.android.libraries.places.api.model.Place;

public class WeatherPresenter implements WeatherContract.WeatherPresenter {
    private WeatherFragment view;
    private WeatherModel model;

    public WeatherPresenter(WeatherFragment view, WeatherModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void placeWasSelected(Place place) {



    }

    @Override
    public void geoLocWasClicked() {

    }
}
