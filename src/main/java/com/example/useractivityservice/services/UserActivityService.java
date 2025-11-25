package com.example.useractivityservice.services;

import com.example.useractivityservice.dto.ActivityDTO;
import com.example.useractivityservice.entities.UserActivity;
import com.example.useractivityservice.repositories.UserActivityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

//--------------------- Elham - UserActivityService --------------
@Service
public class UserActivityService {

    private static final Logger FUNCTIONALITY_LOGGER = LogManager.getLogger("functionality");
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public List<UserActivity> getAllActivities() {
        try{
            List<UserActivity> activityList = userActivityRepository.findAll();
            if (activityList.isEmpty()) {
                throw new RuntimeException("There are no posts in the database");
            }
            return activityList;
        }catch (Exception e) {
            FUNCTIONALITY_LOGGER.error("Exception while fetching activities history- error: '{}'", e.getMessage());
            throw new RuntimeException("Error while fetching activities history: " + e.getMessage(), e);
        }
    }

    public UserActivity setActivity(ActivityDTO activityDTO, String userId) {
        try{
            UserActivity activity = new UserActivity();
            activity.setUserId(userId);
            activity.setMediaId(activityDTO.getMediaId());
            activity.setMediaType(activityDTO.getMediaType());
            activity.setGenreName(activityDTO.getGenreName());
            activity.setPlayedAt(LocalDateTime.now());
            userActivityRepository.save(activity);

            FUNCTIONALITY_LOGGER.info("User activity saved successfully: userId:'{}', mediaId: '{}'", activity.getUserId(), activity.getMediaId());
            return activity;
        }catch (Exception e) {
            FUNCTIONALITY_LOGGER.error("Exception while registering user activity- error: '{}'", e.getMessage());
            throw new RuntimeException("Error while registering user activity: " + e.getMessage(), e);
        }

    }
}
