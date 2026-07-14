package com.movielist.controller;

import com.movielist.dto.MovieResponse;
import com.movielist.dto.SimilarByTitleRequest;
import com.movielist.entity.User;
import com.movielist.security.CustomUserDetailsService;
import com.movielist.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final CustomUserDetailsService userDetailsService;

    public RecommendationController(RecommendationService recommendationService,
                                    CustomUserDetailsService userDetailsService) {
        this.recommendationService = recommendationService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/for-you")
    public List<MovieResponse> forYou(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return recommendationService.getForYou(user);
    }

    @GetMapping("/similar")
    public List<MovieResponse> similar(@RequestParam Long movieId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userDetailsService.getUserByEmail(userDetails.getUsername());
            return recommendationService.getSimilarForUser(user, movieId);
        }
        return recommendationService.getSimilar(movieId);
    }

    @PostMapping("/similar-by-title")
    public List<MovieResponse> similarByTitle(@Valid @RequestBody SimilarByTitleRequest request) {
        return recommendationService.getSimilarByTitle(request.title());
    }
}
