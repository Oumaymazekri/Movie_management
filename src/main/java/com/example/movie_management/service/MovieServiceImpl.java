package com.example.movie_management.service;

import com.example.movie_management.model.Movie;
import com.example.movie_management.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    @Cacheable(value = "movies")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    @Cacheable(value = "movie", key = "#id")
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    @CachePut(value = "movie", key = "#result.id")
    @CacheEvict(value = "movies", allEntries = true)
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    @CacheEvict(value = {"movie", "movies"}, allEntries = true)
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "moviesByTitle", key = "#title")
    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Cacheable(value = "moviesByDirector", key = "#director")
    public List<Movie> searchMoviesByDirector(String director) {
        return movieRepository.findByDirectorContainingIgnoreCase(director);
    }

    @Override
    @Cacheable(value = "moviesByGenre", key = "#genre")
    public List<Movie> searchMoviesByGenre(String genre) {
        return movieRepository.findByGenreContainingIgnoreCase(genre);
    }
}