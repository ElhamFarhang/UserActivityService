package com.example.useractivityservice.controllers;

import com.example.useractivityservice.dto.PlayMediaDTO;
import com.example.useractivityservice.services.UserActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/activity")
public class UserActivityController {

    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @PostMapping("/playmedia")
    public ResponseEntity<String> playMedia(@RequestBody PlayMediaDTO request, Principal principal) {
        String userId = principal.getName();
        userActivityService.regigterUserActivity(request, userId);
        return ResponseEntity.ok("Media activity saved");
    }

    @GetMapping("/mostplayed/{userId}")
    public List<Object[]> getMostPlayed(@PathVariable String userId) {
        return userActivityService.getMostPlayedMedia(userId);
    }
}
