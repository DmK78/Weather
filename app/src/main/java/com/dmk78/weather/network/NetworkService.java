package com.dmk78.weather.network;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.dmk78.weather.BuildConfig;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.Constants;
import com.google.android.libraries.places.api.model.Place;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */
public class NetworkService {
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client.build())
                .client(clientErrorIntercept)
                .build();
    }


    public void getCurWeather(Place place, MutableLiveData<CurrentWeather> callback) {

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
                            callback.postValue(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {
                        callback.setValue(null);
                    }
                });
    }

    public void getFiveDaysWeather(Place place, MutableLiveData<FiveDaysWeather> callback) {

        getJSONApi().getFiveDaysWeather(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang)
                .enqueue(new Callback<FiveDaysWeather>() {

                    @Override
                    public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                        //Log.d(TAG, "onResponse response:: " + response);
                        if (response.body() != null) {
                            FiveDaysWeather result = response.body();
                            result.calculateDateTime();
                            callback.postValue(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                        callback.setValue(null);
                    }
                });
    }

    public void getCurWeatherRX(Place place, MutableLiveData<CurrentWeather> callback) {
        getJSONApi().getCurrentWeatherByCoordRX(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<CurrentWeather>() {
                    @Override
                    public void onSuccess(CurrentWeather currentWeather) {
                            if (!TextUtils.isEmpty(place.getName())) {
                                currentWeather.setCityName(place.getName());
                            }
                            currentWeather.setLatLng(place.getLatLng());
                            callback.postValue(currentWeather);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void getFiveDaysWeatherRX (Place place, MutableLiveData<FiveDaysWeather> callback) {

        getJSONApi().getFiveDaysWeatherRX(place.getLatLng().latitude, place.getLatLng().longitude,
                Constants.key, Constants.units, Constants.lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<FiveDaysWeather>() {
                    @Override
                    public void onSuccess(FiveDaysWeather fiveDaysWeather) {
                        fiveDaysWeather.calculateDateTime();
                        callback.postValue(fiveDaysWeather);
                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    public JsonPlaceHolderApi getJSONApi() {
        return mRetrofit.create(JsonPlaceHolderApi.class);
    }


}