package com.dmk78.weather;

import android.app.Application;

import android.graphics.Movie;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dmk78.weather.model.CurrentWeather;
import com.dmk78.weather.model.FiveDaysWeather;
import com.dmk78.weather.network.NetworkService;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static NetworkService networkService;
    private LiveData<FiveDaysWeather> fiveDaysWeatherLiveData;
    private LiveData<CurrentWeather> currentWeatherLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        networkService = new NetworkService(null);
        fiveDaysWeatherLiveData = n
        favoriteMovies = database.movieDao().getAllFavoriteMovies();
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
