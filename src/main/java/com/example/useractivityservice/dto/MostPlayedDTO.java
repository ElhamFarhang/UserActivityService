package com.example.useractivityservice.dto;

import java.util.UUID;

public class MostPlayedDTO {


    private UUID mediaId;

    private Long timesPlayed;

    public MostPlayedDTO(UUID mediaId, Long timesPlayed) {
        this.mediaId = mediaId;
        this.timesPlayed = timesPlayed;
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

    @Override
    public String toString() {
        return "MostPlayedDTO{" +
                "mediaId=" + mediaId +
                ", timesPlayed=" + timesPlayed +
                '}';
    }
}
