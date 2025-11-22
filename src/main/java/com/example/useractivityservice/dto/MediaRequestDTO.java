package com.example.useractivityservice.dto;

public class MediaRequestDTO {
    private String url;
    private String mediaType;

    public MediaRequestDTO(String mediaType, String url) {
        this.mediaType = mediaType;
        this.url = url;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
