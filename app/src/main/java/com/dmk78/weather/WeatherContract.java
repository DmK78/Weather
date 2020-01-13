package com.dmk78.weather;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.google.android.libraries.places.api.model.Place;

public interface WeatherContract {
    interface WeatherModel{
        public CurrentWeather getCurWeather(Place place);
        public FiveDaysWeather getFiveFaysWeather(Place place);
    }
    interface WeatherPresenter{
        public void placeWasSelected(Place place);
        public void geoLocWasClicked();
    }
}
