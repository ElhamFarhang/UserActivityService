package com.example.useractivityservice.repositories;

import com.example.useractivityservice.entities.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {

    @Query("""
            SELECT activity.mediaId, activity.mediaType, COUNT(activity.mediaId) AS playCount
            FROM UserActivity activity
            WHERE activity.userId = :userId
            GROUP BY activity.mediaId, activity.mediaType
            ORDER BY playCount DESC
            """)
    List<Object[]> findMostPlayedByUser(String userId);

////////////////Anna/////////////////////////////////////////////////////////

    @Query("""
    SELECT g
    FROM UserActivity ua
    JOIN ua.genreName g
    WHERE ua.userId = :userId
      AND ua.mediaType = :mediaType
      AND ua.playedAt >= :startDate
    GROUP BY g
    ORDER BY COUNT(g) DESC
    """)
    List<String> findMostFrequentGenresForUserId(@Param("userId") String userId, Pageable pageable,
                                                 @Param("mediaType") String mediaType, @Param("startDate") LocalDateTime startDate);


    @Query("""
    SELECT DISTINCT g
    FROM UserActivity ua
    JOIN ua.genreName g
    WHERE ua.mediaType = :mediaType
    """)
    List<String> findAllDistinctGenresByMediaType(@Param("mediaType") String mediaType);


    @Query("""
    SELECT ua.mediaId
    FROM UserActivity ua
    JOIN ua.genreName g
    WHERE g = :genre
      AND ua.mediaType = :mediaType
      AND ua.playedAt >= :startDate
    GROUP BY ua.mediaId
    ORDER BY COUNT(ua.id) DESC
""")
    List<UUID> findTopMediaTypeByGenreAndPeriod(@Param("genre") String genre, @Param("startDate") LocalDateTime startDate,
                                                Pageable pageable, @Param("mediaType") String mediaType);


    @Query("""
    SELECT DISTINCT ua.mediaId
    FROM UserActivity ua
    JOIN ua.genreName g
    WHERE ua.userId = :userId
      AND ua.mediaType = :mediaType
      AND ua.playedAt >= :startDate
      AND g = :genre
""")
    List<UUID> findMediaPlayedByUser(@Param("userId") String userId, @Param("mediaType") String mediaType,
                                     @Param("startDate") LocalDateTime startDate, @Param("genre") String genre);

}
