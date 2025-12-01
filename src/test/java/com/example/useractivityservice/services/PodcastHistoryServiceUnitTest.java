package com.example.useractivityservice.services;


import com.example.useractivityservice.dto.MediaResponseDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.media.MediaType;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//--------------------- Elham - PodcastHistoryServiceUnitTest --------------
@ExtendWith(MockitoExtension.class)
class PodcastHistoryServiceUnitTest {

    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private UserActivityRepository mockUserActivityRepository;
    @InjectMocks
    private PodcastHistoryService mockPodcastHistoryService;

    String url = "https://cdn.edufy.se/podcasts/codetalk/ep1.mp3";
    String userId = "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
    String token = "access-token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mockPodcastHistoryService, "podcastServiceUrl", "http://edufypodservice:8080");
    }

    @Test
    void registerPodcastHistory_ShouldRegisterPodcastHistoryInUserActivityRepository() {
        //Arrange
        MediaResponseDTO response = new MediaResponseDTO();
        response.setMediaId(UUID.fromString("aaaa1111-1111-1111-1111-aaaaaaa1111"));
        response.setGenres(List.of("Tech"));
        ResponseEntity<MediaResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(MediaResponseDTO.class)))
                .thenReturn(responseEntity);
        when(mockUserActivityRepository.save(any(UserActivity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        //Act
        Object result = mockPodcastHistoryService.registerPodcastHistory(url, userId, token);
        //Assert
        UserActivity activity = (UserActivity) result;
        assertThat(activity.getUserId()).isEqualTo("1b2d25d9-0ac1-494d-8208-6b15b6430f3a");
        assertThat(activity.getMediaId()).isEqualTo(UUID.fromString("aaaa1111-1111-1111-1111-aaaaaaa1111"));
        assertThat(activity.getMediaType()).isEqualTo(MediaType.PODCAST.name());
        assertThat(activity.getGenreName()).isEqualTo(List.of("Tech"));
        assertThat(activity.getPlayedAt()).isNotNull();
        verify(mockUserActivityRepository, times(1)).save(any(UserActivity.class));
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(MediaResponseDTO.class));
    }

    @Test
    void registerPodcastHistory_ShouldThrowExceptionWhenHistoryRegistrationFails() {
        //Arrange
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(MediaResponseDTO.class)))
                .thenThrow(new RuntimeException("Service unreachable"));
        //Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mockPodcastHistoryService.registerPodcastHistory(url, userId, token));
        assertThat(ex.getMessage()).contains("Error while registering podcast history");
        verify(mockUserActivityRepository, never()).save(any());
    }
}