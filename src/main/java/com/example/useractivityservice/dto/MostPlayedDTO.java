package com.example.useractivityservice.dto;

import java.util.UUID;

public class MostPlayedDTO {


    private UUID mediaId;

    private Long timesPlayed;

    private String mediaType;

    public MostPlayedDTO(UUID mediaId, Long timesPlayed) {
        this.mediaId = mediaId;
        this.timesPlayed = timesPlayed;
    }


    public MostPlayedDTO(UUID mediaId, Long timesPlayed, String mediaType) {
        this.mediaId = mediaId;
        this.timesPlayed = timesPlayed;
        this.mediaType = mediaType;
    }


    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public Long getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(Long timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    @Override
    public String toString() {
        return "MostPlayedDTO{" +
                "mediaId=" + mediaId +
                ", timesPlayed=" + timesPlayed +
                ", mediaType='" + mediaType + '\'' +
                '}';
    }
}
