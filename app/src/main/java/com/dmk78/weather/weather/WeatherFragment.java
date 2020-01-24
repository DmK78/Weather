package com.dmk78.weather.weather;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dmk78.weather.R;
import com.dmk78.weather.adapters.DaysAdapter;
import com.dmk78.weather.adapters.HoursAdapter;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.utils.BgColorSetter;

import com.dmk78.weather.utils.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeatherFragment extends Fragment implements WeatherContract.WeatherView {
    private ConstraintLayout bg;
    private WeatherContract.WeatherPresenter presenter;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        presenter = new WeatherPresenter(this);
        bindAllViews(view);
        imageViewGetCurrentLocation.setOnClickListener(v -> presenter.onGetWeatherByGeoClicked());
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                presenter.onGetWeatherByPlaceClicked(place);

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
            presenter.onGetWeatherByPlaceClicked(presenter.getLastSavedPlace());
            swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Place place = presenter.getLastSavedPlace();
        if (TextUtils.isEmpty(place.getName())) {
            presenter.onGetWeatherByGeoClicked();
        } else presenter.onGetWeatherByPlaceClicked(place);
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


}
