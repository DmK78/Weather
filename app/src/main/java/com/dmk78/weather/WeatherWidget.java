package com.dmk78.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWidget extends AppWidgetProvider {
    private static final String SYNC_CLICKED = "weather_widget_update_action";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Объект RemoteViews дает нам доступ к отображаемым в виджете элементам:
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        //в данном случае - к TextView
        views.setTextViewText(R.id.tvWidgetCity, context.getString(R.string.updating));
        appWidgetManager.updateAppWidget(appWidgetId, views);
        PlacePreferences placePreferences = new PlacePreferences(context);
        String currentCity = placePreferences.getPlaceName();
        double lat = placePreferences.getLat();
        double lng = placePreferences.getLng();
        NetworkService networkService = NetworkService.getInstance();
        networkService.getJSONApi().getCurrentWeatherByCoord(lat, lng, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    CurrentWeather currentWeather = response.body();
                    views.setTextViewText(R.id.tvWidgetCity, currentCity);
                    /*if(currentCity.length()>10){
                        views.setFloat(R.id.tvWidgetCity, "setTextSize", 12);
                    }
                    if(currentWeather.getWeather().get(0).getDescription().length()>10){
                        views.setFloat(R.id.tvWidgetCity, "setTextSize", 8);
                    }*/
                    views.setTextViewText(R.id.tvWidgetTemp, Math.round(currentWeather.getMain().getTemp()) + " C");
                    views.setImageViewResource(R.id.ivWidgetWeather, Utils.convertIconSourceToId(currentWeather.getWeather().get(0).getIcon()));
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
            PlacePreferences placePreferences = new PlacePreferences(context);
            String currentCity = placePreferences.getPlaceName();
            double lat = placePreferences.getLat();
            double lng = placePreferences.getLng();
            NetworkService networkService = NetworkService.getInstance();
            networkService.getJSONApi().getCurrentWeatherByCoord(lat, lng, Constants.key, Constants.units, Constants.lang).enqueue(new Callback<CurrentWeather>() {
                @Override
                public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                    if (response.isSuccessful()) {
                        CurrentWeather currentWeather = response.body();
                        views.setTextViewText(R.id.tvWidgetCity, currentCity);
                        views.setTextViewText(R.id.tvWidgetTemp, Math.round(currentWeather.getMain().getTemp()) + " C");
                        views.setImageViewResource(R.id.ivWidgetWeather, Utils.convertIconSourceToId(currentWeather.getWeather().get(0).getIcon()));
                        views.setTextViewText(R.id.tvWidgetDesc, currentWeather.getWeather().get(0).getDescription());
                        views.setInt(R.id.widgetBg, "setBackgroundResource", BgColorSetter.set(currentWeather.getMain().getMaxTemp()));
/*                        if (currentCity.length() > 10) {
                            views.setFloat(R.id.tvWidgetCity, "setTextSize", 12);
                        }
                        if (currentWeather.getWeather().get(0).getDescription().length() > 10) {
                            views.setFloat(R.id.tvWidgetCity, "setTextSize", 8);
                        }*/
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
