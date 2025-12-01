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
    List<MostPlayedDTO> findMostPlayedMediaByUserId(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate, Pageable pageable);


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
    List<MostPlayedDTO> findMostPlayedMediaByUserIdAndMediaType( @Param("userId") String userId,
                                                                @Param("mediaType") String mediaType, Pageable pageable);

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
    List<MostPlayedDTO> findMostPlayedMediaInPeriodByMediaType( @Param("mediaType") String mediaType,
                                                                @Param("startDate")  LocalDateTime startDate,
                                                                @Param("endDate") LocalDateTime endDate,
                                                                Pageable pageable);


    //History
    List<UserActivity> findByUserIdAndMediaTypeOrderByPlayedAtDesc(String userId, String mediaType);

    List<UserActivity> findByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc(String userId, LocalDateTime start, LocalDateTime end);


    //Recommendations
    List<UserActivity> findByMediaType(String mediaType);

    List<UserActivity> findByUserIdAndMediaTypeAndPlayedAtAfter(String userId, String mediaType, LocalDateTime startDate);

    List<UserActivity> findByMediaTypeAndPlayedAtAfter(String mediaType, LocalDateTime startDate);

    List<UserActivity> findByMediaTypeAndPlayedAtBetween(String mediaType, LocalDateTime start, LocalDateTime end);

}
