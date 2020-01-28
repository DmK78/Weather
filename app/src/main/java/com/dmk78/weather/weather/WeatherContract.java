package com.dmk78.weather.weather;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.model.FiveDaysWeather;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public interface WeatherContract {
    interface WeatherModel {
        void getCurWeather(Place place);

        void getFiveDaysWeather(Place place);
    }

    interface WeatherPresenter {

        void onGetWeatherByGeoClicked();

        void onGetWeatherByPlaceClicked(Place place);

        Place getLastSavedPlace();

    }

    interface WeatherView {
        void renderCurrentWeather(CurrentWeather currentWeather);

        void showProgress();

        void hideProgress();

        void fillDaysAdapter(List<Day> days);

        void fillHoursAdapter(List<Day> hours);

        void showToast(String message);
    }
}
