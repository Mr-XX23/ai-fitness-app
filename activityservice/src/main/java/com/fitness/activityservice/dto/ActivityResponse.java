package com.fitness.activityservice.dto;

import com.fitness.activityservice.models.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponse {
    private  String id;
    private String userId;
    private ActivityType activityType;
    private double duration; // in minutes
    private double caloriesBurned; // in kilometers
    private LocalDateTime startTime; // ISO 8601 format
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
