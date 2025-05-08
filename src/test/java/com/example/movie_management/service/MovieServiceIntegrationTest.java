package com.example.movie_management.service;

import com.example.movie_management.model.Movie;
import com.example.movie_management.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // Assure-toi d’avoir un fichier application-test.properties
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Pour garder l'ordre si nécessaire
public class MovieServiceIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();

        testMovie = new Movie();
        testMovie.setTitle("Test Movie");
        testMovie.setDirector("Test Director");
        testMovie.setReleaseDate(LocalDate.of(2023, 1, 1));
        testMovie.setDescription("Test Description");
        testMovie.setDurationMinutes(120);
        testMovie.setGenre("Drama");
        testMovie.setRating(8.5);

        testMovie = movieRepository.save(testMovie);
    }

    @AfterEach
    void tearDown() {
        movieRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return all movies")
    void getAllMovies_ShouldReturnAllMovies() {
        List<Movie> movies = movieService.getAllMovies();

        assertThat(movies).isNotEmpty();
        assertThat(movies.size()).isEqualTo(1);
        assertThat(movies.get(0).getTitle()).isEqualTo("Test Movie");
    }

    @Test
    @DisplayName("Should return movie by ID if exists")
    void getMovieById_WithExistingId_ShouldReturnMovie() {
        Optional<Movie> found = movieService.getMovieById(testMovie.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Movie");
    }

    @Test
    @DisplayName("Should return empty when movie ID does not exist")
    void getMovieById_WithNonExistingId_ShouldReturnEmpty() {
        Optional<Movie> found = movieService.getMovieById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save a new movie")
    void saveMovie_ShouldCreateNewMovie() {
        Movie newMovie = new Movie();
        newMovie.setTitle("New Movie");
        newMovie.setDirector("New Director");
        newMovie.setReleaseDate(LocalDate.of(2023, 3, 3));
        newMovie.setDescription("New Description");
        newMovie.setDurationMinutes(150);
        newMovie.setGenre("Action");
        newMovie.setRating(7.0);

        Movie saved = movieService.saveMovie(newMovie);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("New Movie");

        Optional<Movie> found = movieRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Should delete the movie")
    void deleteMovie_ShouldRemoveMovie() {
        movieService.deleteMovie(testMovie.getId());

        Optional<Movie> found = movieRepository.findById(testMovie.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find movies by title")
    void searchMoviesByTitle_ShouldReturnMatchingMovies() {
        List<Movie> found = movieService.searchMoviesByTitle("Test");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Test Movie");
    }
}
