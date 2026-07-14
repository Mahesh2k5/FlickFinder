package com.movielist.service;

import com.movielist.dto.MovieResponse;
import com.movielist.entity.Movie;
import com.movielist.entity.User;
import com.movielist.entity.UserLike;
import com.movielist.repository.UserLikeRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserLikeService {

    private final UserLikeRepository userLikeRepository;
    private final MovieService movieService;
    
    public UserLikeService(UserLikeRepository userLikeRepository, MovieService movieService) {
        this.userLikeRepository = userLikeRepository;
        this.movieService = movieService;
    }

    public MovieResponse likeMovie(User user, Long movieId) {
        Movie movie = movieService.getOrCreateEntity(movieId);
        if (userLikeRepository.existsByUserAndMovieId(user, movieId)) {
            return MovieResponse.from(movie);
        }
        UserLike like = new UserLike();
        like.setUser(user);
        like.setMovie(movie);
        userLikeRepository.save(like);
        return MovieResponse.from(movie);
    }

    @Transactional
    public void unlikeMovie(User user, Long movieId) {
        if (!userLikeRepository.existsByUserAndMovieId(user, movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found");
        }
        userLikeRepository.deleteByUserAndMovieId(user, movieId);
    }

    public List<MovieResponse> getLikes(User user) {
        return userLikeRepository.findByUser(user).stream()
                .map(like -> MovieResponse.from(like.getMovie()))
                .toList();
    }

    public boolean isLiked(User user, Long movieId) {
        return userLikeRepository.existsByUserAndMovieId(user, movieId);
    }
}
