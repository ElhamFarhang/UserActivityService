package com.example.useractivityservice.repositories;

import com.example.useractivityservice.dto.MostPlayedDTO;
import com.example.useractivityservice.entities.UserActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
////////////////Anna/////////////////////////////////////////////////////////
@DataJpaTest
@ActiveProfiles("test")
class UserActivityRepositoryTest {

    @Autowired
    private UserActivityRepository userActivityRepository;

    private String userId;
    private String mediaType;
    private UUID mediaId1;
    private UUID mediaId2;

    @BeforeEach
    void setUp() {
        userId = "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
        mediaType = "MUSIC";
        mediaId1 = UUID.randomUUID();
        mediaId2 = UUID.randomUUID();

        userActivityRepository.deleteAll();

        UserActivity ua1 = new UserActivity();
        ua1.setUserId(userId);
        ua1.setMediaType(mediaType);
        ua1.setMediaId(mediaId1);
        ua1.setPlayedAt(LocalDateTime.now().minusDays(1));
        userActivityRepository.save(ua1);

        UserActivity ua2 = new UserActivity();
        ua2.setUserId(userId);
        ua2.setMediaType(mediaType);
        ua2.setMediaId(mediaId1);
        ua2.setPlayedAt(LocalDateTime.now().minusDays(2));
        userActivityRepository.save(ua2);

        UserActivity ua3 = new UserActivity();
        ua3.setUserId(userId);
        ua3.setMediaType(mediaType);
        ua3.setMediaId(mediaId2);
        ua3.setPlayedAt(LocalDateTime.now().minusDays(1));
        userActivityRepository.save(ua3);
    }

    @Test
    void testFindMostPlayedMediaByUserId() {
        List<MostPlayedDTO> result = userActivityRepository.findMostPlayedMediaByUserId(
                userId,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getMediaId()).isEqualTo(mediaId1);
        assertThat(result.get(0).getTimesPlayed()).isEqualTo(2L);
    }

    @Test
    void testFindMostPlayedMediaByUserIdAndMediaType() {
        List<MostPlayedDTO> result = userActivityRepository.findMostPlayedMediaByUserIdAndMediaType(
                userId,
                mediaType,
                PageRequest.of(0, 10)
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMediaId()).isEqualTo(mediaId1);
        assertThat(result.get(0).getTimesPlayed()).isEqualTo(2L);
    }

    @Test
    void testFindMostPlayedMediaInPeriodByMediaType() {
        List<MostPlayedDTO> result = userActivityRepository.findMostPlayedMediaInPeriodByMediaType(
                mediaType,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMediaId()).isEqualTo(mediaId1);
        assertThat(result.get(0).getTimesPlayed()).isEqualTo(2L);
        assertThat(result.get(1).getMediaId()).isEqualTo(mediaId2);
        assertThat(result.get(0).getTimesPlayed()).isEqualTo(2L);
    }

    @Test
    void testFindByUserIdAndMediaTypeOrderByPlayedAtDesc() {
        List<UserActivity> result =
                userActivityRepository.findByUserIdAndMediaTypeOrderByPlayedAtDesc(userId, mediaType);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPlayedAt())
                .isAfter(result.get(1).getPlayedAt());
    }

    @Test
    void testFindByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end   = LocalDateTime.now();

        List<UserActivity> result =
                userActivityRepository.findByUserIdAndPlayedAtBetweenOrderByPlayedAtDesc(userId, start, end);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPlayedAt())
                .isAfter(result.get(1).getPlayedAt());
    }

    @Test
    void testFindByMediaType() {
        List<UserActivity> result = userActivityRepository.findByMediaType(mediaType);

        assertThat(result).hasSize(3);
        assertThat(result).allMatch(ua -> ua.getMediaType().equals(mediaType));
    }

    @Test
    void testFindByUserIdAndMediaTypeAndPlayedAtAfter() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(2);

        List<UserActivity> result =
                userActivityRepository.findByUserIdAndMediaTypeAndPlayedAtAfter(userId, mediaType, cutoff);

        assertThat(result).hasSize(2);
    }

    @Test
    void testFindByMediaTypeAndPlayedAtAfter() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(2);

        List<UserActivity> result =
                userActivityRepository.findByMediaTypeAndPlayedAtAfter(mediaType, cutoff);

        assertThat(result).hasSize(2);
    }

    @Test
    void testFindByMediaTypeAndPlayedAtBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end   = LocalDateTime.now();

        List<UserActivity> result =
                userActivityRepository.findByMediaTypeAndPlayedAtBetween(mediaType, start, end);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(ua -> ua.getMediaType().equals(mediaType));
    }

}