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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisField(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisField(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisField(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisField(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned:");

            List<String> improvements = extractImprovements(jsonNode.path("improvements"));
            List<String> suggestiionsfromai = extractSuggestions(jsonNode.path("suggestions"));
            List<String> safety = extractSafetyGuideLines(jsonNode.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getActivityType().toString())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestiionsfromai)
                    .safety(safety)
                    .createdAt(LocalDateTime.now().toString())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Error processing AI response: {}", e.getMessage());

            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getActivityType().toString())
                .recommendation("No detailed analysis available.")
                .improvements(Collections.singletonList("No improvements suggested."))
                .suggestions(Collections.singletonList("No suggestions suggested."))
                .safety(Collections.singletonList("No specific safety guidelines suggested."))
                .createdAt(LocalDateTime.now().toString())
                .build();
    }

    private List<String> extractSafetyGuideLines(JsonNode safetys) {
        List<String> safetyList = new ArrayList<>();
        if (safetys.isArray()) {
            safetys.forEach(items -> safetyList.add(items.asText()));
        }
        return safetyList.isEmpty() ? Collections.singletonList("No specfic suggestions suggested") : safetyList;
    }

    private List<String> extractSuggestions(JsonNode suggestions) {
        List<String> suggestionList = new ArrayList<>();
        if (suggestions.isArray()) {
            suggestions.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestionList.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestionList.isEmpty() ? Collections.singletonList("No suggestions suggested.") : suggestionList;
    }

    private List<String> extractImprovements(JsonNode improvements) {
    List<String> improvementList = new ArrayList<>();
        if (improvements.isArray()) {
            improvements.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvementList.add(String.format("%s: %s", area, detail));
            });
        }
        return improvementList.isEmpty() ? Collections.singletonList("No improvements suggested.") : improvementList;
    }

    private void addAnalysisField(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if ( !analysisNode.path(key).isMissingNode() ) {
            fullAnalysis.append(prefix).
                    append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
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


