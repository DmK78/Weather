package com.dmk78.weather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.utils.BgColorSetter;
import com.dmk78.weather.utils.Constants;
import com.dmk78.weather.utils.PlacePreferences;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.dmk78.weather.R;
import com.dmk78.weather.utils.Utils;
import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.network.NetworkService;
import com.google.android.libraries.places.api.model.Place;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class WeatherWidget extends AppWidgetProvider {
    private static final String SYNC_CLICKED = "weather_widget_update_action";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Объект RemoteViews дает нам доступ к отображаемым в виджете элементам:
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        //в данном случае - к TextView
        views.setTextViewText(R.id.tvWidgetCity, context.getString(R.string.updating));
        appWidgetManager.updateAppWidget(appWidgetId, views);
        PlacePreferencesImpl placePreferences = new PlacePreferencesImpl(context);
        Place place = placePreferences.loadPlace();
        Log.i("city", String.valueOf(place.getName()));
        Log.i("city", String.valueOf(place.getLatLng().latitude));
        Log.i("city", String.valueOf(place.getLatLng().longitude));
        NetworkService networkService = new NetworkService();
        networkService.getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude, place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    CurrentWeather currentWeather = response.body();
                    Log.i("city", currentWeather.getCityName());
                    Log.i("city", currentWeather.getWeather().get(0).getDescription());
                    Log.i("city", Math.round(currentWeather.getMain().getTemp()) + " C");
                    views.setTextViewText(R.id.tvWidgetCity, place.getName());
                    views.setTextViewText(R.id.tvWidgetTemp, Math.round(currentWeather.getMain().getTemp()) + " C");
                    views.setImageViewResource(R.id.ivWidgetWeather, Utils.getStringIdentifier(context, "i" + currentWeather.getWeather().get(0).getIcon(), "drawable"));
                    //holder.imageViewDayWeather.setImageResource(Utils.getStringIdentifier(context, "i" + day.getWeather().get(0).getIcon(), "drawable"));
                    views.setTextViewText(R.id.tvWidgetDesc, currentWeather.getWeather().get(0).getDescription());
                    views.setInt(R.id.widgetBg, "setBackgroundResource", BgColorSetter.set(currentWeather.getMain().getMaxTemp()));
                    //widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
            }
        });
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName componentName;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        componentName = new ComponentName(context, WeatherWidget.class);
        remoteViews.setOnClickPendingIntent(R.id.widgetBg, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(componentName, remoteViews);
        //обновление всех экземпляров виджета
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SYNC_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views;
            ComponentName watchWidget;
            views = new RemoteViews(context.getPackageName(), R.layout.widget);
            watchWidget = new ComponentName(context, WeatherWidget.class);
            views.setTextViewText(R.id.tvWidgetCity, context.getString(R.string.updating));
            //updating widget
            appWidgetManager.updateAppWidget(watchWidget, views);
            PlacePreferences placePreferences = new PlacePreferencesImpl(context);
            Place place = placePreferences.loadPlace();
            NetworkService networkService = new NetworkService();


            networkService.getJSONApi().getCurrentWeatherByCoord(place.getLatLng().latitude, place.getLatLng().longitude, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
                @Override
                public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                    if (response.isSuccessful()) {
                        CurrentWeather currentWeather = response.body();
                        views.setTextViewText(R.id.tvWidgetCity, place.getName());
                        views.setTextViewText(R.id.tvWidgetTemp, Math.round(currentWeather.getMain().getTemp()) + " C");
                        //views.setImageViewResource(R.id.ivWidgetWeather, Utils.convertIconSourceToId(currentWeather.getWeather().get(0).getIcon()));
                        views.setImageViewResource(R.id.ivWidgetWeather, Utils.getStringIdentifier(context, "i" + currentWeather.getWeather().get(0).getIcon(), "drawable"));
                        views.setTextViewText(R.id.tvWidgetDesc, currentWeather.getWeather().get(0).getDescription());
                        views.setInt(R.id.widgetBg, "setBackgroundResource", BgColorSetter.set(currentWeather.getMain().getMaxTemp()));

                        //widget manager to update the widget
                        appWidgetManager.updateAppWidget(watchWidget, views);
                    }
                }

                @Override
                public void onFailure(Call<CurrentWeather> call, Throwable t) {
                }
            });
        }
    }


}

