package com.example.useractivityservice.services;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {

    List<UUID> getSongTopTenRecommendations(UUID userId);
    List<UUID> getPodTopTenRecommendations(UUID userId);
    List<UUID> getVideoTopTenRecommendations(UUID userId);


}
