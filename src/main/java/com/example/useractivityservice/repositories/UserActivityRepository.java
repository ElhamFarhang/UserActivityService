package com.example.useractivityservice.repositories;

import com.example.useractivityservice.entities.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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




}
