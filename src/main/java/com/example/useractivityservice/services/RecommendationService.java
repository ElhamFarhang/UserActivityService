package com.example.useractivityservice.services;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {


    List<UUID> getRecommendations(String userId, String mediaType);

}
