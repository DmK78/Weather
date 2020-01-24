package com.dmk78.weather.utils;

import android.location.Location;

public interface MyLocationInterface {
    Location getLocation();
    boolean canGetLocation();
}
