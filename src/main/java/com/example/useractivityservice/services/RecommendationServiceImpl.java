package com.example.useractivityservice.services;

import com.example.useractivityservice.external.UserApiClient;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActivityRepository userActivityRepository;
    private final UserApiClient userApiClient;

    @Autowired
    public RecommendationServiceImpl(UserActivityRepository userActivityRepository, UserApiClient userApiClient) {
        this.userActivityRepository = userActivityRepository;
        this.userApiClient = userApiClient;
    }

    @Override
    public List<UUID> getRecommendations(String userId, String mediaType) {
        List<UUID> topTenRecommendations = new ArrayList<>();

        List<String> top3Genres = userActivityRepository.findMostFrequentGenresForUserId(
                userId, PageRequest.of(0, 3), mediaType, LocalDateTime.now().minusDays(100));

        List<String> allOtherGenres = userActivityRepository.findAllDistinctGenresByMediaType(mediaType);

        if (allOtherGenres.size() < 5) {
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

        String additionalGenre = "";
        Boolean moreTopGenres = false;

        topTenRecommendations.addAll(getTopMediaForGenre(topGenre,3, mediaType, userId));

        if (topTenRecommendations.size() == 3) {
            additionalGenre = topGenre;
            moreTopGenres = true;
        }
        topTenRecommendations.addAll(getTopMediaForGenre(secondGenre,3, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(thirdGenre,2, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(randomGenre1,1, mediaType, userId));
        topTenRecommendations.addAll(getTopMediaForGenre(randomGenre2,1, mediaType, userId));


        if (topTenRecommendations.size() < 10 ) {
            while (topTenRecommendations.size() < 10) {
                String randomGenre = allOtherGenres.get(random.nextInt(allOtherGenres.size())); //Kan upprepa genre av de som finns kvar.
                topTenRecommendations.addAll(getTopMediaForGenre(randomGenre,1, mediaType, userId));
            }
        }

        return topTenRecommendations;

    }

    private List<UUID> getTopMediaForGenre(String genre, int numberOfMediaType, String mediaType, String userId) {

        List<UUID> topMedia = userActivityRepository.findTopMediaTypeByGenreAndPeriod(genre, LocalDateTime.now().minusDays(30),
                PageRequest.of(0, numberOfMediaType), mediaType);

        List<UUID> played = userActivityRepository.findMediaPlayedByUser(userId, mediaType, LocalDateTime.MIN, genre);

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
            topMedia = userActivityRepository.findTopMediaTypeByGenreAndPeriod(genre, LocalDateTime.MIN,
                    PageRequest.of(0, numberOfMediaType), mediaType);

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

}
