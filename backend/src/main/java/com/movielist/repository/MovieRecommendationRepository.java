package com.movielist.repository;

import com.movielist.entity.MovieRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, Long> {
    List<MovieRecommendation> findByMovieIdOrderByScoreDesc(Long movieId);
}
