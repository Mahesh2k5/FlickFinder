package com.movielist.service;

import com.movielist.dto.MovieResponse;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.entity.WatchlistItem;
import com.movielist.repository.WatchlistRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MovieService movieService;
    public WatchlistService(WatchlistRepository watchlistRepository, MovieService movieService) {
        this.watchlistRepository = watchlistRepository;
        this.movieService = movieService;
    }

    public MovieResponse addToWatchlist(User user, Long movieId) {
        Movie movie = movieService.getOrCreateEntity(movieId);
        if (watchlistRepository.existsByUserAndMovieId(user, movieId)) {
            return MovieResponse.from(movie);
        }
        WatchlistItem item = new WatchlistItem();
        item.setUser(user);
        item.setMovie(movie);
        watchlistRepository.save(item);
        return MovieResponse.from(movie);
    }

    @Transactional
    public void removeFromWatchlist(User user, Long movieId) {
        if (!watchlistRepository.existsByUserAndMovieId(user, movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Watchlist item not found");
        }
        watchlistRepository.deleteByUserAndMovieId(user, movieId);
    }

    public List<MovieResponse> getWatchlist(User user) {
        return watchlistRepository.findByUser(user).stream()
                .map(item -> MovieResponse.from(item.getMovie()))
                .toList();
    }

    public boolean isOnWatchlist(User user, Long movieId) {
        return watchlistRepository.existsByUserAndMovieId(user, movieId);
    }
}
