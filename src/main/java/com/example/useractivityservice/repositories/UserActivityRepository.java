package com.example.useractivityservice.repositories;

import com.example.useractivityservice.dto.MostPlayedDTO;
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

////////////////Anna/////////////////////////////////////////////////////////

//Most played
/*    @Query("""
        SELECT new com.example.useractivityservice.dto.MostPlayedDTO(ua.mediaId, COUNT(ua.mediaId))
        FROM UserActivity ua
        WHERE ua.userId = :userId
          AND ua.playedAt BETWEEN :startDate AND :endDate
        GROUP BY ua.mediaId
        ORDER BY COUNT(ua.mediaId) DESC
    """)
    List<MostPlayedDTO> findMostPlayedMediaByUserId(@Param("userId") String userId,  @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate, Pageable pageable);*/

    @Query("""
        SELECT new com.example.useractivityservice.dto.MostPlayedDTO(ua.mediaId, 
            COUNT(ua.mediaId), 
            MAX(ua.mediaType)
        )
        FROM UserActivity ua
        WHERE ua.userId = :userId
          AND ua.playedAt BETWEEN :startDate AND :endDate
        GROUP BY ua.mediaId
        ORDER BY COUNT(ua.mediaId) DESC
    """)
    List<MostPlayedDTO> findMostPlayedMediaByUserId(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);


/*
    @Query("""
        SELECT new com.example.useractivityservice.dto.MostPlayedDTO(ua.mediaId, COUNT(ua.mediaId))
        FROM UserActivity ua
        WHERE ua.userId = :userId
          AND ua.mediaType = :mediaType
        GROUP BY ua.mediaId
        ORDER BY COUNT(ua.mediaId) DESC
    """)
    List<MostPlayedDTO> findMostPlayedMediaByUserIdAndMediaType(@Param("userId") String userId, @Param("mediaType") String mediaType,
                                                                Pageable pageable);
*/

    @Query("""
    SELECT new com.example.useractivityservice.dto.MostPlayedDTO(
        ua.mediaId,
        COUNT(ua.mediaId),
        MAX(ua.mediaType)
    )
    FROM UserActivity ua
    WHERE ua.userId = :userId
      AND ua.mediaType = :mediaType
    GROUP BY ua.mediaId
    ORDER BY COUNT(ua.mediaId) DESC
""")
    List<MostPlayedDTO> findMostPlayedMediaByUserIdAndMediaType(
            @Param("userId") String userId,
            @Param("mediaType") String mediaType,
            Pageable pageable);


/*    @Query("""
        SELECT new com.example.useractivityservice.dto.MostPlayedDTO(ua.mediaId, COUNT(ua.mediaId))
        FROM UserActivity ua
        WHERE ua.mediaType = :mediaType
          AND ua.playedAt BETWEEN :startDate AND :endDate
        GROUP BY ua.mediaId
        ORDER BY COUNT(ua.mediaId) DESC
    """)
    List<MostPlayedDTO> findMostPlayedMediaInPeriodByMediaType(@Param("mediaType") String mediaType,  @Param("startDate") LocalDateTime startDate,
                                                               @Param("endDate") LocalDateTime endDate, Pageable pageable);*/

    @Query("""
    SELECT new com.example.useractivityservice.dto.MostPlayedDTO(
        ua.mediaId,
        COUNT(ua.mediaId),
        MAX(ua.mediaType)
    )
    FROM UserActivity ua
    WHERE ua.mediaType = :mediaType
      AND ua.playedAt BETWEEN :startDate AND :endDate
    GROUP BY ua.mediaId
    ORDER BY COUNT(ua.mediaId) DESC
""")
    List<MostPlayedDTO> findMostPlayedMediaInPeriodByMediaType(
            @Param("mediaType") String mediaType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

//History
List<UserActivity> findByUserIdAndMediaTypeOrderByPlayedAtDesc(String userId, String mediaType);

List<UserActivity> findByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc(String userId, LocalDateTime start, LocalDateTime end);

//Recommendations
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
    List<String> findMostFrequentGenresForUserId(@Param("userId") String userId,@Param("mediaType") String mediaType,
                                                 @Param("startDate") LocalDateTime startDate, Pageable pageable);


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
                                                @Param("mediaType") String mediaType, Pageable pageable);


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
