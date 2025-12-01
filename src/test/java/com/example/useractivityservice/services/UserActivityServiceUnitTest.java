package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.ActivityDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//--------------------- Elham - UserActivityServiceUnitTest --------------
@ExtendWith(MockitoExtension.class)
class UserActivityServiceUnitTest {

    @Mock
    private UserActivityRepository mockUserActivityRepository;
    @InjectMocks
    private UserActivityService mockUserActivityService;

    private UserActivity activity = new UserActivity();

    @BeforeEach
    void setUp() {
        activity = new UserActivity(1L, "1b2d25d9-0ac1-494d-8208-6b15b6430f3a",
                UUID.fromString("aaaa1111-1111-1111-1111-aaaaaaa1111"),
                "PODCAST", List.of("Tech"), LocalDateTime.parse("2025-11-12T10:00:00"));
    }

    @Test
    void getAllActivities_ShouldReturnListOfAllActivities() {
        //Arrange
        when(mockUserActivityRepository.findAll()).thenReturn(List.of(activity));
        //Act
        List<UserActivity> result = mockUserActivityService.getAllActivities();
        //Assert
        assertFalse(result.isEmpty());
        assertThat(result.get(0).getUserId()).isEqualTo("1b2d25d9-0ac1-494d-8208-6b15b6430f3a");

        assertThat(result.get(0).getMediaId()).isEqualTo(UUID.fromString("aaaa1111-1111-1111-1111-aaaaaaa1111"));
        assertThat(result.get(0).getMediaType()).isEqualTo("PODCAST");
        assertThat(result.get(0).getGenreName()).isEqualTo(List.of("Tech"));
        assertEquals(1, result.size());
    }

    @Test
    void getAllActivities_ShouldThrowExceptionWhenListIsEmpty() {
        //Arrange
        when(mockUserActivityRepository.findAll()).thenReturn(List.of());
        //Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> mockUserActivityService.getAllActivities());
        assertTrue(ex.getMessage().contains("There are no posts in the database"));
    }

    @Test
    void getAllActivities_ShouldThrowExceptionWhenRepositoryFails() {
        //Arrange
        when(mockUserActivityRepository.findAll()).thenThrow(new RuntimeException("Database error"));
        //Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> mockUserActivityService.getAllActivities());
        assertTrue(ex.getMessage().contains("Failed to fetch activity history"));
    }

    @Test void setActivity_ShouldSaveActivitySuccessfully() {
        //Arrange
        ActivityDTO dto = new ActivityDTO(); dto.setMediaId(UUID.fromString("ed6288bf-84d3-4bee-b753-4e7ef227644f"));
        dto.setMediaType("MUSIC");
        dto.setGenreName(List.of("Rock","Alternative"));
        String userId = "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
        when(mockUserActivityRepository.save(any(UserActivity.class)))
                .thenAnswer(i -> i.getArgument(0));
        UserActivity result = mockUserActivityService.setActivity(dto, userId);
        //Act & Assert
        assertThat(result.getUserId()).isEqualTo("1b2d25d9-0ac1-494d-8208-6b15b6430f3a");
        assertThat(result.getMediaId()).isEqualTo(UUID.fromString("ed6288bf-84d3-4bee-b753-4e7ef227644f"));
        assertThat(result.getMediaType()).isEqualTo("MUSIC");
        assertThat(result.getGenreName()).isEqualTo(List.of("Rock","Alternative"));
        assertThat(result.getPlayedAt()).isNotNull();
        verify(mockUserActivityRepository, times(1)).save(any(UserActivity.class));
    }

    @Test
    void setActivity_ShouldThrowExceptionWhenRepositoryFails() {
        //Arrange
         ActivityDTO dto = new ActivityDTO();
         dto.setMediaId(UUID.fromString("ef6288bf-84d3-4bee-b753-4e7ef22726ef"));
         dto.setMediaType("MUSIC"); dto.setGenreName(List.of("Rock"));
         String userId = "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
         when(mockUserActivityRepository.save(any())).thenThrow(new RuntimeException("Database error"));
        //Act & Assert
         RuntimeException ex = assertThrows(RuntimeException.class, () ->
         mockUserActivityService.setActivity(dto, userId));
         assertTrue(ex.getMessage().contains("Error while registering user activity"));
    }
}