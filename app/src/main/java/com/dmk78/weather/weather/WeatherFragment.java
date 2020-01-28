package com.dmk78.weather.weather;

import android.location.Location;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dmk78.weather.R;
import com.dmk78.weather.adapters.DaysAdapter;
import com.dmk78.weather.adapters.HoursAdapter;
import com.dmk78.weather.App;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.BgColorSetter;

import com.dmk78.weather.utils.MyLocation;
import com.dmk78.weather.utils.MyLocationImpl;
import com.dmk78.weather.utils.PlacePreferences;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.dmk78.weather.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;


/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class WeatherFragment extends Fragment implements WeatherContract.WeatherView {
    private ConstraintLayout bg;
    //@Inject
    //WeatherContract.WeatherPresenter presenter;
    //WeatherPresenter presenter;
    private RecyclerView recyclerDays;
    private DaysAdapter adapterDays;
    private RecyclerView recyclerHours;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HoursAdapter adapterHours;
    private List<Day> days = new ArrayList<>();
    private List<Day> hours = new ArrayList<>();
    private ImageView imageViewCurrentTemp, imageViewWind, imageViewGetCurrentLocation;
    private TextView textViewTemp, textViewMinTemp, textViewPressure, textViewWeatherDesc,
            textViewWindSpeed, textViewHumidity, textViewCity;
    private LiveData<CurrentWeather> currentWeatherLiveData;
    private LiveData<FiveDaysWeather> fiveDaysWeatherLiveData;
    private WeatherViewModel viewModel;
private PlacePreferencesImpl placePreferences;
    private MyLocation locationService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        //presenter = new WeatherPresenter(this);
       // App.getWeatherPresenter().injectTo(this);
       // presenter.bindFragmentView(this);
        locationService = new MyLocationImpl(view.getContext(), this);
        bindAllViews(view);
        placePreferences = new PlacePreferencesImpl(getContext());
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        getData();
        imageViewGetCurrentLocation.setOnClickListener(v -> {
            Location location = locationService.getLocation();
            if(location!=null){
                placePreferences.savePlace(Place.builder().setLatLng(new LatLng(location.getLatitude(),location.getLongitude())).build());
            }else {
                showToast("Can`t get location");
            }
            getData();
        });
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placePreferences.savePlace(place);
                getData();

            }

            @Override
            public void onError(Status status) {
                Log.i(WeatherFragment.class.getName(), "An error occurred: " + status);
            }
        });
        this.recyclerDays.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerHours.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterHours = new HoursAdapter(this.hours, getContext());
        recyclerHours.setAdapter(adapterHours);
        adapterDays = new DaysAdapter(this.days, getContext());
        recyclerDays.setAdapter(adapterDays);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }

    private void getData() {
        currentWeatherLiveData=viewModel.getCurrentWeatherLiveDataResponse();
        currentWeatherLiveData.observe(this, new Observer<CurrentWeather>() {
            @Override
            public void onChanged(CurrentWeather currentWeather) {
                renderCurrentWeather(currentWeather);
            }
        });
        fiveDaysWeatherLiveData=viewModel.getFiveDaysWeatherLiveDataResponse();
        fiveDaysWeatherLiveData.observe(this, new Observer<FiveDaysWeather>() {
            @Override
            public void onChanged(FiveDaysWeather fiveDaysWeather) {

                List<Day> dayList = fiveDaysWeather.getDays();
                fillHoursList(dayList);
                fillDaysList(dayList);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Place place = viewModel.getSavedPlace();
        if (TextUtils.isEmpty(place.getName())) {
            Location location = locationService.getLocation();
            if(location==null){
                showToast("Can`t get location");
            } else {
                place = Place.builder().setLatLng(new LatLng(location.getLatitude(),location.getLongitude())).build();
                placePreferences.savePlace(place);
            }

            //currentWeatherLiveData = viewModel.getCurrentWeatherLiveDataResponse();

        }
            currentWeatherLiveData = viewModel.getCurrentWeatherLiveDataResponse();

        currentWeatherLiveData.observe(this, new Observer<CurrentWeather>() {
            @Override
            public void onChanged(CurrentWeather currentWeather) {
                currentWeather = currentWeatherLiveData.getValue();
                renderCurrentWeather(currentWeather);
            }
        });



        /*Place place = .getLastSavedPlace();
        if (TextUtils.isEmpty(place.getName())) {
            presenter.onGetWeatherByGeoClicked();
        } else presenter.onGetWeatherByPlaceClicked(place);*/
    }

    private void bindAllViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        imageViewCurrentTemp = view.findViewById(R.id.imageViewCurrent);
        textViewCity = view.findViewById(R.id.textViewCurrentCity);
        textViewTemp = view.findViewById(R.id.textViewCurrentTemp);
        textViewMinTemp = view.findViewById(R.id.textViewCurrentTempMin);
        textViewPressure = view.findViewById(R.id.textViewPressure);
        textViewWeatherDesc = view.findViewById(R.id.textViewWeatherDesc);
        textViewWindSpeed = view.findViewById(R.id.textViewWindSpeed);
        textViewHumidity = view.findViewById(R.id.textViewHumidity);
        imageViewGetCurrentLocation = view.findViewById(R.id.imageViewGetLocation);
        imageViewWind = view.findViewById(R.id.imageViewWind);
        bg = view.findViewById(R.id.bgMain);
        recyclerDays = view.findViewById(R.id.resyclerDays);
        recyclerHours = view.findViewById(R.id.recyclerHours);
    }

    @Override
    public void renderCurrentWeather(CurrentWeather weather) {
        Date date = new Date();
        date.setTime((long) weather.getDt() * 1000);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy EEE");
        textViewCity.setText(formatForDateNow.format(date) + "\n" + weather.getCityName() + ", " + weather.getSys().getCountry());
        textViewTemp.setText(String.valueOf(Math.round(weather.getMain().getTemp())) + " C");
        textViewMinTemp.setText(Math.round(weather.getMain().getMinTemp()) + " C");
        textViewPressure.setText(Math.round(weather.getMain().getPressure()) + " мм");
        textViewWeatherDesc.setText(weather.getWeather().get(0).getDescription());
        textViewWindSpeed.setText(weather.getWind().getSpeed() + " m/s");
        textViewHumidity.setText(getString(R.string.humidity) + " " + String.valueOf(Math.round(weather.getMain().getHumidity())) + " %");
        //imageViewCurrentTemp.setImageResource(Utils.convertIconSourceToId(weather.getWeather().get(0).getIcon()));
        imageViewCurrentTemp.setImageResource(Utils.getStringIdentifier(getContext(), "i" + weather.getWeather().get(0).getIcon(), "drawable"));
        imageViewWind.animate().rotation(weather.getWind().getDegree()).setDuration(1000).start();
        bg.setBackgroundResource(BgColorSetter.set(weather.getMain().getMaxTemp()));
    }

    @Override
    public void showProgress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void fillDaysAdapter(List<Day> days) {
        adapterDays.setData(days);
    }

    @Override
    public void fillHoursAdapter(List<Day> hours) {
        adapterHours.setData(hours);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void fillHoursList(List<Day> days) {
        List<Day> result = new ArrayList<>();

        result.addAll(getWeatherFor24Hours(days));
        fillHoursAdapter(result);

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
        fillDaysAdapter(result);
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


}
