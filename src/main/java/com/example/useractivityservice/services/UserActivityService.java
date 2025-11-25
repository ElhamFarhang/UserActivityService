package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.ActivityDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

//--------------------- Elham - UserActivityService --------------
@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public List<UserActivity> getAllActivities() {
        List<UserActivity> activityList = userActivityRepository.findAll();
        if (activityList.isEmpty()) {
            throw new RuntimeException("There are no posts in the database");
        }
        return activityList;
    }

    public UserActivity setActivity(ActivityDTO activityDTO, String userId) {
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setMediaId(activityDTO.getMediaId());
        activity.setMediaType(activityDTO.getMediaType());
        activity.setGenreName(activityDTO.getGenreName());
        activity.setPlayedAt(LocalDateTime.now());

        return userActivityRepository.save(activity);
    }
}
