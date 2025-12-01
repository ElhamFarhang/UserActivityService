package com.example.useractivityservice.controllers;

import com.example.useractivityservice.dto.ActivityDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.services.MusicHistoryService;
import com.example.useractivityservice.services.PodcastHistoryService;
import com.example.useractivityservice.services.UserActivityService;
import com.example.useractivityservice.services.VideoHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//--------------------- Elham - UserActivityController --------------
@RestController
@RequestMapping("/activity/media")
public class UserActivityController {

    private final UserActivityService userActivityService;
    private final MusicHistoryService musicHistoryService;
    private final PodcastHistoryService podcastHistoryService;
    private final VideoHistoryService videoHistoryService;

    @Autowired
    public UserActivityController(UserActivityService userActivityService, MusicHistoryService musicHistoryService, PodcastHistoryService podcastHistoryService, VideoHistoryService videoHistoryService) {
        this.userActivityService = userActivityService;
        this.musicHistoryService = musicHistoryService;
        this.podcastHistoryService = podcastHistoryService;
        this.videoHistoryService = videoHistoryService;
    }

    @PostMapping("/playmusic")
    public ResponseEntity<?> playMusic(@RequestParam String musicurl, @AuthenticationPrincipal Jwt jwt, @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = jwt.getSubject();
            String accessToken = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(musicHistoryService.registerMusicHistory(musicurl, userId, accessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/playpodcast")
    public ResponseEntity<?> playPodcast(@RequestParam String podurl, @AuthenticationPrincipal Jwt jwt, @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = jwt.getSubject();
            String accessToken = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(podcastHistoryService.registerPodcastHistory(podurl, userId, accessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/playvideo")
    public ResponseEntity<?> playvideo(@RequestParam String videourl, @AuthenticationPrincipal Jwt jwt, @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = jwt.getSubject();
            String accessToken = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(videoHistoryService.registerVideoHistory(videourl, userId, accessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/activities")
    public ResponseEntity<List<UserActivity>> getAllActivities() {
        return ResponseEntity.ok(userActivityService.getAllActivities());
    }

    @PostMapping("/addactivity")
    public ResponseEntity<String> addActivity(@RequestBody ActivityDTO activityDTO, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        UserActivity activity = userActivityService.setActivity(activityDTO, userId);
        return ResponseEntity.ok("Activity saved " + '\n' + activity);
    }
}
