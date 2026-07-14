package com.movielist.dto;

import com.movielist.entity.Movie;

public record MovieResponse(
        Long id,
        Long tmdbId,
        String title,
        Integer year,
        String posterUrl,
        String overview,
        String genres,
        String keywords,
        String cast,
        String director
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getPosterUrl(),
                movie.getOverview(),
                movie.getGenres(),
                movie.getKeywords(),
                movie.getCast(),
                movie.getDirector()
        );
    }
}
