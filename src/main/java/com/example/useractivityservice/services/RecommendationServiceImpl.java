package com.example.useractivityservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecommendationServiceImpl implements RecommendationService {

    /*Det ska finnas en mikrotjänst som visar topp 10 rekommenderade media som en
användare inte lyssnat på utifrån användarens topp 3 genrer. Låt 20% av dessa
rekommendationer komma från andra genrer. (Här kan tumme ner komma väl till pass,
låt säga att man lyssnat på alla medier, då behöver man gå ett varv till, men undviker de
man redan valt bort) */




    @Override
    public List<UUID> getSongTopTenRecommendations(UUID userId) {
        String topGenre;
        String secondGenre;
        String thirdGenre;
        List<UUID> topTenRecommendations = new ArrayList<>();

        List<String> top3Genres = new ArrayList<>();

        top3Genres = findBy//TODO

        topGenre = top3Genres.get(0);
        secondGenre = top3Genres.get(1);
        thirdGenre = top3Genres.get(2);

        List<String> allOtherGenres = new ArrayList<>();

        String randomGenre1;
        String randomGenre2;

        List<UUID>


        return topTenRecommendations;
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
