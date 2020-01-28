package com.dmk78.weather.utils;

import android.location.Location;

public interface MyLocation {
    Location getLocation();
    boolean canGetLocation();
}
