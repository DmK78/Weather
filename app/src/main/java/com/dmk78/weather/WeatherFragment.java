package com.dmk78.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk78.weather.Data.CurrentWeather;
import com.dmk78.weather.Data.FiveDaysWeather;
import com.dmk78.weather.Data.Day;
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
import java.util.ListIterator;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment implements LocationListener {

    private TextView textViewCity;
    private double mLatitude;
    private double mLongitude;
    private String key = "8f99535cdea446be868e707ba8062fc0";
    private String units = "metric";
    private String lang = "ru";
    private ConstraintLayout bg;
    private NetworkService networkService = NetworkService.getInstance();
    private RecyclerView recycler;
    public DaysAdapter adapter;
    private List<Day> days;
    private List<Day> convertedDays;
    private ImageView imageViewCurrentTemp, imageViewWind, imageViewGetCurrentLocation;
    private TextView textViewTemp, textViewMinTemp, textViewPressure, textViewWeatherDesc,
            textViewWindSpeed;
    private CurrentWeather currentWeather;
    private PlacePreferences placePreferences;
    private String currentCity;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private FiveDaysWeather fiveDaysWeather;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        bindAllViews(view);

        placePreferences = new PlacePreferences(getContext());
        imageViewGetCurrentLocation.setOnClickListener(this::getCoord);
        currentCity = placePreferences.getPlaceName();

        if (currentCity != "") {
            mLatitude = placePreferences.getLat();
            mLongitude = placePreferences.getLng();
            getWeatherByCoord(mLatitude, mLongitude);
        } else {
            getCoord(getView());
        }


        Places.initialize(getContext(), getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng pos = place.getLatLng();
                mLatitude = pos.latitude;
                mLongitude = pos.longitude;
                currentCity = place.getName();
                search.setText(null);
                getWeatherByCoord(mLatitude, mLongitude);


            }

            @Override
            public void onError(Status status) {
                Log.i(WeatherFragment.class.getName(), "An error occurred: " + status);
            }
        });
        //getAllDays("Dubai");
        recycler = view.findViewById(R.id.resyclerDays);
        this.recycler.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    private void checkLocPermissions() {

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

    private void bindAllViews(View view) {
        imageViewCurrentTemp = view.findViewById(R.id.imageViewCurrent);
        textViewCity = view.findViewById(R.id.textViewCurrentCity);
        textViewTemp = view.findViewById(R.id.textViewCurrentTemp);
        textViewMinTemp = view.findViewById(R.id.textViewCurrentTempMin);
        textViewPressure = view.findViewById(R.id.textViewPressure);
        textViewWeatherDesc = view.findViewById(R.id.textViewWeatherDesc);
        textViewWindSpeed = view.findViewById(R.id.textViewWindSpeed);
        imageViewGetCurrentLocation = view.findViewById(R.id.imageViewGetLocation);
        imageViewWind = view.findViewById(R.id.imageViewWind);
        bg = view.findViewById(R.id.bgMain);
    }

    private void getCoord(View view) {
        checkLocPermissions();
        currentCity = "";

        LocationManager lm = (LocationManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
        if (lm != null) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
            if (location != null) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();

                getWeatherByCoord(mLatitude, mLongitude);

            }
        }
    }


    private void getWeatherByCoord(double mLatitude, double mLongitude) {
        networkService.getJSONApi().getCurrentWeatherByCoord(mLatitude, mLongitude, key, units, lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    currentWeather = response.body();
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                    if (currentCity != "") {
                        currentWeather.setCityName(currentCity);
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

    private void getWeatherByCityName(final String city) {
        networkService.getJSONApi().getCurrentWeatherByCity(city, key, units, lang)
                .enqueue(new Callback<CurrentWeather>() {
                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        if (response.isSuccessful()) {
                            currentWeather = response.body();
                            currentCity = city;
                            renderCurrentWeather(currentWeather);
                            savePreferences();
                            // getAllDays(currentWeather.getCityName());
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
        textViewCity.setText(formatForDateNow.format(date) + " " + currentWeather.getCityName() + ", " + this.currentWeather.getSys().getCountry());
        textViewTemp.setText(String.valueOf(Math.round(this.currentWeather.getMain().getTemp())) + " C");
        textViewMinTemp.setText(String.valueOf(Math.round(this.currentWeather.getMain().getMinTemp())) + " C");
        textViewPressure.setText(String.valueOf(this.currentWeather.getMain().getPressure()));
        textViewWeatherDesc.setText(this.currentWeather.getWeather().get(0).getDescription());
        textViewWindSpeed.setText(String.valueOf(this.currentWeather.getWind().getSpeed()) + "m/s");
//        textViewWindDirection.setText(String.valueOf(currentWeather.getWind().getDegree())+"degree");
        imageViewCurrentTemp.setImageResource(Utils.convertIconSourceToId(this.currentWeather.getWeather().get(0).getIcon()));
        imageViewWind.animate().rotation(this.currentWeather.getWind().getDegree()).setDuration(1000).start();
        bg.setBackgroundResource(BgColorSetter.set(currentWeather.getMain().getMaxTemp()));
    }


    private void getAllDays(String city) {


        networkService.getJSONApi().getFiveDaysWeather(mLatitude, mLongitude, key, units, lang)
                .enqueue(new Callback<FiveDaysWeather>() {
                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        if (response.isSuccessful()) {
                            fiveDaysWeather = response.body();

                            fillDaysList(fiveDaysWeather.getList());

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

    private void fillDaysList(List<Day> days) {

        days.addAll(days);

        convertedDays = convertToShort(days);

        adapter = new DaysAdapter(convertedDays, getContext());
        recycler.setAdapter(adapter);
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
        ListIterator<Day> iterator = days.listIterator();
        while (iterator.hasNext()) {
            Day day = iterator.next();
            String tmp = convertData(day.getDt_txt());
            if (day.getDt_txt().length() > 10) { //странный глюк, почему то итератор начинает проходить по второму разу, пришлось поставить это условие
                day.setDt_txt(tmp);
            }
            if (day.getDt_txt().equals(baseData)) {
                if (day.getMain().getMinTemp() < minTemp) {
                    minTemp = day.getMain().getMinTemp();
                }
                if (day.getMain().getMaxTemp() > maxTemp) {
                    maxTemp = day.getMain().getMaxTemp();
                }
                continue;
            } else {
                Day dayPrev = days.get(iterator.previousIndex() - 1);
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
        placePreferences.savePlace(currentCity, mLatitude, mLongitude);
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
}
