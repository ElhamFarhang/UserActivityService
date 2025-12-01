package com.example.useractivityservice.services;

import com.example.useractivityservice.configs.UserInfo;
import com.example.useractivityservice.dto.HistoryDTO;
import com.example.useractivityservice.dto.MostPlayedDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.external.UserApiClient;
import com.example.useractivityservice.mapper.DtoConverter;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/// ///////////////////////////////////ANNA////////////////////////////////////////
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActivityRepository userActivityRepository;
    private final UserApiClient userApiClient;
    private final UserInfo userInfo;
    private final DtoConverter dtoConverter;
    private static final Logger FUNCTIONALITY_LOGGER = LogManager.getLogger("functionality");

    @Autowired
    public RecommendationServiceImpl(UserActivityRepository userActivityRepository, UserApiClient userApiClient,
                                     UserInfo userInfo, DtoConverter dtoConverter) {
        this.userActivityRepository = userActivityRepository;
        this.userApiClient = userApiClient;
        this.userInfo = userInfo;
        this.dtoConverter = dtoConverter;
    }


    @Override
    public List<UUID> getRecommendations(String mediaType) {
        String userId = userInfo.getUserId();
        List<UUID> topTenRecommendations = new ArrayList<>();

        List<UserActivity> activities = userActivityRepository.findByUserIdAndMediaTypeAndPlayedAtAfter(
                        userId, mediaType, LocalDateTime.now().minusDays(100));

        Map<String, Long> genreCount = new HashMap<>();
        for (UserActivity ua : activities) {
            for (String genre : ua.getGenreName()) {
                genreCount.put(genre, genreCount.getOrDefault(genre, 0L) + 1);
            }
        }

        List<String> top3Genres = genreCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();


        List<UserActivity> allActivities = userActivityRepository.findByMediaType(mediaType);
        Set<String> allOtherGenresSet = new HashSet<>();
        for (UserActivity ua : allActivities) {
            allOtherGenresSet.addAll(ua.getGenreName());
        }

        List<String> allOtherGenres = new ArrayList<>(allOtherGenresSet);

        if (allOtherGenres.size() < 5) {
            FUNCTIONALITY_LOGGER.warn("{} failed to retrieve genres for {}-recommendations for userId: {}", userInfo.getRole(), mediaType, userInfo.getUserId());
            throw new IllegalStateException("Not enough genres available for recommendations");
        }

        Random random = new Random();

        String topGenre;
        String secondGenre;
        String thirdGenre;

        if (top3Genres.size() >= 3) {
            topGenre = top3Genres.get(0);
            secondGenre = top3Genres.get(1);
            thirdGenre = top3Genres.get(2);
        } else if (top3Genres.size() == 2) {
            topGenre = top3Genres.get(0);
            secondGenre = top3Genres.get(1);
            thirdGenre = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
        } else if (top3Genres.size() == 1) {
            topGenre = top3Genres.get(0);
            secondGenre = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
            thirdGenre = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
        } else {
            topGenre = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
            secondGenre = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
            thirdGenre = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
        }

        allOtherGenres.remove(topGenre);
        allOtherGenres.remove(secondGenre);
        allOtherGenres.remove(thirdGenre);
        String randomGenre1 = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
        String randomGenre2 = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));


        topTenRecommendations.addAll(getTopMediaForGenre(topGenre,3, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(secondGenre,3, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(thirdGenre,2, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(randomGenre1,1, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(randomGenre2,1, mediaType, userId));


        if (topTenRecommendations.size() < 10 && !allOtherGenres.isEmpty() ) {
            int counter = 0;
            while (topTenRecommendations.size() < 10 && counter < 100) {
                counter++;
                String randomGenre = allOtherGenres.get(random.nextInt(allOtherGenres.size())); //Kan upprepa genre av de som finns kvar.
                topTenRecommendations.addAll(getTopMediaForGenre(randomGenre,1, mediaType, userId));
            }
        }

        if (topTenRecommendations.size() < 10) {
            FUNCTIONALITY_LOGGER.warn("{}-recommendations for userId: '{}', retrieved by {}, but failed to get the full amount", mediaType, userInfo.getUserId(), userInfo.getRole());

        } else {
            FUNCTIONALITY_LOGGER.info("{}-recommendations for userId: '{}', retrieved by {}", mediaType, userInfo.getUserId(), userInfo.getRole());
        }

        return topTenRecommendations;
    }

    private List<UUID> getTopMediaForGenre(String genre, int numberOfMediaType, String mediaType, String userId) {
        List<UUID> topMedia = findTopMediaByGenreInRange(genre, LocalDateTime.now().minusDays(30),
                LocalDateTime.now(), 100, mediaType);
        List<UserActivity> activities = userActivityRepository.findByUserIdAndMediaTypeAndPlayedAtAfter(
                userId, mediaType, LocalDateTime.of(1970,1,1,0,0));
        List<UUID> played = activities.stream()
                .filter(ua -> ua.getGenreName().contains(genre))
                .map(UserActivity::getMediaId)
                .distinct()
                .toList();
        List<UUID> recommendations = new ArrayList<>();
        for (UUID mediaId : topMedia) {
            if (!played.contains(mediaId)) {
                recommendations.add(mediaId);
            }
            if (recommendations.size() >= numberOfMediaType) {
                break;
            }
        }

        if (recommendations.size() < numberOfMediaType) {
            List<UserActivity> topMediaActivities = userActivityRepository
                    .findByMediaTypeAndPlayedAtAfter(mediaType, LocalDateTime.now().minusYears(1));

            topMedia = topMediaActivities.stream()
                    .filter(ua -> ua.getGenreName().contains(genre))
                    .collect(Collectors.groupingBy(UserActivity::getMediaId, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                    .limit(numberOfMediaType)
                    .map(Map.Entry::getKey)
                    .toList();

            for (UUID mediaId : topMedia) {
                if (!played.contains(mediaId)) {
                    recommendations.add(mediaId);
                }
                if (recommendations.size() >= numberOfMediaType) {
                    break;
                }
            }
            if (recommendations.size() < numberOfMediaType) {

                List<UUID> dislikedMedia = userApiClient.getLikedOrDislikedMediaList(userId, mediaType, false);
                for (UUID mediaId : topMedia) {
                    if (!dislikedMedia.contains(mediaId)) {
                        recommendations.add(mediaId);
                    }
                    topMedia.remove(mediaId);
                    if (recommendations.size() >= numberOfMediaType) {
                        break;
                    }
                }
            }
            if (recommendations.size() < numberOfMediaType) {
                List<UUID> likedMedia = userApiClient.getLikedOrDislikedMediaList(userId, mediaType, true);
                for (UUID mediaId : topMedia) {
                    if (!likedMedia.contains(mediaId)) {
                        recommendations.add(mediaId);
                    }
                    topMedia.remove(mediaId);
                    if (recommendations.size() >= numberOfMediaType) {
                        break;
                    }
                }
            }
            if (recommendations.size() < numberOfMediaType) {
                for (UUID mediaId : topMedia) {
                    recommendations.add(mediaId);
                    if (recommendations.size() >= numberOfMediaType) {
                        break;
                    }
                }
            }
        }
        return recommendations;
    }

    private List<UUID> findTopMediaByGenreInRange(String genre, LocalDateTime start, LocalDateTime end, int limit, String mediaType) {
        List<UserActivity> mediaPlayed = userActivityRepository.findByMediaTypeAndPlayedAtBetween(mediaType, start, end);

        Map<UUID, Long> counts = mediaPlayed.stream()
                .filter(ua -> ua.getGenreName() != null && ua.getGenreName().contains(genre))
                .collect(Collectors.groupingBy(UserActivity::getMediaId, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    //----------------History
    @Override
    public List<HistoryDTO> getHistory(String mediaType) {
        List<UserActivity> historyByMediaType = userActivityRepository.findByUserIdAndMediaTypeOrderByPlayedAtDesc(userInfo.getUserId(), mediaType);

        List<HistoryDTO> historyDTOs = new ArrayList<>();

        for (UserActivity userActivity : historyByMediaType) {
            HistoryDTO historyDTO = dtoConverter.makeHistoryDTO(userActivity);
            historyDTOs.add(historyDTO);
        }
        FUNCTIONALITY_LOGGER.info("{}-history for userId: '{}', retrieved by {}", mediaType, userInfo.getUserId(), userInfo.getRole());
        return historyDTOs;
    }

    @Override
    public List<HistoryDTO> getHistoryBetween(LocalDateTime start, LocalDateTime end) {
        List<UserActivity> historyByTime = userActivityRepository.findByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc(userInfo.getUserId(), start, end);

        List<HistoryDTO> historyDTOs = new ArrayList<>();

        for (UserActivity userActivity : historyByTime) {
            HistoryDTO historyDTO = dtoConverter.makeHistoryDTO(userActivity);
            historyDTOs.add(historyDTO);
        }
        FUNCTIONALITY_LOGGER.info("History for {}---{} for userId: '{}' retrieved by {}", start, end, userInfo.getUserId(), userInfo.getRole());
        return historyDTOs;
    }

    //----------------Most played

    @Override
    public List<MostPlayedDTO> getMostPlayedForAllByMediaType(String mediaType, LocalDateTime start, LocalDateTime end) {
        List<MostPlayedDTO> mostPlayed = userActivityRepository.findMostPlayedMediaInPeriodByMediaType(mediaType, start,
                                                                    end, PageRequest.of(0, 100));

        FUNCTIONALITY_LOGGER.info("Most played {}s for all users, retrieved by {}", mediaType, userInfo.getRole());
        return mostPlayed;
    }

    @Override
    public List<MostPlayedDTO> getMostPlayedForUserAndMediaType(String mediaType, LocalDateTime start, LocalDateTime end) {
        List<MostPlayedDTO> mostPlayed = userActivityRepository.findMostPlayedMediaByUserIdAndMediaType(userInfo.getUserId(),
                                                                    mediaType, PageRequest.of(0, 100));

        FUNCTIONALITY_LOGGER.info("Most played {}s for userId: '{}', retrieved by {}", mediaType, userInfo.getUserId(), userInfo.getRole());
        return mostPlayed;
    }

    @Override
    public List<MostPlayedDTO> getMostPlayedForUser(LocalDateTime start, LocalDateTime end) {
        List<MostPlayedDTO> mostPlayed = userActivityRepository.findMostPlayedMediaByUserId(userInfo.getUserId(), start,
                                                                    end, PageRequest.of(0, 100));

        FUNCTIONALITY_LOGGER.info("Most played by userId: '{}' for {}---{}  retrieved by {}", userInfo.getUserId(), start, end,   userInfo.getRole());
        return mostPlayed;
    }


}
