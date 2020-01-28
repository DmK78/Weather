package com.dmk78.weather.network;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.dmk78.weather.BuildConfig;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.Constants;
import com.dmk78.weather.weather.WeatherContract;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */
public class NetworkService implements WeatherContract.WeatherModel {
    private Retrofit mRetrofit;
    private WeatherCallback callback;

    public NetworkService(WeatherCallback callback) {
        if (callback != null) {
            this.callback = callback;
        }
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
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);
                    if (response.code() >= 400 && response.code() <= 599) {
                        Log.i("MyError", "" + response.code());
                        return response;
                    }
                    return response;
                })
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .client(clientErrorIntercept)
                .build();
    }

    @Override
    public void getCurWeather(Place place) {
        getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang).enqueue(new CurrentWeatherCallBack(place));
    }

    @Override
    public void getFiveDaysWeather(Place place) {
        getJSONApi().getFiveDaysWeather(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang).enqueue(new FiveDaysWeatherCallBack(place));
    }

    public LiveData<CurrentWeather> getCurWeatherByGeoLiveData(Place currentPlace) {

    }

    public LiveData<CurrentWeather> getCurWeatherByPlaceLiveData(Location currentPlace) {
    }

    public class CurrentWeatherCallBack implements Callback<CurrentWeather> {
        Place place;
        public CurrentWeatherCallBack(Place place) {
            this.place = place;
        }
        @Override
        public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
            if (response.isSuccessful()) {
                CurrentWeather result = response.body();
                if (!TextUtils.isEmpty(place.getName())) {
                    result.setCityName(place.getName());
                }
                result.setLatLng(place.getLatLng());
                callback.getCurWeather(result);
                getFiveDaysWeather(place);
            } else {
                Log.i(Constants.ERROR_LOG, String.valueOf(response.code()));
            }
        }
        @Override
        public void onFailure(Call<CurrentWeather> call, Throwable t) {
            Log.i(Constants.ERROR_LOG, t.getMessage());
        }
    }

    private class FiveDaysWeatherCallBack implements Callback<FiveDaysWeather> {
        Place place;
        public FiveDaysWeatherCallBack(Place place) {
            this.place = place;
        }

        @Override
        public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
            if (response.isSuccessful()) {
                FiveDaysWeather result = response.body();
                result.calculateDateTime();
                callback.getFiveDaysWeather(result);
            } else {
                Log.i(Constants.ERROR_LOG, String.valueOf(response.code()));
            }
        }

        @Override
        public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
            Log.i(Constants.ERROR_LOG, t.getMessage());
        }
    }


    public JsonPlaceHolderApi getJSONApi() {
        return mRetrofit.create(JsonPlaceHolderApi.class);
    }

    public interface WeatherCallback {
        void getCurWeather(CurrentWeather currentWeather);

        void getFiveDaysWeather(FiveDaysWeather fiveDaysWeather);
    }


}