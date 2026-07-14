package com.movielist.controller;

import com.movielist.service.MovieSeedService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final MovieSeedService movieSeedService;

    public AdminController(MovieSeedService movieSeedService) {
        this.movieSeedService = movieSeedService;
    }

    @PostMapping("/seed-movies")
    public Map<String, Object> seedMovies(@RequestParam(defaultValue = "10") int pages) {
        int seeded = movieSeedService.seedPopularMovies(pages);
        return Map.of(
                "seeded", seeded,
                "totalMovies", movieSeedService.countMovies()
        );
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void autoSeedOnStartup() {
        if (movieSeedService.countMovies() == 0) {
            System.out.println("Auto-seeding initial movies on startup...");
            movieSeedService.seedPopularMovies(2);
        }
    }
}
