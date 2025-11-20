package com.example.useractivityservice.services;

import java.util.List;
import java.util.UUID;

public class RecommendationServiceImpl implements RecommendationService {

    @Override
    public List<UUID> getSongTopTenRecommendations(UUID userId) {
        String topGenre;
        String secondGenre;
        String thirdGenre;

        String randomGenre1;
        String randomGenre2;


        return List.of();
    }

    @Override
    public List<UUID> getPodTopTenRecommendations(UUID userId) {
        return List.of();
    }

    @Override
    public List<UUID> getVideoTopTenRecommendations(UUID userId) {
        return List.of();
    }
}
