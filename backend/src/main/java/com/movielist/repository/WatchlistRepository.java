package com.movielist.repository;

import com.movielist.entity.User;
import com.movielist.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {
    List<WatchlistItem> findByUser(User user);
    Optional<WatchlistItem> findByUserAndMovieId(User user, Long movieId);
    boolean existsByUserAndMovieId(User user, Long movieId);
    
    @Modifying
    @Query("DELETE FROM WatchlistItem w WHERE w.user = :user AND w.movie.id = :movieId")
    void deleteByUserAndMovieId(@Param("user") User user, @Param("movieId") Long movieId);
}
