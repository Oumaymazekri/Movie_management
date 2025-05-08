package com.example.movie_management.service;

import com.example.movie_management.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();
    Optional<Movie> getMovieById(Long id);
    Movie saveMovie(Movie movie);
    void deleteMovie(Long id);
    List<Movie> searchMoviesByTitle(String title);
    List<Movie> searchMoviesByDirector(String director);
    List<Movie> searchMoviesByGenre(String genre);
}