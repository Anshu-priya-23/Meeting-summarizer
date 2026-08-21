package com.assignment.meetingsummarizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryService {

        @Value("${groq.api.key}")
        private String groqApiKey;

        private static final String GROQ_CHAT_URL = "https://api.groq.com/openai/v1/chat/completions";

        public String summarize(String transcript) {
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(groqApiKey);

                // BULLETPROOF PROMPT: Using strict bracket tags that the UI will regex parse.
                String prompt = "You are an expert AI meeting assistant. Analyze the transcript and extract the details. You MUST format your response EXACTLY using these three section tags: [SUMMARY], [DECISIONS], and [ACTIONS]. Do not use markdown headers.\n\n"
                                +
                                "[SUMMARY]\n" +
                                "Meeting Title: [Title]\n" +
                                "Meeting Date: [Date or N/A]\n" +
                                "Meeting Time: [Time or N/A]\n" +
                                "Attendees: [Attendees or N/A]\n\n" +
                                "[Write a 2-paragraph detailed summary of the meeting here.]\n\n" +
                                "[DECISIONS]\n" +
                                "- [Decision 1]\n" +
                                "- [Decision 2]\n\n" +
                                "[ACTIONS]\n" +
                                "- [Action 1]\n" +
                                "- [Action 2]\n\n" +
                                "Transcript:\n" + transcript;

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "openai/gpt-oss-20b");
                requestBody.put("temperature", 0.2);

                Map<String, String> userMessage = new HashMap<>();
                userMessage.put("role", "user");
                userMessage.put("content", prompt);

                requestBody.put("messages", List.of(userMessage));

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                try {
                        ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_CHAT_URL, requestEntity,
                                        Map.class);
                        Map<String, Object> responseBody = response.getBody();
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

                        return (String) message.get("content");
                } catch (Exception e) {
                        throw new RuntimeException("Failed to generate summary: " + e.getMessage());
                }
        }
}