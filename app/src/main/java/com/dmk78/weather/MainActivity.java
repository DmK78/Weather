package com.dmk78.weather;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return new WeatherFragment();
    }


}
