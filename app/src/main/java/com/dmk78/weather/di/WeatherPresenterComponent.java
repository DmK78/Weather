package com.dmk78.weather.di;

import androidx.fragment.app.Fragment;

import com.dmk78.weather.weather.WeatherContract;
import com.dmk78.weather.weather.WeatherFragment;
import com.dmk78.weather.weather.WeatherPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Dmitry Kolganov (mailto:dmk78@inbox.ru)
 * @version $Id$
 * @since 28.01.2020
 */

@Singleton
@Component(modules = WeatherPresenterModule.class)
public interface WeatherPresenterComponent {
    void injectTo(WeatherFragment weatherFragment);


}
