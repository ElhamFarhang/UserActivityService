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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/// ///////////////////////////////////ANNA////////////////////////////////////////
@ActiveProfiles("test")
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

    private final String userId = "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
    private final String mediaType = "PODCAST";

    private MostPlayedDTO mostPlayedDTO;
    private HistoryDTO historyDTO;
    private UserActivity userActivity;

    private final UUID id1 = UUID.randomUUID();
    private final UUID id2 = UUID.randomUUID();
    private final UUID id3 = UUID.randomUUID();
    private final UUID id4 = UUID.randomUUID();
    private final LocalDateTime start = LocalDateTime.now().minusDays(2);
    private final LocalDateTime end = LocalDateTime.now();

    @BeforeEach
    void setup() {
        service = new RecommendationServiceImpl(repoMock, userApiMock, userInfoMock, dtoConverter);

        userActivity = new UserActivity();
        userActivity.setMediaType(mediaType);
        userActivity.setMediaId(id2);
        userActivity.setUserId(userId);
        userActivity.setPlayedAt(LocalDateTime.now().minusDays(1));
        userActivity.setGenreName(List.of("rock"));

        mostPlayedDTO = new MostPlayedDTO(id1, 2L, mediaType);


        when(userInfoMock.getRole()).thenReturn("TESTER");
    }

    // getRecommendations
    @Test
    void getRecommendations_ShouldThrowIfNotEnoughGenres() {
        when(userInfoMock.getUserId()).thenReturn(userId);

        when(repoMock.findByUserIdAndMediaTypeAndPlayedAtAfter(any(), any(), any()))
                .thenReturn(List.of(userActivity));

        when(repoMock.findByMediaType(mediaType))
                .thenReturn(List.of(userActivity));

        assertThrows(IllegalStateException.class,
                () -> service.getRecommendations(mediaType));
    }

    @Test
    void getRecommendations_ShouldUseTop3GenresAndReturn10Items() {
        when(userInfoMock.getUserId()).thenReturn(userId);

        UserActivity ua1 = new UserActivity();
        ua1.setUserId(userId);
        ua1.setMediaType(mediaType);
        ua1.setPlayedAt(LocalDateTime.now().minusDays(5));
        ua1.setGenreName(List.of("rock", "pop"));

        UserActivity ua2 = new UserActivity();
        ua2.setUserId(userId);
        ua2.setMediaType(mediaType);
        ua2.setPlayedAt(LocalDateTime.now().minusDays(20));
        ua2.setGenreName(List.of("jazz"));

        when(repoMock.findByUserIdAndMediaTypeAndPlayedAtAfter(any(), any(), any()))
                .thenReturn(List.of(ua1, ua2));

        UserActivity g1 = new UserActivity();
        g1.setGenreName(List.of("rock"));
        UserActivity g2 = new UserActivity();
        g2.setGenreName(List.of("pop"));
        UserActivity g3 = new UserActivity();
        g3.setGenreName(List.of("jazz"));
        UserActivity g4 = new UserActivity();
        g4.setGenreName(List.of("metal"));
        UserActivity g5 = new UserActivity();
        g5.setGenreName(List.of("blues"));

        when(repoMock.findByMediaType(mediaType))
                .thenReturn(List.of(g1, g2, g3, g4, g5));

        List<UUID> fake = List.of(UUID.randomUUID(), UUID.randomUUID());
        RecommendationServiceImpl spyService = Mockito.spy(service);

        doReturn(fake).when(spyService)
                .getTopMediaForGenre(any(), anyInt(), eq(mediaType), eq(userId));

        List<UUID> res = spyService.getRecommendations(mediaType);

        assertEquals(10, res.size());
    }

    @Test
    void getRecommendations_ShouldHandleLessThan3UserGenres() {
        when(userInfoMock.getUserId()).thenReturn(userId);

        UserActivity ua = new UserActivity();
        ua.setUserId(userId);
        ua.setMediaType(mediaType);
        ua.setPlayedAt(LocalDateTime.now().minusDays(5));
        ua.setGenreName(List.of("rock"));
        when(repoMock.findByUserIdAndMediaTypeAndPlayedAtAfter(any(), any(), any()))
                .thenReturn(List.of(ua));


        UserActivity g1 = new UserActivity(); g1.setGenreName(List.of("rock"));
        UserActivity g2 = new UserActivity(); g2.setGenreName(List.of("pop"));
        UserActivity g3 = new UserActivity(); g3.setGenreName(List.of("jazz"));
        UserActivity g4 = new UserActivity(); g4.setGenreName(List.of("metal"));
        UserActivity g5 = new UserActivity(); g5.setGenreName(List.of("blues"));

        when(repoMock.findByMediaType(mediaType))
                .thenReturn(List.of(g1, g2, g3, g4, g5));

        RecommendationServiceImpl spyService = Mockito.spy(service);
        doReturn(List.of(UUID.randomUUID()))
                .when(spyService)
                .getTopMediaForGenre(any(), anyInt(), eq(mediaType), eq(userId));

        List<UUID> res = spyService.getRecommendations(mediaType);

        assertFalse(res.isEmpty());
    }

    @Test
    void getRecommendations_ShouldNotFailWhenGetTopMediaReturnsEmpty() {
        when(userInfoMock.getUserId()).thenReturn(userId);


        UserActivity ua = new UserActivity();
        ua.setUserId(userId);
        ua.setMediaType(mediaType);
        ua.setPlayedAt(LocalDateTime.now().minusDays(5));
        ua.setGenreName(List.of("rock", "pop", "jazz"));
        when(repoMock.findByUserIdAndMediaTypeAndPlayedAtAfter(any(), any(), any()))
                .thenReturn(List.of(ua));

        // DB genres
        List<UserActivity> allG = new ArrayList<>();
        for (String g : List.of("rock", "pop", "jazz", "metal", "blues"))
        {
            UserActivity x = new UserActivity();
            x.setGenreName(List.of(g));
            allG.add(x);
        }
        when(repoMock.findByMediaType(mediaType))
                .thenReturn(allG);

        RecommendationServiceImpl spyService = Mockito.spy(service);
        doReturn(Collections.emptyList())
                .when(spyService)
                .getTopMediaForGenre(any(), anyInt(), eq(mediaType), eq(userId));

        List<UUID> res = spyService.getRecommendations(mediaType);

        assertNotNull(res);
    }
    //getTopMediaForGenre

    //findTopMediaByGenreInRange




    // getHistory
    @Test
    void getHistory_ShouldConvertAndReturnItems() {
        when(userInfoMock.getUserId()).thenReturn(userId);
        when(repoMock.findByUserIdAndMediaTypeOrderByPlayedAtDesc(userId, mediaType))
                .thenReturn(List.of(userActivity));


        List<HistoryDTO> result = service.getHistory(mediaType);

        assertEquals(1, result.size());
        assertEquals(userActivity.getMediaType(), result.get(0).getMediaType());
        assertEquals(userActivity.getMediaId(), result.get(0).getMediaId());
        assertEquals(userActivity.getPlayedAt(), result.get(0).getPlayedAt());

    }


    // getHistoryBetween
    @Test
    void getHistoryBetween_ShouldReturnWithinRange() {
        when(userInfoMock.getUserId()).thenReturn(userId);

        when(repoMock.findByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc(userId, start, end))
                .thenReturn(List.of(userActivity));

        List<HistoryDTO> result = service.getHistoryBetween(start,end);

        assertEquals(1, result.size());
        assertEquals(userActivity.getMediaType(), result.get(0).getMediaType());
        assertEquals(userActivity.getMediaId(), result.get(0).getMediaId());
        assertEquals(userActivity.getPlayedAt(), result.get(0).getPlayedAt());

    }

    // getMostPlayedForAllByMediaType
    @Test
    void getMostPlayedForAllByMediaType_ShouldReturnRepositoryResult() {
        List<MostPlayedDTO> list = List.of(mostPlayedDTO);

        when(repoMock.findMostPlayedMediaInPeriodByMediaType(mediaType,start,end, PageRequest.of(0,100)))
                .thenReturn(list);

        List<MostPlayedDTO> result = service.getMostPlayedForAllByMediaType(mediaType,start,end);

        assertEquals(list,result);
    }

    // getMostPlayedForUserAndMediaType
    @Test
    void getMostPlayedForUserAndMediaType_ShouldReturnList() {
        List<MostPlayedDTO> list = List.of(mostPlayedDTO);

        when(userInfoMock.getUserId()).thenReturn(userId);
        when(repoMock.findMostPlayedMediaByUserIdAndMediaType(userId,mediaType,PageRequest.of(0,100)))
                .thenReturn(list);

        List<MostPlayedDTO> result = service.getMostPlayedForUserAndMediaType(mediaType,start,end);

        assertEquals(list, result);
    }

    // getMostPlayedForUser
    @Test
    void getMostPlayedForUser_ShouldReturnList() {
        List<MostPlayedDTO> list = List.of(mostPlayedDTO);

        when(userInfoMock.getUserId()).thenReturn(userId);
        when(repoMock.findMostPlayedMediaByUserId(userId,start,end,PageRequest.of(0,100)))
                .thenReturn(list);

        List<MostPlayedDTO> result = service.getMostPlayedForUser(start,end);

        assertEquals(list,result);
    }

}
