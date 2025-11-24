package com.example.useractivityservice.dto;

import java.util.List;
import java.util.UUID;

public class MediaResponseDTO {
    private UUID mediaId;
    private List<String> genres;

    public MediaResponseDTO(List<String> genres, UUID mediaId) {
        this.genres = genres;
        this.mediaId = mediaId;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
