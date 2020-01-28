package com.dmk78.weather.model;

import java.util.Objects;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class Sys {
    private String country;

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sys sys = (Sys) o;
        return Objects.equals(country, sys.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country);
    }
}