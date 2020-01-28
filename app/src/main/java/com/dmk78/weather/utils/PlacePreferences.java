package com.dmk78.weather.utils;

import com.google.android.libraries.places.api.model.Place;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public interface PlacePreferences {
    void savePlace(Place place);

    Place loadPlace();

}
