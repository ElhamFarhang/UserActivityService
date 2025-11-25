package com.example.useractivityservice.dto;

import java.util.List;
import java.util.UUID;

//--------------------- Elham - ActivityDTO --------------
public class ActivityDTO {

    private UUID mediaId;
    private String mediaType;
    private List<String> genreName;

    public ActivityDTO(List<String> genreName, UUID mediaId, String mediaType) {
        this.genreName = genreName;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
    }

    public List<String> getGenreName() {
        return genreName;
    }

    public void setGenreName(List<String> genreName) {
        this.genreName = genreName;
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
}
