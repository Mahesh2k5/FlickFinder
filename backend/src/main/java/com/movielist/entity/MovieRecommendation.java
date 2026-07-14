package com.movielist.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movie_recommendations")
public class MovieRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "recommended_movie_id", nullable = false)
    private Long recommendedMovieId;

    private Double score;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getRecommendedMovieId() { return recommendedMovieId; }
    public void setRecommendedMovieId(Long recommendedMovieId) { this.recommendedMovieId = recommendedMovieId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
