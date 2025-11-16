package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.models.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;
    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest request) {

       boolean isValidUser =  userValidationService.validateUser(request.getUserId());

       if ( !isValidUser ) {
           throw new RuntimeException("Invalid User ID: " + request.getUserId());
       }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .activityType(request.getActivityType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);

        try {
            kafkaTemplate.send(topicName, savedActivity.getId(), savedActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
    ActivityResponse activityResponse = new ActivityResponse();

    activityResponse.setId(activity.getId());
    activityResponse.setUserId(activity.getUserId());
    activityResponse.setActivityType(activity.getActivityType());
    activityResponse.setDuration(activity.getDuration());
    activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
    activityResponse.setStartTime(activity.getStartTime());
    activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
    activityResponse.setCreatedAt(activity.getCreatedAt());
    activityResponse.setUpdatedAt(activity.getUpdatedAt());

    return activityResponse;

    }
}
