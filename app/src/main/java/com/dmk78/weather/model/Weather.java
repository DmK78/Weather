package com.dmk78.weather.model;

import java.util.Objects;

public class Weather {

    public Integer id;
    public String main;
    public String description;
    public String icon;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return Objects.equals(id, weather.id) &&
                Objects.equals(main, weather.main) &&
                Objects.equals(description, weather.description) &&
                Objects.equals(icon, weather.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, main, description, icon);
    }
}