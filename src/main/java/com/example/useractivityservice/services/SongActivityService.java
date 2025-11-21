package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.MediaRequestDTO;
import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SongActivityService {
    @Value("${music.service.url}")
    private String musicServiceUrl;

    private final UserActivityRepository userActivityRepository;
    private final RestTemplate restTemplate;

    public SongActivityService(UserActivityRepository userActivityRepository, RestTemplate restTemplate) {
        this.userActivityRepository = userActivityRepository;
        this.restTemplate = restTemplate;
    }

    public void registerMusicHistory(MediaRequestDTO request, String userId) {

        try{
            String url = musicServiceUrl + "/songbyurl/" + request.getUrl();
            MediaResponseDTO mediaResponseDTO  = restTemplate.getForObject(url, MediaResponseDTO.class);

            if (mediaResponseDTO == null) {
                throw new RuntimeException("Song not found in SongService");
            }

            UserActivity userActivity = new UserActivity();
            userActivity.setUserId(userId);
            userActivity.setMediaId(mediaResponseDTO.getMediaId());
            userActivity.setMediaType("SONG");
            userActivity.setGenreName(mediaResponseDTO.getGenres());

            userActivityRepository.save(userActivity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register song activity", e);
        }

    }
}
