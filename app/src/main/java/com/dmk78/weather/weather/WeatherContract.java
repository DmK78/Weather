package com.dmk78.weather.weather;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.model.FiveDaysWeather;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public interface WeatherContract {
    interface WeatherModel {
        void getCurWeather(Place place);

        void getFiveFaysWeather(Place place);
    }

    interface WeatherPresenter {
        void getWeatherByPlace(Place place);

        void onGetWeatherByGeoClicked();

        void onGetWeatherByPlaceClicked(Place place);

        Place getLastavedPlace();

    }

    interface WeatherView {
        void renderCurrentWeather(CurrentWeather currentWeather);

        void showProgress();

        void hideProgress();

        void fillDaysAdapter(List<Day> days);

        void fillHoursAdapter(List<Day> hours);
    }
}
