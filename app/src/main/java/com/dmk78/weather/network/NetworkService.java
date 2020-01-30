package com.dmk78.weather.network;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dmk78.weather.BuildConfig;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.Constants;
import com.dmk78.weather.weather.WeatherContract;
import com.google.android.libraries.places.api.model.Place;

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
public class NetworkService implements WeatherContract.ViewModel {
    private Retrofit mRetrofit;


    public NetworkService() {

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
    public LiveData<CurrentWeather> getCurWeather(Place place) {

        final MutableLiveData<CurrentWeather> data = new MutableLiveData<>();
        getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang)
                .enqueue(new Callback<CurrentWeather>() {

                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        //Log.d(TAG, "onResponse response:: " + response);
                        if (response.body() != null) {
                            CurrentWeather result = response.body();
                            if (!TextUtils.isEmpty(place.getName())) {
                                result.setCityName(place.getName());
                            }
                            result.setLatLng(place.getLatLng());
                            data.setValue(result);
                            getFiveDaysWeather(place);
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public LiveData<FiveDaysWeather> getFiveDaysWeather(Place place) {

        final MutableLiveData<FiveDaysWeather> data = new MutableLiveData<>();
        getJSONApi().getFiveDaysWeather(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang)
                .enqueue(new Callback<FiveDaysWeather>() {

                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        //Log.d(TAG, "onResponse response:: " + response);
                        if (response.body() != null) {
                            FiveDaysWeather result = response.body();
                            result.calculateDateTime();
                            data.setValue(result);

                        }
                    }

                    @Override
                    public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }


    public JsonPlaceHolderApi getJSONApi() {
        return mRetrofit.create(JsonPlaceHolderApi.class);
    }

    public interface WeatherCallback {
        void getCurWeather(CurrentWeather currentWeather);

        void getFiveDaysWeather(FiveDaysWeather fiveDaysWeather);
    }


}