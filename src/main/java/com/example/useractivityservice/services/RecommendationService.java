package com.example.useractivityservice.services;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {

    List<UUID> getSongTopTenRecommendations(String userId);
    List<UUID> getPodTopTenRecommendations(String userId);
    List<UUID> getVideoTopTenRecommendations(String userId);


}
