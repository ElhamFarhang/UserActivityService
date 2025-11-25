package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.media.MediaType;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

//--------------------- Elham - MusicHistoryService --------------
@Service
public class MusicHistoryService {

    @Value("${music.service.url}")
    private String musicServiceUrl;

    private static final Logger FUNCTIONALITY_LOGGER = LogManager.getLogger("functionality");
    private final RestTemplate restTemplate;
    private final UserActivityRepository userActivityRepository;

    public MusicHistoryService(RestTemplate restTemplate, UserActivityRepository userActivityRepository) {
        this.restTemplate = restTemplate;
        this.userActivityRepository = userActivityRepository;
    }

    public Object registerMusicHistory(String musicurl, String userId, String accessToken) {
        try {
            String endpoint = UriComponentsBuilder
                    .fromHttpUrl(musicServiceUrl + "/songs/songurl")
                    .queryParam("url", musicurl)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<MediaResponseDTO> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, MediaResponseDTO.class);
            UserActivity activity = new UserActivity();
            activity.setUserId(userId);
            activity.setMediaId(response.getBody().getMediaId());
            activity.setMediaType(MediaType.MUSIC.name());
            activity.setGenreName(response.getBody().getGenres());
            activity.setPlayedAt(LocalDateTime.now());
            userActivityRepository.save(activity);

            FUNCTIONALITY_LOGGER.info("User activity saved successfully: userId:'{}', media url: '{}'", activity.getUserId(), musicurl);
            return activity;
        } catch (Exception e) {
            FUNCTIONALITY_LOGGER.error("Exception while registering music history media url: '{}', error: '{}'", musicurl, e.getMessage());
            throw new RuntimeException("Error while registering music history: " + e.getMessage(), e);
        }
    }
}
