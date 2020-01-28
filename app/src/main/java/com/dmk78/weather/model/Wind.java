package com.dmk78.weather.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */


public class Wind {

    private float speed;

    @SerializedName("deg")
    private float degree;


    public float getSpeed() {
        return speed;
    }

    public float getDegree() {
        return degree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wind wind = (Wind) o;
        return Float.compare(wind.speed, speed) == 0 &&
                Float.compare(wind.degree, degree) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(speed, degree);
    }
}