package com.dmk78.weather;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.google.android.libraries.places.api.model.Place;

public interface WeatherContract {
    interface WeatherModel{
        public void getCurWeather(Place place);
        public void getFiveFaysWeather(Place place);
    }
    interface WeatherPresenter{
        public void getWeatherByPlace(Place place);

    }
}
