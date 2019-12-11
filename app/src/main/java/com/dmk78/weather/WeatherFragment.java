package com.dmk78.weather;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk78.weather.Data.CurrentWeather;
import com.dmk78.weather.Data.FiveDaysWeather;
import com.dmk78.weather.Data.Day;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    private TextView textViewCity;

    private String key = "8f99535cdea446be868e707ba8062fc0";
    private String units = "metric";
    private String lang = "ru";
    public static final String MY_SETTINGS = "my_settings";
    public static final String APP_PREFERENCES_CITY = "city"; // имя кота
    NetworkService networkService = NetworkService.getInstance();
    private RecyclerView recycler;
    public DaysAdapter adapter;
    private List<Day> days;
    private List<Day> convertedDays;
    private ImageView imageViewCurrentTemp, imageViewWind;
    private EditText editTextEnterCity;
    private TextView textViewTemp, textViewMinTemp, textViewPressure, textViewWeatherDesc,
    textViewWindSpeed, textViewWindDirection;
    private CurrentWeather currentWeather;
    private Button buttonFindCity;
    private SharedPreferences sharedPreferences;
    private String currentCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        imageViewCurrentTemp = view.findViewById(R.id.imageViewCurrent);
        editTextEnterCity = view.findViewById(R.id.editTextEnterCity);
        textViewCity = view.findViewById(R.id.textViewCurrentCity);
        textViewTemp = view.findViewById(R.id.textViewCurrentTemp);
        textViewMinTemp = view.findViewById(R.id.textViewCurrentTempMin);
        textViewPressure=view.findViewById(R.id.textViewPressure);
        textViewWeatherDesc=view.findViewById(R.id.textViewWeatherDesc);
        textViewWindSpeed=view.findViewById(R.id.textViewWindSpeed);
        textViewWindDirection=view.findViewById(R.id.textViewWindDirection);
        imageViewWind=view.findViewById(R.id.imageViewWind);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentCity = sharedPreferences.getString(APP_PREFERENCES_CITY, "");
        if (currentCity != "") {
            updateCurrentWeather(currentCity);
        }


        buttonFindCity = view.findViewById(R.id.buttonFind);
        buttonFindCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextEnterCity.getText().toString() != "") {
                    updateCurrentWeather(editTextEnterCity.getText().toString());
                    savePreferences(APP_PREFERENCES_CITY, editTextEnterCity.getText().toString());
                }
            }
        });
        editTextEnterCity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    updateCurrentWeather(editTextEnterCity.getText().toString());
                    savePreferences(APP_PREFERENCES_CITY, editTextEnterCity.getText().toString());
                }

                return false;
            }
        });

        getAllDays("Dubai");
        recycler = view.findViewById(R.id.resyclerDays);
        this.recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));


        return view;
    }

    private void updateCurrentWeather(final String city) {
        networkService.getJSONApi().getCurrentWeather(city, key, units, lang)
                .enqueue(new Callback<CurrentWeather>() {
                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        if (response.isSuccessful()) {
                            currentWeather = response.body();
                            renderCurrentWeather();
                            getAllDays(currentWeather.getCityName());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {

                    }
                });
    }

    private void renderCurrentWeather() {
        textViewCity.setText(currentWeather.getCityName());
        textViewTemp.setText(String.valueOf(Math.round(currentWeather.getMain().getTemp()))+" C");
        textViewMinTemp.setText(String.valueOf(Math.round(currentWeather.getMain().getMinTemp()))+" C");
        textViewPressure.setText(String.valueOf(currentWeather.getMain().getPressure()));
        textViewWeatherDesc.setText(currentWeather.getWeather().get(0).getDescription());
        textViewWindSpeed.setText(String.valueOf(currentWeather.getWind().getSpeed())+"m/s");
        textViewWindDirection.setText(String.valueOf(currentWeather.getWind().getDegree())+"degree");
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

    private void savePreferences(String key, String value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


}
