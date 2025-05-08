package com.example.movie_management.service;

import com.example.movie_management.model.Movie;
import com.example.movie_management.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        movie1 = new Movie(1L, "Inception", "Christopher Nolan",
                LocalDate.of(2010, 7, 16),
                "Un voleur qui s'infiltre dans les rêves", 148, "Science-Fiction", 8.8);

        movie2 = new Movie(2L, "The Shawshank Redemption", "Frank Darabont",
                LocalDate.of(1994, 9, 23),
                "Deux hommes se lient d'amitié en prison", 142, "Drame", 9.3);
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie1, movie2));

        // Act
        List<Movie> result = movieService.getAllMovies();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).contains(movie1, movie2);
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovie() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));

        // Act
        Optional<Movie> result = movieService.getMovieById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(movie1);
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Movie> result = movieService.getMovieById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(movieRepository, times(1)).findById(999L);
    }

    @Test
    void saveMovie_ShouldReturnSavedMovie() {
        // Arrange
        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);

        // Act
        Movie result = movieService.saveMovie(movie1);

        // Assert
        assertThat(result).isEqualTo(movie1);
        verify(movieRepository, times(1)).save(movie1);
    }

    @Test
    void deleteMovie_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(movieRepository).deleteById(anyLong());

        // Act
        movieService.deleteMovie(1L);

        // Assert
        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchMoviesByTitle_ShouldReturnMatchingMovies() {
        // Arrange
        when(movieRepository.findByTitleContainingIgnoreCase("Inception")).thenReturn(List.of(movie1));

        // Act
        List<Movie> result = movieService.searchMoviesByTitle("Inception");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
        verify(movieRepository, times(1)).findByTitleContainingIgnoreCase("Inception");
    }
}