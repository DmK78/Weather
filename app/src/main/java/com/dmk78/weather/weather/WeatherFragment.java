package com.dmk78.weather.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dmk78.weather.R;
import com.dmk78.weather.adapters.DaysAdapter;
import com.dmk78.weather.adapters.HoursAdapter;
import com.dmk78.weather.databinding.ActivityWeatherBinding;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.utils.BgColorSetter;

import com.dmk78.weather.utils.MyLocationImpl;
import com.dmk78.weather.utils.PlacePreferences;
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

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class WeatherFragment extends Fragment {
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private DaysAdapter adapterDays;
    private HoursAdapter adapterHours;
    private List<Day> days = new ArrayList<>();
    private List<Day> hours = new ArrayList<>();
    private WeatherViewModel viewModel;
    private ActivityWeatherBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        checkLocPermissions();
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        binding = ActivityWeatherBinding.inflate(inflater);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewmodel(viewModel);
        setupAdapters();

        binding.swipe.setOnRefreshListener(() -> {
            updateUi(viewModel.getSavedPlace());
        });
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                updateUi(place);
            }

            @Override
            public void onError(Status status) {
                Log.i(WeatherFragment.class.getName(), "An error occurred: " + status);
            }
        });
        binding.imageViewGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onClickGeo();
            }
        });
        viewModel.getCurrentWeatherLiveDataResponse().observe(this, weather -> {
            if (weather != null) {
                Date date = new Date();
                date.setTime((long) weather.getDt() * 1000);
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy EEE");
                binding.textViewCurrentCity.setText(formatForDateNow.format(date) + "\n" + weather.getCityName() + ", " + weather.getSys().getCountry());
                binding.textViewCurrentTemp.setText(String.valueOf(Math.round(weather.getMain().getTemp())) + " C");
                binding.textViewCurrentTempMin.setText(Math.round(weather.getMain().getMinTemp()) + " C");
                binding.textViewPressure.setText(Math.round(weather.getMain().getPressure()) + " мм");
                binding.textViewWeatherDesc.setText(weather.getWeather().get(0).getDescription());
                binding.textViewWindSpeed.setText(weather.getWind().getSpeed() + " m/s");
                binding.textViewHumidity.setText(getString(R.string.humidity) + " " + String.valueOf(Math.round(weather.getMain().getHumidity())) + " %");
                binding.imageViewCurrent.setImageResource(Utils.getStringIdentifier(getContext(), "i" + weather.getWeather().get(0).getIcon(), "drawable"));
                binding.imageViewWind.animate().rotation(weather.getWind().getDegree()).setDuration(1000).start();
                binding.bgMain.setBackgroundResource(BgColorSetter.set(weather.getMain().getMaxTemp()));
                binding.swipe.setRefreshing(false);
            }

        });
        viewModel.getFiveDaysWeatherLiveDataResponse().observe(this, fiveDaysWeather -> {
            if (fiveDaysWeather != null) {
                List<Day> dayList = fiveDaysWeather.getDays();
                adapterHours.setData(dayList);
                adapterDays.setData(dayList);
                binding.swipe.setRefreshing(false);
            }

        });
        Place place = viewModel.getSavedPlace();
        if (TextUtils.isEmpty(place.getName())) {
            viewModel.onClickGeo();

        } else updateUi(place);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void setupAdapters() {
        adapterHours = new HoursAdapter(this.hours, getContext());
        adapterDays = new DaysAdapter(this.days, getContext());
        binding.resyclerDays.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.resyclerDays.setAdapter(adapterDays);
        binding.recyclerHours.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerHours.setAdapter(adapterHours);


    }

    private void updateUi(Place place) {
        binding.swipe.setRefreshing(true);
        viewModel.updateWeather(place);
    }

    public void checkLocPermissions() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

    }


}
