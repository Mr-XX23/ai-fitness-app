package com.fitness.aiservice.service;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.ThinkingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
public class GeminiService {

    Client client = new Client();
    GenerateContentConfig config = GenerateContentConfig.builder().thinkingConfig(ThinkingConfig.builder().thinkingBudget(0).build()).build();

    public String getRecommdations(String details) {
        GenerateContentResponse response =
                client.models.generateContent("gemini-2.5-flash", details, config);
        return response.text();
    }
}
