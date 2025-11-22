package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.ActivityDTO;
import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.dto.MediaRequestDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {

    @Value("${music.service.url}")
    private String musicServiceUrl;

    @Value("${podcast.service.url}")
    private String podcastServiceUrl;

//    @Value("${video.service.url}")
//    private String videoServiceUrl;

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
//            else if (request.getMediaType().equalsIgnoreCase("VIDEO")) {
//                url = videoServiceUrl + "/videobyurl/" + request.getUrl();
//            }
            else {
                throw new RuntimeException("Unknown media type: " + request.getMediaType());
            }
            MediaResponseDTO mediaResponseDTO  = restTemplate.getForObject(url, MediaResponseDTO.class);
            UserActivity activity = new UserActivity();
            activity.setUserId(userId);
            activity.setMediaId(mediaResponseDTO.getMediaId());
            activity.setGenreName(mediaResponseDTO.getGenres());
            activity.setMediaType(request.getMediaType());
            activity.setPlayedAt(LocalDateTime.now());

            userActivityRepository.save(activity);
        }catch (Exception e) {
            throw new RuntimeException("Failed to register song activity", e);
        }
    }

    public List<UserActivity> getAllActivities() {
        List<UserActivity> activityList = userActivityRepository.findAll();
        if (activityList.isEmpty()) {
            throw new RuntimeException("There are no posts in the database");
        }
        return activityList;
    }

    public UserActivity setActivity(ActivityDTO activityDTO, String userId) {
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setMediaId(activityDTO.getMediaId());
        activity.setMediaType(activityDTO.getMediaType());
        activity.setGenreName(activityDTO.getGenreName());
        activity.setPlayedAt(LocalDateTime.now());

        return userActivityRepository.save(activity);
    }
}
