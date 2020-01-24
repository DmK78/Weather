package com.dmk78.weather.weather;


import androidx.fragment.app.Fragment;

import com.dmk78.weather.BaseActivity;

public class WeatherActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return new WeatherFragment();
    }


}
