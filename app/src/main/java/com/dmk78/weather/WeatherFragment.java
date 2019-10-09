package com.dmk78.weather;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dmk78.weather.Data.CurrentWeather;
import com.dmk78.weather.Data.FiveDaysWeather;
import com.dmk78.weather.Data.WeatherDay;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {
    public static String ERROR_CODE = "errorCode";
    private TextView textViewCity;
    private String url = "http://api.openweathermap.org/data/2.5/";
    private String key = "8f99535cdea446be868e707ba8062fc0";
    private String units = "metric";
    private String lang = "ru";
    private Button buttonOneDay, buttonFiveDays;
    NetworkService networkService = NetworkService.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather, container, false);
        textViewCity = view.findViewById(R.id.textViewCity);
        buttonOneDay = view.findViewById(R.id.buttonOneDay);
        buttonFiveDays = view.findViewById(R.id.buttonFiveDays);
        buttonOneDay.setOnClickListener(currentWeatherListener);
        buttonFiveDays.setOnClickListener(fiveDaysWeatherListener);
        return view;
    }

    private View.OnClickListener currentWeatherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            networkService.getJSONApi().getCurrentWeather("Екатеринбург", key, units, lang).enqueue(new Callback<CurrentWeather>() {
                @Override
                public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                    if (response.isSuccessful()) {
                        CurrentWeather currentWeather = response.body();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(currentWeather.getCityName() + "\n")
                                .append("temp: " + currentWeather.getMain().getTemp() + "\n")
                                .append("wind: " + currentWeather.getWind().getSpeed());
                        textViewCity.setText(stringBuilder);
                        //writePostsToRecycler(postsFromUrl);
                    } else {
                        Toast.makeText(getContext(), String.format("Error code is: %s", response.code()), Toast.LENGTH_SHORT).show();
                        Log.i("MyError", "" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<CurrentWeather> call, Throwable t) {
                    Toast.makeText(getContext(), String.format("Error code is: %s", t.getMessage()), Toast.LENGTH_SHORT).show();
                    Log.i("MyError", "" + t.getMessage());
                }
            });
        }
    };

    private View.OnClickListener fiveDaysWeatherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            networkService.getJSONApi().getFiveDaysWeather("Екатеринбург", key, units, lang)
                    .enqueue(new Callback<FiveDaysWeather>() {
                        @Override
                        public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                            if (response.isSuccessful()) {
                                FiveDaysWeather fiveDaysWeather = response.body();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(fiveDaysWeather.getCity().getName() + "\n");
                                for (WeatherDay weatherDay : fiveDaysWeather.getList()) {
                                    stringBuilder.append(weatherDay.getWeather().get(0).getDescription() + "\n");
                                }
                                    /*.append("temp: " + currentWeather.getMain().getTemp() + "\n")
                                    .append("wind: " + currentWeather.getWind().getSpeed());*/
                                textViewCity.setText(stringBuilder);
                                //writePostsToRecycler(postsFromUrl);
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
    };
}
