package com.dmk78.weather;

import android.location.Location;
import android.os.Bundle;

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

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.model.Day;
import com.dmk78.weather.network.NetworkService;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {
    private ConstraintLayout bg;
    private NetworkService networkService = NetworkService.getInstance();
    private RecyclerView recyclerDays;
    private DaysAdapter adapterDays;
    private RecyclerView recyclerHours;
    private HoursAdapter adapterHours;
    private List<Day> days = new ArrayList<>();
    private List<Day> hours = new ArrayList<>();
    private ImageView imageViewCurrentTemp, imageViewWind, imageViewGetCurrentLocation;
    private TextView textViewTemp, textViewMinTemp, textViewPressure, textViewWeatherDesc,
            textViewWindSpeed, textViewHumidity, textViewCity;
    //private CurrentWeather currentWeather;
    private PlacePreferences placePreferences;
    private LocationService locationService;
    private FiveDaysWeather fiveDaysWeather;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        bindAllViews(view);
        locationService = new LocationService(getContext(), WeatherFragment.this);
        locationService.checkLocPermissions();
        placePreferences = new PlacePreferences(getContext());
        imageViewGetCurrentLocation.setOnClickListener(this::getWeatherByLocation);
        Place currentPlace = placePreferences.getPlace();
        if (!currentPlace.getName().isEmpty()) {
            getWeatherByCoord(currentPlace);
        } else {
            getWeatherByLocation(view);
        }
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                getWeatherByCoord(place);
            }

            @Override
            public void onError(Status status) {
                Log.i(WeatherFragment.class.getName(), "An error occurred: " + status);
            }
        });
        this.recyclerDays.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerHours.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        return view;
    }


    private void bindAllViews(View view) {
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


    private void getWeatherByCoord(Place place) {
        networkService.getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude, place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                CurrentWeather result = null;
                if (response.isSuccessful()) {
                    result = response.body();
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                    if (place.getName().equals("")) {
                    } else {
                        result.setCityName(place.getName());
                    }
                    renderCurrentWeather(result);
                    placePreferences.savePlace(place);
                    getAllDays(place);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
            }
        });
    }

    private void renderCurrentWeather(CurrentWeather weather) {
        Date date = new Date();
        date.setTime((long) weather.getDt() * 1000);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy EEE");
        textViewCity.setText(formatForDateNow.format(date) + "\n" + weather.getCityName() + ", " + weather.getSys().getCountry());
        textViewTemp.setText(String.valueOf(Math.round(weather.getMain().getTemp())) + " C");
        textViewMinTemp.setText(String.valueOf(Math.round(weather.getMain().getMinTemp())) + " C");
        textViewPressure.setText(String.valueOf(Math.round(weather.getMain().getPressure())) + " мм");
        textViewWeatherDesc.setText(weather.getWeather().get(0).getDescription());
        textViewWindSpeed.setText(String.valueOf(weather.getWind().getSpeed()) + " m/s");
        textViewHumidity.setText(getString(R.string.humidity) + " " + String.valueOf(Math.round(weather.getMain().getHumidity())) + " %");
        imageViewCurrentTemp.setImageResource(Utils.convertIconSourceToId(weather.getWeather().get(0).getIcon()));
        imageViewWind.animate().rotation(weather.getWind().getDegree()).setDuration(1000).start();
        bg.setBackgroundResource(BgColorSetter.set(weather.getMain().getMaxTemp()));
    }


    private void getAllDays(Place place) {
        networkService.getJSONApi().getFiveDaysWeather(place.getLatLng().latitude,
                place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang)
                .enqueue(new Callback<FiveDaysWeather>() {
                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        if (response.isSuccessful()) {
                            fiveDaysWeather = response.body();
                            fiveDaysWeather.calculateDateTime();
                            fillDaysList(fiveDaysWeather.getDays());
                            fillHoursList(fiveDaysWeather.getDays());
                        } else {
                            Toast.makeText(getContext(), String.format("Error code is: %s", response.code()), Toast.LENGTH_SHORT).show();
                            Log.i("MyError", "" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                        Toast.makeText(getContext(), String.format("Error code is: %s", t.getMessage()), Toast.LENGTH_SHORT).show();
                        Log.i("MyError", "" + t.getMessage());
                    }
                });
    }

    private void fillHoursList(List<Day> days) {
        this.hours.clear();
        this.hours.addAll(getWeatherFor24Hours(days));
        adapterHours = new HoursAdapter(this.hours, getContext());
        recyclerHours.setAdapter(adapterHours);
    }

    private List<Day> getWeatherFor24Hours(List<Day> days) {
        List<Day> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            result.add(days.get(i));
        }
        return result;
    }

    private void fillDaysList(List<Day> days) {
        this.days.clear();
        this.days.addAll(convertToShort(days));
        adapterDays = new DaysAdapter(this.days, getContext());
        recyclerDays.setAdapter(adapterDays);
    }

    /**
     * метод обрезает дату
     *
     * @param data
     * @return
     */
    private String convertData(String data) {
        String result = data.substring(8, 10) + "." + data.substring(5, 7) + "." + data.substring(0, 4);
        return result;
    }

    /**
     * метод группирует дни по дате, вычисляет минимальную и максимальнут температуру
     *
     * @param days
     * @return
     */
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










        /*String baseData = convertData(days.get(0).getDt_txt());
        float minTemp = +100;
        float maxTemp = -100;
        List<Day> result = new ArrayList<>();

        for (int i = 0; i < days.size(); i++) {
            Day day = days.get(i);
            Log.i("days", day.getDt_txt());
            String tmp = convertData(day.getDt_txt());
            day.setDt_txt(tmp);
            if (day.getDt_txt().equals(baseData)) {
                if (day.getMain().getMinTemp() < minTemp) {
                    minTemp = day.getMain().getMinTemp();
                }
                if (day.getMain().getMaxTemp() > maxTemp) {
                    maxTemp = day.getMain().getMaxTemp();
                }

                continue;
            } else {
                Day dayPrev = days.get(i - 1);
                baseData = day.getDt_txt();
                dayPrev.getMain().setMaxTemp(maxTemp);
                dayPrev.getMain().setMinTemp(minTemp);
                maxTemp = -100;
                minTemp = +100;
                result.add(dayPrev);
            }

        }


        return result;*/
    }


    private void getWeatherByLocation(View view) {
        Location location = locationService.getCoord();
        if (location != null) {
            Place currentPlace = Place.builder().setName("").setLatLng(new LatLng(location.getLatitude(), location.getLongitude())).build();
            getWeatherByCoord(currentPlace);
        }
    }
}
