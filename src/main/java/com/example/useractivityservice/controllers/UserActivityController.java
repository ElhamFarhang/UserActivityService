package com.example.useractivityservice.controllers;

import com.example.useractivityservice.dto.MediaRequestDTO;
import com.example.useractivityservice.services.SongActivityService;
import com.example.useractivityservice.services.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user/activity")
public class UserActivityController {

    private final UserActivityService userActivityService;

    @Autowired
    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @PostMapping("/playmedia")
    public ResponseEntity<String> playMedia(@RequestBody MediaRequestDTO request, Principal principal) {
        String userId = principal.getName();
        userActivityService.regigterUserActivity(request, userId);
        return ResponseEntity.ok("Media activity saved");
    }
//
//    @GetMapping("/mostplayed/{userId}")
//    public List<Object[]> getMostPlayed(@PathVariable String userId) {
//        return userActivityService.getMostPlayedMedia(userId);
//    }

}
