package com.movielist.controller;

import com.movielist.dto.MovieResponse;
import com.movielist.entity.User;
import com.movielist.security.CustomUserDetailsService;
import com.movielist.service.WatchlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final CustomUserDetailsService userDetailsService;

    public WatchlistController(WatchlistService watchlistService, CustomUserDetailsService userDetailsService) {
        this.watchlistService = watchlistService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public List<MovieResponse> getWatchlist(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return watchlistService.getWatchlist(user);
    }

    @PostMapping("/{movieId}")
    public MovieResponse add(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long movieId) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return watchlistService.addToWatchlist(user, movieId);
    }

    @DeleteMapping("/{movieId}")
    public void remove(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long movieId) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        watchlistService.removeFromWatchlist(user, movieId);
    }
}
