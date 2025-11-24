package com.example.useractivityservice.services;

import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RecommendationServiceImpl implements RecommendationService {

    /*Det ska finnas en mikrotjänst som visar topp 10 rekommenderade media som en
användare inte lyssnat på utifrån användarens topp 3 genrer. Låt 20% av dessa
rekommendationer komma från andra genrer. (Här kan tumme ner komma väl till pass,
låt säga att man lyssnat på alla medier, då behöver man gå ett varv till, men undviker de
man redan valt bort) */


    private final UserActivityRepository userActivityRepository;

    public RecommendationServiceImpl(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public List<UUID> getSongTopTenRecommendations(String userId) { //TODO Ändra till UUID?
        List<UUID> topTenRecommendations = new ArrayList<>();

        List<String> top3Genres = userActivityRepository.findMostFrequentGenresForUserId(  //TODO lägga till date?
                userId, PageRequest.of(0, 3), "song");

        String topGenre = top3Genres.get(0);
        String secondGenre = top3Genres.get(1);
        String thirdGenre = top3Genres.get(2);

        List<String> allOtherGenres = userActivityRepository.findAllDistinctGenresByMediaType("song");

        allOtherGenres.remove(topGenre);
        allOtherGenres.remove(secondGenre);
        allOtherGenres.remove(thirdGenre);

        Random random = new Random();

        String randomGenre1 = allOtherGenres.remove(random.nextInt(allOtherGenres.size()));
        String randomGenre2 = allOtherGenres.get(random.nextInt(allOtherGenres.size()));

        topTenRecommendations.addAll(getTopSongsForGenre(topGenre,3));
        topTenRecommendations.addAll(getTopSongsForGenre(secondGenre,3));
        topTenRecommendations.addAll(getTopSongsForGenre(thirdGenre,2));
        topTenRecommendations.addAll(getTopSongsForGenre(randomGenre1,1));
        topTenRecommendations.addAll(getTopSongsForGenre(randomGenre2,1));

        return topTenRecommendations;
    }

    private List<UUID> getTopSongsForGenre(String genre, int numberOfSongs) {

        List<UUID> topSongs = userActivityRepository.findTopMediaTypeByGenreAndPeriod(
                genre,
                LocalDateTime.now().minusDays(100),
                PageRequest.of(0, numberOfSongs) , "song");

        if (topSongs.size() < numberOfSongs) {          //TODO se till att numberOfSongs alltid är det som returneras
            getRandomNotDislikedMediaForGenre(genre, numberOfSongs-topSongs.size());
        }

        return topSongs;
    }

    private List<UUID> getRandomNotDislikedMediaForGenre(String genre, int numberOfSongs) {
        List<UUID> randomNotDislikedMedia = new ArrayList<>();

        return randomNotDislikedMedia;

    }

    @Override
    public List<UUID> getPodTopTenRecommendations(String userId) {
        return List.of();
    }

    @Override
    public List<UUID> getVideoTopTenRecommendations(String userId) {
        return List.of();
    }
}
