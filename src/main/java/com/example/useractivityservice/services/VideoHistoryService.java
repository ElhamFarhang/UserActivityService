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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

//--------------------- Elham - VideoHistoryService --------------
@Service
public class VideoHistoryService {

    @Value("${video.service.url}")
    private String videoServiceUrl;

    private final RestTemplate restTemplate;
    private final UserActivityRepository userActivityRepository;

    public VideoHistoryService(RestTemplate restTemplate, UserActivityRepository userActivityRepository) {
        this.restTemplate = restTemplate;
        this.userActivityRepository = userActivityRepository;
    }

    public Object registerMusicHistory(String videourl, String userId, String accessToken) {
        String endpoint = UriComponentsBuilder
                .fromHttpUrl(videoServiceUrl + "/videos/getidandgenrefromurl/?url=")
                .queryParam("url", videourl)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<MediaResponseDTO> response = restTemplate.exchange(endpoint , HttpMethod.GET, entity, MediaResponseDTO.class);
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setMediaId(response.getBody().getMediaId());
        activity.setMediaType(MediaType.VIDEO.name());
        activity.setGenreName(response.getBody().getGenres());
        activity.setPlayedAt(LocalDateTime.now());

        return userActivityRepository.save(activity);
    }
}
