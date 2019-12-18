package com.dmk78.weather;

public class BgColorSetter {
    public static int set(float temp) {
        int result = 0;

        if (temp > 30) {
            result = R.color.colorWeather7;

        }else
        if (temp <= 30 && temp > 20) {
            result = R.color.colorWeather6;
        }else
        if (temp <= 20 && temp > 10) {
            result = R.color.colorWeather5;
        }else
        if (temp <= 10 && temp > 0) {
            result = R.color.colorWeather4;
        }else
        if (temp <= 0 && temp > -10) {
            result = R.color.colorWeather3;
        }else
        if (temp <= -10 && temp > -20) {
            result = R.color.colorWeather2;
        }else
        if (temp <= -20) {
            result = R.color.colorWeather1;
        }
        return result;

    }
}