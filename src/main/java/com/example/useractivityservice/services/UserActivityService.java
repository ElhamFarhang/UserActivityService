package com.example.useractivityservice.services;

import com.example.useractivityservice.clients.PodcastClient;
import com.example.useractivityservice.clients.SongClient;
import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.dto.PlayMediaDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserActivityService {

    private final PodcastClient podcastClient;
    private final SongClient songClient;
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(PodcastClient podcastClient, SongClient songClient, UserActivityRepository userActivityRepository) {
        this.podcastClient = podcastClient;
        this.songClient = songClient;
        this.userActivityRepository = userActivityRepository;
    }

        public List<Object[]> getMostPlayedMedia(String userId) {
        return userActivityRepository.findMostPlayedByUser(userId);
    }

    public void regigterUserActivity(PlayMediaDTO request, String userId) {

        UUID mediaId;
        String genreName;

        if (request.getMediaType().equalsIgnoreCase("Podcast")) {
            MediaResponseDTO response = podcastClient.getEpisodeByUrl(request.getUrl());
            mediaId = response.getMediaId();
            genreName = response.getGenreName();

        } else if (request.getMediaType().equalsIgnoreCase("SONG")) {
            MediaResponseDTO response = songClient.getSongByUrl(request.getUrl());
            mediaId = response.getMediaId();
            genreName = response.getGenreName();

        } else {
            throw new RuntimeException("Unknown media type: " + request.getMediaType());
        }
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setMediaId(mediaId);
        activity.setGenreName(genreName);
        activity.setMediaType(request.getMediaType());

        userActivityRepository.save(activity);
    }

//    public String getUserIdFromToken() {
//        JwtAuthenticationToken token =
//                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//
//        return token.getToken().getClaimAsString("sub");
//    }
}
