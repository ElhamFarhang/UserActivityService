package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.dto.MediaRequestDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserActivityService {

    @Value("${music.service.url}")
    private String musicServiceUrl;

    @Value("${podcast.service.url}")
    private String podcastServiceUrl;

    @Value("${video.service.url}")
    private String videoServiceUrl;

    private final RestTemplate restTemplate;
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(RestTemplate restTemplate, UserActivityRepository userActivityRepository) {
        this.restTemplate = restTemplate;
        this.userActivityRepository = userActivityRepository;
    }

    //        public List<Object[]> getMostPlayedMedia(String userId) {
//        return userActivityRepository.findMostPlayedByUser(userId);
//    }

    public void regigterUserActivity(MediaRequestDTO request, String userId) {

        String mediaTypa = request.getMediaType();
        String url;

        try{
            if (mediaTypa.equalsIgnoreCase("PODCAST")) {
                url = podcastServiceUrl + "/getidandgenrefromurl/" + request.getUrl();
            }
            else if (request.getMediaType().equalsIgnoreCase("SONG")) {
                url = musicServiceUrl + "/songbyurl/" + request.getUrl();
            }
            else if (request.getMediaType().equalsIgnoreCase("VIDEO")) {
                url = videoServiceUrl + "/videobyurl/" + request.getUrl();
            }
            else {
                throw new RuntimeException("Unknown media type: " + request.getMediaType());
            }
            MediaResponseDTO mediaResponseDTO  = restTemplate.getForObject(url, MediaResponseDTO.class);
            UserActivity activity = new UserActivity();
            activity.setUserId(userId);
            activity.setMediaId(mediaResponseDTO.getMediaId());
            activity.setGenreName(mediaResponseDTO.getGenres());
            activity.setMediaType(request.getMediaType());

            userActivityRepository.save(activity);
        }catch (Exception e) {
            throw new RuntimeException("Failed to register song activity", e);
        }
    }



}
