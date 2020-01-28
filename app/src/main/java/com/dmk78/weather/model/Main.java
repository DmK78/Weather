package com.dmk78.weather.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class Main {

    private float temp;

   private float pressure;

    private float humidity;

    @SerializedName("temp_min")
    private float minTemp;

    @SerializedName("temp_max")
    private float maxTemp;

    public float getTemp() {
        return temp;
    }

   public float getPressure() {
       return pressure;
   }

    public float getHumidity() {
        return humidity;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Main main = (Main) o;
        return Float.compare(main.temp, temp) == 0 &&
                Float.compare(main.pressure, pressure) == 0 &&
                Float.compare(main.humidity, humidity) == 0 &&
                Float.compare(main.minTemp, minTemp) == 0 &&
                Float.compare(main.maxTemp, maxTemp) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temp, pressure, humidity, minTemp, maxTemp);
    }
}