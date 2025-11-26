package com.example.useractivityservice.controllers;


import com.example.useractivityservice.dto.HistoryDTO;
import com.example.useractivityservice.dto.MostPlayedDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.services.RecommendationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/// ///////////////////////////////////ANNA////////////////////////////////////////
@RestController
@RequestMapping("/activity/stats")
public class RecomendationsHistoryController {


    private final RecommendationServiceImpl recommendationService;

    @Autowired
    public RecomendationsHistoryController(RecommendationServiceImpl recommendationService) {
        this.recommendationService = recommendationService;
    }


    @GetMapping("/getRecommendations/{mediaType}")
    public ResponseEntity<List<UUID>> getRecommendations(@PathVariable String mediaType) {
        return ResponseEntity.ok(recommendationService.getRecommendations(mediaType));
    }


    @GetMapping("/getHistory/{mediaType}")
    public ResponseEntity<List<HistoryDTO>> getUserHistoryForMediaType(@PathVariable String mediaType) {
        return ResponseEntity.ok(recommendationService.getHistory(mediaType));
    }


    @GetMapping("/getAllHistoryBetween")
    public ResponseEntity<List<HistoryDTO>> getUserHistoryBetween(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(recommendationService.getHistoryBetween(start, end));
    }


    @GetMapping("/allusersmostplayed/{mediaType}")
    public ResponseEntity<List<MostPlayedDTO>> getMostPlayedByAll(@PathVariable String mediaType,
                                                            @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(recommendationService.getMostPlayedForAllByMediaType(mediaType,start, end));
    }

    @GetMapping("/usermostplayed/{mediaType}")
    public ResponseEntity<List<MostPlayedDTO>> getMostPlayedByUserAndMediaType(@PathVariable String mediaType,
                                                            @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(recommendationService.getMostPlayedForUserAndMediaType(mediaType,start, end));
    }

    @GetMapping("/usermostplayedAllTypes")
    public ResponseEntity<List<MostPlayedDTO>> getMostPlayedByUser(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(recommendationService.getMostPlayedForUser(start, end));
    }


}
