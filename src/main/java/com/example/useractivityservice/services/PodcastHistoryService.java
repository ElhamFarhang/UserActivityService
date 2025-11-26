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

//--------------------- Elham - PodcastHistoryService --------------
@Service
public class PodcastHistoryService {

    @Value("${podcast.service.url}")
    private String podcastServiceUrl;

    private static final Logger FUNCTIONALITY_LOGGER = LogManager.getLogger("functionality");
    private final RestTemplate restTemplate;
    private final UserActivityRepository userActivityRepository;

    public PodcastHistoryService(RestTemplate restTemplate, UserActivityRepository userActivityRepository) {
        this.restTemplate = restTemplate;
        this.userActivityRepository = userActivityRepository;
    }

    public Object registerPodcastHistory(String podurl, String userId, String accessToken) {
       try{
        String endpoint = UriComponentsBuilder
                .fromHttpUrl(podcastServiceUrl + "/pods/podcasts/episodes/getidandgenrefromurl/?url=")
                .queryParam("url", podurl)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<MediaResponseDTO> response = restTemplate.exchange(endpoint , HttpMethod.GET, entity, MediaResponseDTO.class);
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setMediaId(response.getBody().getMediaId());
        activity.setMediaType(MediaType.PODCAST.name());
        activity.setGenreName(response.getBody().getGenres());
        activity.setPlayedAt(LocalDateTime.now());
        userActivityRepository.save(activity);

        FUNCTIONALITY_LOGGER.info("User activity saved successfully: userId: '{}', mediaUrl: '{}'",  activity.getUserId(), podurl);
        return activity;
    } catch (Exception e) {
        FUNCTIONALITY_LOGGER.error("Failed to register podcast history. mediaUrl: '{}', error: '{}'", podurl, e.getMessage());
        throw new RuntimeException("Error while registering podcast history: " + e.getMessage(), e);
    }
    }
}
