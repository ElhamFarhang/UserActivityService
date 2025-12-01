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

//--------------------- Elham - VideoHistoryServiceUnitTest --------------
@ExtendWith(MockitoExtension.class)
class VideoHistoryServiceUnitTest {

    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private UserActivityRepository mockUserActivityRepository;
    @InjectMocks
    private VideoHistoryService mockVideoHistoryService;

    String url = "https://cdn.example.com/videos/physics-basics.mp4";
    String userId = "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
    String token = "access-token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mockVideoHistoryService, "videoServiceUrl", "http://edufyvideo2:8089");
    }

    @Test
    void registerVideoHistory_ShouldRegisterVideoHistoryInUserActivityRepository() {
        //Arrange
        MediaResponseDTO response = new MediaResponseDTO();
        response.setMediaId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        response.setGenres(List.of("Education", "Science"));
        ResponseEntity<MediaResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(MediaResponseDTO.class)))
                .thenReturn(responseEntity);
        when(mockUserActivityRepository.save(any(UserActivity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        //Act
        Object result = mockVideoHistoryService.registerVideoHistory(url, userId, token);
        //Assert
        UserActivity activity = (UserActivity) result;
        assertThat(activity.getUserId()).isEqualTo("1b2d25d9-0ac1-494d-8208-6b15b6430f3a");
        assertThat(activity.getMediaId()).isEqualTo(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        assertThat(activity.getMediaType()).isEqualTo(MediaType.VIDEO.name());
        assertThat(activity.getGenreName()).isEqualTo(List.of("Education", "Science"));
        assertThat(activity.getPlayedAt()).isNotNull();
        verify(mockUserActivityRepository, times(1)).save(any(UserActivity.class));
        verify(mockRestTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(MediaResponseDTO.class));
    }

    @Test
    void registerVideoHistory_ShouldThrowExceptionWhenHistoryRegistrationFails() {
        //Arrange
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(MediaResponseDTO.class)))
                .thenThrow(new RuntimeException("Service unreachable"));
        //Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mockVideoHistoryService.registerVideoHistory(url, userId, token));
        assertThat(ex.getMessage()).contains("Error while registering video history");
        verify(mockUserActivityRepository, never()).save(any());
    }
}