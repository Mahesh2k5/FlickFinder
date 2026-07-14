package com.movielist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.movielist.client.TmdbClient;
import com.movielist.entity.Movie;
import com.movielist.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MovieSeedService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;
    private final MovieService movieService;

    public MovieSeedService(TmdbClient tmdbClient, MovieRepository movieRepository, MovieService movieService) {
        this.tmdbClient = tmdbClient;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    public int seedPopularMovies(int pages) {
        Set<Long> seen = new HashSet<>();
        int count = 0;

        for (int page = 1; page <= pages; page++) {
            for (JsonNode node : tmdbClient.getPopularMovies(page)) {
                long tmdbId = node.get("id").asLong();
                if (seen.add(tmdbId)) {
                    movieService.ensureMovieCached(tmdbId, node);
                    count++;
                }
            }
        }

        for (JsonNode node : tmdbClient.getTrendingMovies()) {
            long tmdbId = node.get("id").asLong();
            if (seen.add(tmdbId)) {
                movieService.ensureMovieCached(tmdbId, node);
                count++;
            }
        }

        return count;
    }

    public int countMovies() {
        return (int) movieRepository.count();
    }
}
