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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

import java.util.Arrays;
import java.util.List;
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

    NetworkService networkService = NetworkService.getInstance();
    private RecyclerView recycler;
    public DaysAdapter adapter;
    private List<Day> days;
    private List<Day> convertedDays;
    private ImageView imageViewCurrentTemp, imageViewWind, imageViewGetCurrentLocation;
    private EditText editTextEnterCity;
    private TextView textViewTemp, textViewMinTemp, textViewPressure, textViewWeatherDesc,
            textViewWindSpeed, textViewWindDirection;
    private CurrentWeather currentWeather;
    private Button buttonFindCity;
    private PlacePreferences placePreferences;
    private String currentCity;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public void onResume() {
        super.onResume();
        currentCity = placePreferences.getPlaceName();
        if (currentCity != "") {
            mLatitude = placePreferences.getLat();
            mLongitude = placePreferences.getLng();
            getWeatherByCoord(mLatitude, mLongitude);
        }
    }

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
                getWeatherByCoord(mLatitude, mLongitude);


            }

            @Override
            public void onError(Status status) {
                Log.i(WeatherFragment.class.getName(), "An error occurred: " + status);
            }
        });
        //getAllDays("Dubai");
        //recycler = view.findViewById(R.id.resyclerDays);
        //this.recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));


        return view;
    }

    private void checkLocPermissions(){

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
                   // if (currentCity == "") {
                        currentCity = currentWeather.getCityName();
                    //}
                    renderCurrentWeather();
                    savePreferences();

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
                            renderCurrentWeather();
                            savePreferences();
                            // getAllDays(currentWeather.getCityName());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {

                    }
                });
    }

    private void renderCurrentWeather() {
        textViewCity.setText(currentCity + ", " + currentWeather.getSys().getCountry());
        textViewTemp.setText(String.valueOf(Math.round(currentWeather.getMain().getTemp())) + " C");
        textViewMinTemp.setText(String.valueOf(Math.round(currentWeather.getMain().getMinTemp())) + " C");
        textViewPressure.setText(String.valueOf(currentWeather.getMain().getPressure()));
        textViewWeatherDesc.setText(currentWeather.getWeather().get(0).getDescription());
        textViewWindSpeed.setText(String.valueOf(currentWeather.getWind().getSpeed()) + "m/s");
//        textViewWindDirection.setText(String.valueOf(currentWeather.getWind().getDegree())+"degree");
        imageViewCurrentTemp.setImageResource(Utils.convertIconSourceToId(currentWeather.getWeather().get(0).getIcon()));
        imageViewWind.animate().rotation(currentWeather.getWind().getDegree()).setDuration(1000).start();
    }


    private void getAllDays(String city) {


        networkService.getJSONApi().getFiveDaysWeather(city, key, units, lang)
                .enqueue(new Callback<FiveDaysWeather>() {
                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        if (response.isSuccessful()) {
                            FiveDaysWeather fiveDaysWeather = response.body();

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

        adapter = new DaysAdapter(days, getContext());
        recycler.setAdapter(adapter);
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
