package com.dmk78.weather.utils;

import com.dmk78.weather.R;

public class Utils {
    public static int convertIconSourceToId(String icon) {
        int result = 0;
        switch (icon) {
            case "01d":
                result= R.drawable.i01d;
                break;
            case "02d":
                result= R.drawable.i02d;
                break;
            case "03d":
                result= R.drawable.i03d;
                break;
            case "04d":
                result= R.drawable.i04d;
                break;
            case "09d":
                result= R.drawable.i09d;
                break;
            case "10d":
                result= R.drawable.i10d;
                break;
            case "11d":
                result= R.drawable.i11d;
                break;
            case "13d":
                result= R.drawable.i13d;
                break;
            case "50d":
                result= R.drawable.i50d;
                break;
            case "01n":
                result= R.drawable.i01n;
                break;
            case "02n":
                result= R.drawable.i02n;
                break;
            case "03n":
                result= R.drawable.i03n;
                break;
            case "04n":
                result= R.drawable.i04n;
                break;
            case "09n":
                result= R.drawable.i09n;
                break;
            case "10n":
                result= R.drawable.i10n;
                break;
            case "11n":
                result= R.drawable.i11n;
                break;
            case "13n":
                result= R.drawable.i13n;
                break;
            case "50n":
                result= R.drawable.i50n;
                break;


            default:
                break;



        }
        return result;
    }




}
