package com.movielist.controller;

import com.movielist.dto.MovieResponse;
import com.movielist.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public List<MovieResponse> search(@RequestParam("q") String query) {
        return movieService.search(query);
    }

    @GetMapping("/trending")
    public List<MovieResponse> trending() {
        return movieService.trending();
    }

    @GetMapping("/{id}")
    public MovieResponse getById(@PathVariable Long id) {
        return movieService.getById(id);
    }

    @GetMapping("/tmdb/{tmdbId}")
    public MovieResponse getByTmdbId(@PathVariable Long tmdbId) {
        return movieService.getByTmdbId(tmdbId);
    }
}
