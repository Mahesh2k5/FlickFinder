package com.movielist.service;

import com.movielist.dto.MovieResponse;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InteractionService {

    private final UserLikeService userLikeService;
    private final WatchlistService watchlistService;
    private final MovieRepository movieRepository;

    public InteractionService(UserLikeService userLikeService, WatchlistService watchlistService,
                              MovieRepository movieRepository) {
        this.userLikeService = userLikeService;
        this.watchlistService = watchlistService;
        this.movieRepository = movieRepository;
    }

    public List<MovieResponse> getLikedMovies(User user) {
        return userLikeService.getLikes(user);
    }

    public List<MovieResponse> getWatchlist(User user) {
        return watchlistService.getWatchlist(user);
    }

    public List<MovieResponse> resolveMovieIds(List<Long> movieIds) {
        Map<Long, Movie> movieMap = new LinkedHashMap<>();
        for (Movie movie : movieRepository.findAllById(movieIds)) {
            movieMap.put(movie.getId(), movie);
        }
        List<MovieResponse> ordered = new ArrayList<>();
        for (Long id : movieIds) {
            Movie movie = movieMap.get(id);
            if (movie != null) {
                ordered.add(MovieResponse.from(movie));
            }
        }
        return ordered;
    }
}
