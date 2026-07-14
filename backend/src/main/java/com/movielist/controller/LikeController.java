package com.movielist.controller;

import com.movielist.dto.MovieResponse;
import com.movielist.entity.User;
import com.movielist.security.CustomUserDetailsService;
import com.movielist.service.UserLikeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final UserLikeService userLikeService;
    private final CustomUserDetailsService userDetailsService;

    public LikeController(UserLikeService userLikeService, CustomUserDetailsService userDetailsService) {
        this.userLikeService = userLikeService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public List<MovieResponse> getLikes(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return userLikeService.getLikes(user);
    }

    @PostMapping("/{movieId}")
    public MovieResponse like(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long movieId) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return userLikeService.likeMovie(user, movieId);
    }

    @DeleteMapping("/{movieId}")
    public void unlike(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long movieId) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        userLikeService.unlikeMovie(user, movieId);
    }
}
