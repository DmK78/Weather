package com.dmk78.weather.weather;

import androidx.lifecycle.LiveData;

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
    interface ViewModel {
        LiveData<CurrentWeather> getCurWeather(Place place);

        LiveData<FiveDaysWeather> getFiveDaysWeather(Place place);
    }

    interface WeatherPresenter {

        void onGetWeatherByGeoClicked();

        void onGetWeatherByPlaceClicked(Place place);

        Place getLastSavedPlace();

    }

    interface WeatherView {
        void renderCurrentWeather(LiveData<CurrentWeather> weatherLiveData);

        void showProgress();

        void hideProgress();

        void showToast(String message);
    }
}
