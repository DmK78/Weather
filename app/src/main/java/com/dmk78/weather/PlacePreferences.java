package com.dmk78.weather;

import android.content.Context;
import android.content.SharedPreferences;

public class PlacePreferences {
    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;
    public static final String MY_SETTINGS = "my_settings";
    public static final String APP_PREFERENCES_PLACE = "place";
    public static final String APP_PREFERENCES_LAT = "lat";
    public static final String APP_PREFERENCES_LNG = "lng";

    public PlacePreferences(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(MY_SETTINGS,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }

    public void savePlace(String place, double lat, double lng) {
        editor.putString(APP_PREFERENCES_PLACE, place);
        editor.putFloat(APP_PREFERENCES_LAT,(float)lat);
        editor.putFloat(APP_PREFERENCES_LNG,(float)lng);
        editor.commit();
    }

    public String getPlaceName(){
        return sharedPreferences.getString(APP_PREFERENCES_PLACE,"");
    }
    public double getLat(){
        return (double) sharedPreferences.getFloat(APP_PREFERENCES_LAT,0);
    }
    public double getLng(){
        return (double) sharedPreferences.getFloat(APP_PREFERENCES_LNG,0);
    }


}
