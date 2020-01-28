package com.dmk78.weather.utils;

import com.google.android.libraries.places.api.model.Place;

public interface PlacePreferences {
    void savePlace(Place place);

    Place loadPlace();

}
