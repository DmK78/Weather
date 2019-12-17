package com.dmk78.weather.Data;

import com.google.gson.annotations.SerializedName;

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
}