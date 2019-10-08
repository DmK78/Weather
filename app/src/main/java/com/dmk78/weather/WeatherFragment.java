package com.dmk78.weather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dmk78.weather.Data.CurrentWeather;
import com.dmk78.weather.Data.FiveDaysWeather;
import com.dmk78.weather.Data.WeatherDay;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends Fragment {
    public static String ERROR_CODE = "errorCode";
    private TextView textViewCity;
    private String url = "http://api.openweathermap.org/data/2.5/";
    private String key = "8f99535cdea446be868e707ba8062fc0";
    private String units = "metric";
    private String lang = "ru";
    private Button buttonOneDay, buttonFiveDays;

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

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            }
            client.addInterceptor(interceptor);
            OkHttpClient clientErrorIntercept = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            okhttp3.Response response = chain.proceed(request);
                            if (response.code() >= 400 && response.code() <= 599) {
                                //Intent intent = new Intent(getContext(), ErrorActivity.class);
                                //intent.putExtra(ERROR_CODE,response.code());
                                //startActivity(intent);
                                Log.i("MyError", "" + response.code());
                                return response;
                            }
                            return response;
                        }
                    })
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .client(clientErrorIntercept)
                    .build();
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<CurrentWeather> call = jsonPlaceHolderApi.getCurrentWeather("Екатеринбург", key, units, lang);
            call.enqueue(new Callback<CurrentWeather>() {
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

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            }
            client.addInterceptor(interceptor);
            OkHttpClient clientErrorIntercept = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            okhttp3.Response response = chain.proceed(request);
                            if (response.code() >= 400 && response.code() <= 599) {
                                //Intent intent = new Intent(getContext(), ErrorActivity.class);
                                //intent.putExtra(ERROR_CODE,response.code());
                                //startActivity(intent);
                                Log.i("MyError", "" + response.code());
                                return response;
                            }
                            return response;
                        }
                    })
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .client(clientErrorIntercept)
                    .build();
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<List<WeatherDay>> call = jsonPlaceHolderApi.getDaysWeather("Екатеринбург", key, units, lang);
            call.enqueue(new Callback<List<WeatherDay>>() {
                @Override
                public void onResponse(Call<List<WeatherDay>> call, Response<List<WeatherDay>> response) {
                    if (response.isSuccessful()) {
                        List<WeatherDay> days = response.body();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (WeatherDay weatherDay : days) {
                            stringBuilder.append(weatherDay.getWind().getSpeed() + "\n");
                                    /*.append("temp: " + currentWeather.getMain().getTemp() + "\n")
                                    .append("wind: " + currentWeather.getWind().getSpeed());*/


                        }


                        textViewCity.setText(stringBuilder);
                        //writePostsToRecycler(postsFromUrl);

                    } else {
                        Toast.makeText(getContext(), String.format("Error code is: %s", response.code()), Toast.LENGTH_SHORT).show();
                        Log.i("MyError", "" + response.code());
                    }

                }

                @Override
                public void onFailure(Call<List<WeatherDay>> call, Throwable t) {
                    Toast.makeText(getContext(), String.format("Error code is: %s", t.getMessage()), Toast.LENGTH_SHORT).show();
                    Log.i("MyError", "" + t.getMessage());

                }
            });


        }
    };
}
