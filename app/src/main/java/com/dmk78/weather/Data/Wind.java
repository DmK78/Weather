package com.dmk78.weather.Data;

import com.google.gson.annotations.SerializedName;

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

}