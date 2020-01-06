package com.dmk78.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.network.NetworkService;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWidget extends AppWidgetProvider {

    final String LOG_TAG = "myLogs";
    private static final String SYNC_CLICKED = "weather_widget_update_action";
    private static final String WAITING_MESSAGE = "Wait for weather";
    public static final int httpsDelayMs = 300;
    private static String key = "8f99535cdea446be868e707ba8062fc0";
    private static String units = "metric";
    private static String lang = "ru";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Объект RemoteViews дает нам доступ к отображаемым в виджете элементам:
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        //в данном случае - к TextView
        views.setTextViewText(R.id.tv, WAITING_MESSAGE);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        String output;
        //запускаем отдельный поток для получения данных с сайта биржи
        //в основном потоке делать запрос нельзя - выбросит исключение

        PlacePreferences placePreferences = new PlacePreferences(context);
        String currentCity = placePreferences.getPlaceName();
        double lat = placePreferences.getLat();
        double lng = placePreferences.getLng();

        NetworkService networkService = NetworkService.getInstance();
        networkService.getJSONApi().getCurrentWeatherByCoord(lat, lng, key, units, lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    CurrentWeather currentWeather = response.body();
                    views.setTextViewText(R.id.tv, "Weather for");
                    views.setTextViewText(R.id.textViewWidgetCity, currentCity);
                    views.setTextViewText(R.id.textViewWidgetTemp, String.valueOf(currentWeather.getMain().getTemp()));

                    //widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });


        //выводим в виджет результат


        // Instruct the widget manager to update the widget

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName componentName;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        componentName = new ComponentName(context, WeatherWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.textViewWidgetCity, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(componentName, remoteViews);

        //обновление всех экземпляров виджета
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
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

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            watchWidget = new ComponentName(context, WeatherWidget.class);

            remoteViews.setTextViewText(R.id.textViewWidgetCity, WAITING_MESSAGE);

            //updating widget
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
            PlacePreferences placePreferences = new PlacePreferences(context);
            String currentCity = placePreferences.getPlaceName();
            double lat = placePreferences.getLat();
            double lng = placePreferences.getLng();

            NetworkService networkService = NetworkService.getInstance();
            networkService.getJSONApi().getCurrentWeatherByCoord(lat, lng, key, units, lang).enqueue(new Callback<CurrentWeather>() {
                @Override
                public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                    if (response.isSuccessful()) {
                        CurrentWeather currentWeather = response.body();
                        remoteViews.setTextViewText(R.id.textViewWidgetCity, currentCity);
                        remoteViews.setTextViewText(R.id.textViewWidgetTemp, String.valueOf(currentWeather.getMain().getTemp()));
                        //remoteViews.setTextColor(R.id.textViewWidgetTemp, BgColorSetter.set(currentWeather.getMain().getTemp()));
                        //widget manager to update the widget
                        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
                    }

                }

                @Override
                public void onFailure(Call<CurrentWeather> call, Throwable t) {

                }
            });

//

        }
    }
}
