package com.movielist.repository;

import com.movielist.entity.User;
import com.movielist.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    List<UserLike> findByUser(User user);
    Optional<UserLike> findByUserAndMovieId(User user, Long movieId);
    boolean existsByUserAndMovieId(User user, Long movieId);
    
    @Modifying
    @Query("DELETE FROM UserLike u WHERE u.user = :user AND u.movie.id = :movieId")
    void deleteByUserAndMovieId(@Param("user") User user, @Param("movieId") Long movieId);
}
