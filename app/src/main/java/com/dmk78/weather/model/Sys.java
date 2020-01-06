package com.dmk78.weather.model;

import java.util.Objects;

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