package com.dmk78.weather.weather;

import android.app.Application;

import android.graphics.Movie;
import android.os.AsyncTask;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;
import com.dmk78.weather.utils.MyLocation;
import com.dmk78.weather.utils.MyLocationImpl;
import com.dmk78.weather.utils.PlacePreferencesImpl;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WeatherViewModel extends AndroidViewModel {
    private PlacePreferencesImpl placePreferences;
    private NetworkService networkService;
    private LiveData<CurrentWeather> currentWeatherLiveData;
    private LiveData<FiveDaysWeather> fiveDaysWeatherLiveData;
    private Place currentPlace;
    private MyLocation locationService;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        locationService = new MyLocationImpl(getApplication().getApplicationContext(),null);
        networkService = new NetworkService(null);
        currentPlace = placePreferences.loadPlace();
        if(TextUtils.isEmpty(currentPlace.getName())){
            currentWeatherLiveData=networkService.getCurWeatherByGeoLiveData(currentPlace);
        } else {
            currentWeatherLiveData=networkService.getCurWeatherByPlaceLiveData(locationService.getLocation());

        }

    }

    public Movie getMovieByID(int movieID) {
        try {
            return new GetMovieTask().execute(movieID).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavoriteMovie getFavoriteMovieByID(int movieID) {
        try {
            return new GetFavoriteMovieTask().execute(movieID).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void deleteAllMovies() {
        new DeleteMoviesTask().execute();
    }

    public void insertMovie(Movie movie) {
        new InsertMovieTask().execute(movie);
    }

    public void deleteMovie(Movie movie) {
        new DeleteTask().execute(movie);
    }

    public void insertFavoriteMovie(FavoriteMovie movie) {
        new InsertFavoriteMovieTask().execute(movie);
    }

    public void deleteFavoriteMovie(FavoriteMovie movie) {
        new DeleteFavoriteTask().execute(movie);
    }

    public Place getSavedPlace() {
        return placePreferences.loadPlace();
    }

    public LiveData<CurrentWeather> getCurrentWeatherByGeoLiveData(Place place) {
    }

    public LiveData<CurrentWeather> getCurrentWeatherByPlaceLiveData(Place place) {
        return null;
    }

    private static class DeleteTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    private static class InsertMovieTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    ///

    private static class InsertFavoriteMovieTask extends AsyncTask<FavoriteMovie, Void, Void> {

        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertFavoriteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteFavoriteTask extends AsyncTask<FavoriteMovie, Void, Void> {

        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteFavoriteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... integers) {
            database.movieDao().deletAllMovies();
            return null;
        }
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class GetFavoriteMovieTask extends AsyncTask<Integer, Void, FavoriteMovie> {
        @Override
        protected FavoriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getFavoriteMovieById(integers[0]);
            }
            return null;
        }
    }
}
