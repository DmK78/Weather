package com.dmk78.weather.weather;

import android.location.Location;

import androidx.fragment.app.Fragment;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.MyLocationInterface;
import com.dmk78.weather.utils.MyLocationService;
import com.dmk78.weather.utils.PlaceInterface;
import com.dmk78.weather.utils.PlacePreferences;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;

//@Module
public class WeatherPresenter implements WeatherContract.WeatherPresenter, WeatherModel.ModelInterface {
    private WeatherContract.WeatherView view;
    private WeatherContract.WeatherModel model;
    private MyLocationInterface locationService;
    private PlaceInterface placePreferences;


    public WeatherPresenter(Fragment view) {
        this.view = (WeatherFragment) view;
        this.model = WeatherModel.getInstance(this);
        locationService = new MyLocationService(view.getContext(), view);
        placePreferences = new PlacePreferences(view.getContext());
    }

    public WeatherPresenter() {
    }

    public void bindFragmentView(Fragment view){
        this.view = (WeatherContract.WeatherView) view;
        this.model = WeatherModel.getInstance(this);
        locationService = new MyLocationService(view.getContext(), view);
        placePreferences = new PlacePreferences(view.getContext());

    }

    @Override
    public void onGetWeatherByGeoClicked() {


        Location location = locationService.getLocation();

        if (location != null) {
            Place place = Place.builder().setLatLng(new LatLng(location.getLatitude(), location.getLongitude())).build();
            onGetWeatherByPlaceClicked(place);
        }
    }

    @Override
    public void onGetWeatherByPlaceClicked(Place place) {
        placePreferences.savePlace(place);
        view.showProgress();

        model.getCurWeather(place);


    }

    @Override
    public Place getLastSavedPlace() {

        return placePreferences.loadPlace();
    }

    public void fillHoursList(List<Day> days) {
        List<Day> result = new ArrayList<>();

        result.addAll(getWeatherFor24Hours(days));
        view.fillHoursAdapter(result);

    }

    private List<Day> getWeatherFor24Hours(List<Day> days) {
        List<Day> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            result.add(days.get(i));
        }
        return result;
    }

    public void fillDaysList(List<Day> days) {
        List<Day> result = new ArrayList<>();
        result.addAll(convertToShort(days));
        view.fillDaysAdapter(result);
    }

    private List<Day> convertToShort(List<Day> days) {
        String baseData = days.get(0).getDate();
        float minTemp = +100;
        float maxTemp = -100;
        List<Day> result = new ArrayList<>();

        for (int i = 0; i < days.size(); i++) {
            Day day = days.get(i);
            String tmp = day.getDate();
            day.setDate(tmp);
            if (day.getDate().equals(baseData)) {
                if (day.getMain().getMinTemp() < minTemp) {
                    minTemp = day.getMain().getMinTemp();
                }
                if (day.getMain().getMaxTemp() > maxTemp) {
                    maxTemp = day.getMain().getMaxTemp();
                }

                continue;
            } else {
                Day dayPrev = days.get(i - 1);
                baseData = day.getDate();
                dayPrev.getMain().setMaxTemp(maxTemp);
                dayPrev.getMain().setMinTemp(minTemp);
                maxTemp = -100;
                minTemp = +100;
                result.add(dayPrev);
            }

        }
        return result;

    }


    @Override
    public void getCurWeather(CurrentWeather currentWeather) {
        Place place = Place.builder().setName(currentWeather.getCityName()).setLatLng(currentWeather.getLatLng()).build();
        placePreferences.savePlace(place);
        view.renderCurrentWeather(currentWeather);

    }

    @Override
    public void getFiveDaysWeather(FiveDaysWeather fiveDaysWeather) {
        List<Day> dayList = fiveDaysWeather.getDays();
        fillHoursList(dayList);
        fillDaysList(dayList);
        view.hideProgress();

    }


}

