package com.example.useractivityservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodeDto {

    private UUID id;
    private String url;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private Long durationSeconds;
//    private PodcastDto podcast;
    private UUID podcastId;
    private String thumbnailUrl;
    private String imageUrl;
    private UUID seasonId;

    public EpisodeDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

/*    public PodcastDto getPodcast() {
        return podcast;
    }

    public void setPodcast(PodcastDto podcast) {
        this.podcast = podcast;
    }*/

    public UUID getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(UUID podcastId) {
        this.podcastId = podcastId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UUID getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(UUID seasonId) {
        this.seasonId = seasonId;
    }

    @Override
    public String toString() {
        return "EpisodeDto{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", durationSeconds=" + durationSeconds +
//                ", podcast=" + podcast +
                ", podcastId=" + podcastId +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", seasonId=" + seasonId +
                '}';
    }
}
