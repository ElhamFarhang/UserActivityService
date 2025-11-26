package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.HistoryDTO;
import com.example.useractivityservice.dto.MostPlayedDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RecommendationService {


    List<UUID> getRecommendations(String mediaType);

    List<HistoryDTO> getHistory(String mediaType);

    List<HistoryDTO> getHistoryBetween(LocalDateTime start, LocalDateTime end);

    List<MostPlayedDTO> getMostPlayedForAllByMediaType(String mediaType, LocalDateTime start, LocalDateTime end);

    List<MostPlayedDTO> getMostPlayedForUserAndMediaType(String mediaType, LocalDateTime start, LocalDateTime end);

    List<MostPlayedDTO> getMostPlayedForUser(LocalDateTime start, LocalDateTime end);

}
