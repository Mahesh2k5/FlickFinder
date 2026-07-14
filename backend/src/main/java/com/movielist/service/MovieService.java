package com.movielist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.movielist.client.TmdbClient;
import com.movielist.dto.MovieResponse;
import com.movielist.entity.Movie;
import com.movielist.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final TmdbClient tmdbClient;

    public MovieService(MovieRepository movieRepository, TmdbClient tmdbClient) {
        this.movieRepository = movieRepository;
        this.tmdbClient = tmdbClient;
    }

    public List<MovieResponse> search(String query) {
        List<JsonNode> results = tmdbClient.searchMovies(query);
        List<MovieResponse> movies = new ArrayList<>();
        for (JsonNode node : results) {
            Movie movie = ensureMovieCached(node.get("id").asLong(), node);
            movies.add(MovieResponse.from(movie));
        }
        return movies;
    }

    public List<MovieResponse> trending() {
        List<JsonNode> results = tmdbClient.getTrendingMovies();
        List<MovieResponse> movies = new ArrayList<>();
        for (JsonNode node : results) {
            Movie movie = ensureMovieCached(node.get("id").asLong(), node);
            movies.add(MovieResponse.from(movie));
        }
        return movies;
    }

    public MovieResponse getById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        return MovieResponse.from(movie);
    }

    public MovieResponse getByTmdbId(Long tmdbId) {
        Movie movie = ensureMovieCached(tmdbId, null);
        return MovieResponse.from(movie);
    }

    public List<MovieResponse> getByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return movieRepository.findAllById(ids).stream()
                .map(MovieResponse::from)
                .toList();
    }

    public Movie ensureMovieCached(long tmdbId, JsonNode summaryNode) {
        return movieRepository.findByTmdbId(tmdbId).orElseGet(() -> {
            try {
                JsonNode details = tmdbClient.getMovieDetails(tmdbId);
                Movie movie = summaryNode != null
                        ? tmdbClient.mapToEntity(summaryNode)
                        : tmdbClient.mapToEntity(details);
                tmdbClient.enrichMovieFromDetails(movie, details);
                return movieRepository.save(movie);
            } catch (Exception e) {
                Movie movie = new Movie();
                movie.setTmdbId(tmdbId);
                movie.setTitle("Unknown");
                return movieRepository.save(movie);
            }
        });
    }

    public Movie getOrCreateEntity(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }
}
