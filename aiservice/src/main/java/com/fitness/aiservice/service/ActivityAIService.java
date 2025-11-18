package com.fitness.aiservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.models.Activity;
import com.fitness.aiservice.models.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendations(Activity activity) {
        String prompt = createPromptFromActivity(activity);
        String recommendations = geminiService.getRecommdations(prompt);
        log.info("{}", recommendations);
        return processAIResponse(activity, recommendations);
    }

    private Recommendation processAIResponse(Activity activity, String recommendations) {

        try {
            String jsonString = recommendations.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            JsonNode analysisNode = jsonNode.path("analysis");
        } catch (JsonProcessingException e) {
            log.error("Error processing AI response: {}", e.getMessage());
        }

        return null;
    }

    private String createPromptFromActivity(Activity activity) {
        return String.format(
                """
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %f minutes
        Calories Burned: %f
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getActivityType().toString(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics().toString()
        );
    }
}


