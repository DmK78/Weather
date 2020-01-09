package com.dmk78.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class WeatherFragment extends Fragment implements LocationListener {
    //private double mLatitude;
    //private double mLongitude;
    //private Location currentLocation;
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
    private CurrentWeather currentWeather;
    private PlacePreferences placePreferences;
    //private String currentCity;
    private LocationService locationService;
    private FiveDaysWeather fiveDaysWeather;
    private Place currentPlace;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        bindAllViews(view);
        locationService = new LocationService(getContext(), WeatherFragment.this);
        locationService.checkLocPermissions();
        placePreferences = new PlacePreferences(getContext());
        imageViewGetCurrentLocation.setOnClickListener(this::getWeatherByLocation);
        //currentCity = placePreferences.getPlaceName();
        currentPlace = Place.builder().setName(placePreferences.getPlaceName()).setLatLng(
                new LatLng(placePreferences.getLat(), placePreferences.getLng())).build();

        if (currentPlace.getName() != "") {
            /*currentLocation = new Location("empty location");
            currentLocation.setLatitude(placePreferences.getLat());
            currentLocation.setLongitude(placePreferences.getLng());*/
            getWeatherByCoord(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);
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
                currentPlace = place;
                /*LatLng pos = place.getLatLng();
                currentLocation.setLatitude(place.getLatLng().latitude);
                currentLocation.setLongitude(place.getLatLng().longitude);
                currentCity = place.getName();*/
                search.setText("");
                getWeatherByCoord(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);


            }

            @Override
            public void onError(Status status) {
                Log.i(WeatherFragment.class.getName(), "An error occurred: " + status);
            }
        });
        recyclerDays = view.findViewById(R.id.resyclerDays);
        this.recyclerDays.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerHours = view.findViewById(R.id.recyclerHours);
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
    }


    private void getWeatherByCoord(double mLatitude, double mLongitude) {
        networkService.getJSONApi().getCurrentWeatherByCoord(mLatitude, mLongitude, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    currentWeather = response.body();
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                    if (currentPlace.getName() != "") {
                        currentPlace = Place.builder().setName(currentWeather.getCityName()).setLatLng(new LatLng(mLatitude, mLongitude)).build();
                    }
                    renderCurrentWeather(currentWeather);
                    savePreferences();
                    getAllDays(currentWeather.getCityName());

                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }


    private void renderCurrentWeather(CurrentWeather currentWeather) {
        Date date = new Date();
        date.setTime((long) currentWeather.getDt() * 1000);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        textViewCity.setText(formatForDateNow.format(date) + "\n" + currentWeather.getCityName() + ", " + this.currentWeather.getSys().getCountry());
        textViewTemp.setText(String.valueOf(Math.round(this.currentWeather.getMain().getTemp())) + " C");
        textViewMinTemp.setText(String.valueOf(Math.round(this.currentWeather.getMain().getMinTemp())) + " C");
        textViewPressure.setText(String.valueOf(Math.round(this.currentWeather.getMain().getPressure())) + " мм");
        textViewWeatherDesc.setText(this.currentWeather.getWeather().get(0).getDescription());
        textViewWindSpeed.setText(String.valueOf(this.currentWeather.getWind().getSpeed()) + " m/s");
        textViewHumidity.setText(getString(R.string.humidity) + " " + String.valueOf(Math.round(currentWeather.getMain().getHumidity())) + " %");
        imageViewCurrentTemp.setImageResource(Utils.convertIconSourceToId(this.currentWeather.getWeather().get(0).getIcon()));
        imageViewWind.animate().rotation(this.currentWeather.getWind().getDegree()).setDuration(1000).start();
        bg.setBackgroundResource(BgColorSetter.set(currentWeather.getMain().getMaxTemp()));


    }


    private void getAllDays(String city) {
        networkService.getJSONApi().getFiveDaysWeather(currentPlace.getLatLng().latitude,
                currentPlace.getLatLng().longitude, Constants.key, Constants.units, Constants.lang)
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
        String baseData = convertData(days.get(0).getDt_txt());
        //String baseData = "";
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


        return result;
    }

    private void savePreferences() {
        placePreferences.savePlace(currentPlace.getName(), currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);
    }


    @Override
    public void onLocationChanged(Location location) {
        /*mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
        getWeatherByCoord(mLatitude, mLongitude);*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getWeatherByLocation(View view) {
        Location location = locationService.getCoord();
        if (location != null) {
            currentPlace = Place.builder().setLatLng(new LatLng(location.getAltitude(), location.getLongitude())).build();
            getWeatherByCoord(currentPlace.getLatLng().latitude, currentPlace.getLatLng().longitude);
        }
    }
}
