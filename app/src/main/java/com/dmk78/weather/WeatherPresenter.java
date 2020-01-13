package com.dmk78.weather;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.PlacePreferences;
import com.google.android.libraries.places.api.model.Place;
import java.util.ArrayList;
import java.util.List;

public class WeatherPresenter implements WeatherContract.WeatherPresenter, WeatherModel.ModelInterface {
    private WeatherFragment view;
    private WeatherModel model;

    PlacePreferences placePreferences;

    public WeatherPresenter(WeatherFragment view) {
        this.view = view;
        this.model = WeatherModel.getInstance(this);

        placePreferences = new PlacePreferences(view.getContext());


    }



    @Override
    public void getWeatherByPlace(Place place) {
        view.showProgress();
        model.getCurWeather(place);
    }

    public void fillHoursList(List<Day> days) {
        List<Day> result = new ArrayList<>();

        result.addAll(getWeatherFor24Hours(days));
        view.setHoursAdapterData(result);

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
        view.setDaysAdapterData(result);
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
