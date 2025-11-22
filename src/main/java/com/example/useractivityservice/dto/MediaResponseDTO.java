package com.example.useractivityservice.dto;

import java.util.List;

public class MediaResponseDTO {
    private String mediaId;
    private List<String> genres;

    public MediaResponseDTO(List<String> genres, String mediaId) {
        this.genres = genres;
        this.mediaId = mediaId;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
