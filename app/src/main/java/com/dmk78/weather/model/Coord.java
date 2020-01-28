package com.dmk78.weather.model;

import java.util.Objects;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class Coord {


    private Double lat;

    private Double lon;

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Objects.equals(lat, coord.lat) &&
                Objects.equals(lon, coord.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
