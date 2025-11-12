package com.fitness.activityservice.dto;

import com.fitness.activityservice.models.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequest {
    private String userId;
    private ActivityType activityType;
    private double duration; // in minutes
    private double caloriesBurned; // in kilometers
    private LocalDateTime startTime; // ISO 8601 format
    private Map<String, Object> additionalMetrics;
}
