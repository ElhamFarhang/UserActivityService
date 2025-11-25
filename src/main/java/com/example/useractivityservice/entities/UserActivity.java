package com.example.useractivityservice.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//--------------------- Elham - UserActivity --------------
@Entity
@Table(name = "user_activity")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, columnDefinition = "char(36)")
    private UUID mediaId;

    @Column(nullable = false)
    private String mediaType;

    @Column(name = "genre_name", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> genreName = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime playedAt;

    public UserActivity() {
    }

    public List<String> getGenreName() {
        return genreName;
    }

    public void setGenreName(List<String> genreName) {
        this.genreName = genreName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "UserActivity{" +
                "genreName=" + genreName +
                ", id=" + id +
                ", userId='" + userId + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", playedAt=" + playedAt +
                '}';
    }
}
