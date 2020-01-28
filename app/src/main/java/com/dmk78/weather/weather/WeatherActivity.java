package com.dmk78.weather.weather;


import androidx.fragment.app.Fragment;

import com.dmk78.weather.BaseActivity;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 01.12.2019
 */

public class WeatherActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return new WeatherFragment();
    }


}
