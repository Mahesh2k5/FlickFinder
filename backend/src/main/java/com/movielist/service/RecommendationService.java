package com.movielist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.movielist.client.TmdbClient;
import com.movielist.dto.MovieResponse;
import com.movielist.entity.Movie;
import com.movielist.entity.MovieRecommendation;
import com.movielist.entity.User;
import com.movielist.repository.MovieRecommendationRepository;
import com.movielist.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import com.movielist.repository.UserLikeRepository;
import com.movielist.entity.UserLike;

@Service
public class RecommendationService {

    private final InteractionService interactionService;
    private final MovieService movieService;
    private final TmdbClient tmdbClient;
    private final MovieRecommendationRepository movieRecommendationRepository;
    private final UserLikeRepository userLikeRepository;

    public RecommendationService(InteractionService interactionService,
                                 MovieService movieService, TmdbClient tmdbClient,
                                 MovieRecommendationRepository movieRecommendationRepository,
                                 UserLikeRepository userLikeRepository) {
        this.interactionService = interactionService;
        this.movieService = movieService;
        this.tmdbClient = tmdbClient;
        this.movieRecommendationRepository = movieRecommendationRepository;
        this.userLikeRepository = userLikeRepository;
    }

    public List<MovieResponse> getForYou(User user) {
        List<UserLike> likes = userLikeRepository.findByUser(user);
        if (likes.isEmpty()) {
            return movieService.trending();
        }

        Map<Long, Double> scoreMap = new HashMap<>();
        for (UserLike like : likes) {
            List<MovieRecommendation> recs = movieRecommendationRepository.findByMovieIdOrderByScoreDesc(like.getMovie().getId());
            for (MovieRecommendation rec : recs) {
                // Ignore if the user already liked the recommended movie
                if (likes.stream().anyMatch(l -> l.getMovie().getId().equals(rec.getRecommendedMovieId()))) {
                    continue;
                }
                scoreMap.merge(rec.getRecommendedMovieId(), rec.getScore(), Double::sum);
            }
        }

        if (scoreMap.isEmpty()) {
            return movieService.trending();
        }

        List<Long> topMovieIds = scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return interactionService.resolveMovieIds(topMovieIds);
    }

    public List<MovieResponse> getSimilar(Long movieId) {
        List<Long> movieIds = movieRecommendationRepository.findByMovieIdOrderByScoreDesc(movieId)
                .stream()
                .map(MovieRecommendation::getRecommendedMovieId)
                .collect(Collectors.toList());

        if (movieIds.isEmpty()) {
            Movie movie = movieService.getOrCreateEntity(movieId);
            return fallbackSimilarFromTmdb(movie.getTmdbId());
        }
        return interactionService.resolveMovieIds(movieIds);
    }

    public List<MovieResponse> getSimilarForUser(User user, Long movieId) {
        return getSimilar(movieId);
    }

    public List<MovieResponse> getSimilarByTitle(String title) {
        List<JsonNode> results = tmdbClient.searchMovies(title);
        if (results.isEmpty()) {
            return List.of();
        }
        Movie movie = movieService.ensureMovieCached(results.get(0).get("id").asLong(), results.get(0));
        return getSimilar(movie.getId());
    }

    private List<MovieResponse> fallbackSimilarFromTmdb(Long tmdbId) {
        List<JsonNode> similarNodes = tmdbClient.getSimilarMovies(tmdbId);
        if (similarNodes == null || similarNodes.isEmpty()) {
            return movieService.trending();
        }
        
        return similarNodes.stream()
                .map(node -> {
                    Movie movie = tmdbClient.mapToEntity(node);
                    return MovieResponse.from(movie);
                })
                .limit(10)
                .collect(Collectors.toList());
    }
}
