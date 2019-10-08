package com.dmk78.weather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private String url = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String key = "&appid=8f99535cdea446be868e707ba8062fc0&lang=ru&units=metric";
    private FragmentManager fm;
    private Fragment weatherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager(); // получить FragmentManager
        weatherFragment = fm.findFragmentById(R.id.frameContainer);
        if (weatherFragment == null) {
            weatherFragment = new WeatherFragment();
            fm.beginTransaction()
                    .add(R.id.frameContainer, weatherFragment) // добавить фрагмент в контейнер
                    .commit();
        }
    }
}
