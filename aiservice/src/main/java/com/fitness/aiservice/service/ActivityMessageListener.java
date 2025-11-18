package com.fitness.aiservice.service;

import com.fitness.aiservice.models.Activity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
     public void processActivity(Activity activity) {
            log.info("Received activity: {}", activity.getUserId());
            activityAIService.generateRecommendations(activity);
     }
}
