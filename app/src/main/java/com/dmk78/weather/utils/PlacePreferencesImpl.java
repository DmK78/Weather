package com.dmk78.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import javax.inject.Inject;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class PlacePreferencesImpl implements PlacePreferences {
    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;

    public static final String APP_PREFERENCES_PLACE = "place";
    public static final String APP_PREFERENCES_LAT = "lat";
    public static final String APP_PREFERENCES_LNG = "lng";

    @Inject
    public PlacePreferencesImpl(SharedPreferences mSharedPreferences) {
        this.sharedPreferences = mSharedPreferences;
        editor = sharedPreferences.edit();
    }


    public PlacePreferencesImpl(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.MY_SETTINGS, Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();


    }


    @Override
    public void savePlace(Place place) {

        editor.putString(APP_PREFERENCES_PLACE, place.getName());
        editor.putFloat(APP_PREFERENCES_LAT, (float) place.getLatLng().latitude);
        editor.putFloat(APP_PREFERENCES_LNG, (float) place.getLatLng().longitude);
        editor.commit();
    }

    @Override
    public Place loadPlace() {
        return Place.builder().setName(sharedPreferences.getString(APP_PREFERENCES_PLACE, "")).
                setLatLng(new LatLng(sharedPreferences.getFloat(APP_PREFERENCES_LAT, 0),
                        sharedPreferences.getFloat(APP_PREFERENCES_LNG, 0))).build();
    }


}
