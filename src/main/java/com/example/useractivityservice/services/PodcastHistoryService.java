package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.media.MediaType;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class PodcastHistoryService {

    @Value("${podcast.service.url}")
    private String podcastServiceUrl;

    private final RestTemplate restTemplate;
    private final UserActivityRepository userActivityRepository;

    public PodcastHistoryService(RestTemplate restTemplate, UserActivityRepository userActivityRepository) {
        this.restTemplate = restTemplate;
        this.userActivityRepository = userActivityRepository;
    }

    public Object registerPodcastHistory(String podurl, String userId, String accessToken) {
        String encodedUrl = URLEncoder.encode(podurl, StandardCharsets.UTF_8);
        String endpoint = podcastServiceUrl + "/pods/podcasts/episodes/getidandgenrefromurl/?url=" + encodedUrl;

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

        return userActivityRepository.save(activity);
    }
}
