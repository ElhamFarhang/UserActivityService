package com.example.useractivityservice.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_activity")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false) //UUID episode_id , UUID uuid
    private UUID mediaId;

    @Column(nullable = false)
    private String mediaType;

    @Column(nullable = false)
    private String genreName;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    public UserActivity() {
    }

    @PrePersist
    public void onCreate() {
        this.playedAt = LocalDateTime.now();
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserActivity[" +
                "genreName:'" + genreName + '\'' +
                ", id:" + id +
                ", userId:" + userId +
                ", mediaId:" + mediaId +
                ", mediaType:'" + mediaType + '\'' +
                ", playedAt:" + playedAt +
                ']';
    }
}
