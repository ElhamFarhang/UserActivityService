/*
package com.example.useractivityservice.services;

import com.example.useractivityservice.configs.UserInfo;
import com.example.useractivityservice.dto.HistoryDTO;
import com.example.useractivityservice.dto.MostPlayedDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.external.UserApiClient;
import com.example.useractivityservice.mapper.DtoConverter;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/// ///////////////////////////////////ANNA////////////////////////////////////////
//@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private UserActivityRepository repoMock;
    @Mock
    private UserApiClient userApiMock;
    @Mock
    private UserInfo userInfoMock;


    private final DtoConverter dtoConverter = new DtoConverter();

    @InjectMocks
    private RecommendationServiceImpl service;

    private final String userId = "testUserId";
    private final String mediaType = "podcast";

    private MostPlayedDTO mostPlayedDTO;
    private HistoryDTO historyDTO;
    private UserActivity userActivity;

    private final UUID id1 = UUID.randomUUID();
    private final UUID id2 = UUID.randomUUID();
    private final UUID id3 = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new RecommendationServiceImpl(repoMock, userApiMock, userInfoMock, dtoConverter);

        userActivity = new UserActivity();

        when(userInfoMock.getUserId()).thenReturn(userId);
        when(userInfoMock.getRole()).thenReturn("TESTER");
    }

    // getRecommendations
    @Test
    void getRecommendations_ShouldGenerateTen_WhenGenresSufficient() {
        when(repoMock.findMostFrequentGenresForUserId(any(), any(), any(), eq(PageRequest.of(0,3))))
                .thenReturn(List.of("Sci-Fi", "Tech", "History"));

        when(repoMock.findAllDistinctGenresByMediaType(mediaType))
                .thenReturn(new ArrayList<>(List.of("Sci-Fi", "Tech", "History", "Drama", "Horror", "Comedy")));

        // force predictable returns
        when(repoMock.findTopMediaTypeByGenreAndPeriod(any(), any(), any(), any()))
                .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        when(repoMock.findMediaPlayedByUser(any(), any(), any(), any()))
                .thenReturn(List.of()); // nothing is filtered out

        List<UUID> result = service.getRecommendations(mediaType);

        assertNotNull(result);
        assertTrue(result.size() >= 10);
    }

    @Test
    void getRecommendations_ShouldThrow_WhenNotEnoughGenres() {
        when(repoMock.findAllDistinctGenresByMediaType(mediaType)).thenReturn(List.of("OnlyOne"));

        assertThrows(IllegalStateException.class,
                () -> service.getRecommendations(mediaType));
    }

    @Test
    void getRecommendations_ShouldFallbackToRandomGenres_WhenUserHasNoHistory() {
        when(repoMock.findMostFrequentGenresForUserId(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(repoMock.findAllDistinctGenresByMediaType(mediaType))
                .thenReturn(new ArrayList<>(List.of("Sci-Fi","Tech","Drama","Mystery","Music","Comedy")));

        when(repoMock.findTopMediaTypeByGenreAndPeriod(any(), any(), any(), any()))
                .thenReturn(List.of(id1,id2,id3));

        when(repoMock.findMediaPlayedByUser(any(), any(), any(), any()))
                .thenReturn(List.of());

        List<UUID> rec = service.getRecommendations(mediaType);
        assertTrue(rec.size() >= 5);
    }

    // getHistory
    @Test
    void getHistory_ShouldConvertAndReturnItems() {
        userActivity = new UserActivity();
        userActivity.setUserId(userId);
        userActivity.setMediaType(mediaType);
        userActivity.setGenreName(List.of("test"));


        when(repoMock.findByUserIdAndMediaTypeOrderByPlayedAtDesc(userId, mediaType))
                .thenReturn(List.of(userActivity));


        List<HistoryDTO> result = service.getHistory(mediaType);

        assertEquals(1, result.size());

    }


    // getHistoryBetween

    @Test
    void getHistoryBetween_ShouldReturnWithinRange() {
        UserActivity a = new UserActivity();
        HistoryDTO dto = new HistoryDTO();

        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(repoMock.findByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc(userId, start, end))
                .thenReturn(List.of(a));

        List<HistoryDTO> result = service.getHistoryBetween(start,end);

        assertEquals(1, result.size());

    }
    // getMostPlayedForAllByMediaType
    @Test
    void getMostPlayedForAllByMediaType_ShouldReturnRepositoryResult() {
        List<MostPlayedDTO> list = List.of(mostPlayedDTO);
        LocalDateTime start=LocalDateTime.now().minusDays(7);
        LocalDateTime end=LocalDateTime.now();

        when(repoMock.findMostPlayedMediaInPeriodByMediaType(mediaType,start,end, PageRequest.of(0,100)))
                .thenReturn(list);

        List<MostPlayedDTO> result = service.getMostPlayedForAllByMediaType(mediaType,start,end);

        assertEquals(list,result);
    }

    // getMostPlayedForUserAndMediaType
    @Test
    void getMostPlayedForUserAndMediaType_ShouldReturnList() {
        List<MostPlayedDTO> list = List.of();

        when(repoMock.findMostPlayedMediaByUserIdAndMediaType(userId,mediaType,PageRequest.of(0,100)))
                .thenReturn(list);

        assertEquals(list,service.getMostPlayedForUserAndMediaType(mediaType,null,null));
    }

    // getMostPlayedForUser
    @Test
    void getMostPlayedForUser_ShouldReturnList() {
        List<MostPlayedDTO> list = List.of();
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now();

        when(repoMock.findMostPlayedMediaByUserId(userId,start,end,PageRequest.of(0,100)))
                .thenReturn(list);

        assertEquals(list,service.getMostPlayedForUser(start,end));
    }
    //getTopMediaForGenre



}*/
