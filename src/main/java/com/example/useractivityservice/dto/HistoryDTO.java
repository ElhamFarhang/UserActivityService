package com.example.useractivityservice.dto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HistoryDTO {

    private UUID mediaId;

    private String mediaType;

    private List<String> genreName = new ArrayList<>();

    private LocalDateTime playedAt;

    public HistoryDTO() {
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

    public List<String> getGenreName() {
        return genreName;
    }

    public void setGenreName(List<String> genreName) {
        this.genreName = genreName;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    @Override
    public String toString() {
        return "HistoryDTO{" +
                "mediaId=" + mediaId +
                ", mediaType='" + mediaType + '\'' +
                ", genreName=" + genreName +
                ", playedAt=" + playedAt +
                '}';
    }
}
