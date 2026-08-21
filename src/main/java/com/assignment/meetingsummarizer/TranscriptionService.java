package com.assignment.meetingsummarizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranscriptionService {

        @Value("${assemblyai.api.key}")
        private String assemblyApiKey;

        public String transcribeAudio(MultipartFile file) throws Exception {
                RestTemplate restTemplate = new RestTemplate();
                ObjectMapper mapper = new ObjectMapper();

                System.out.println("Uploading large file to AssemblyAI...");

                // 1. Upload the raw audio file (This bypasses all size limits)
                HttpHeaders uploadHeaders = new HttpHeaders();
                uploadHeaders.set("Authorization", assemblyApiKey);
                uploadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                // NEW (Streams the file safely chunk-by-chunk):
                HttpEntity<org.springframework.core.io.Resource> uploadEntity = new HttpEntity<>(file.getResource(),
                                uploadHeaders);
                ResponseEntity<String> uploadResponse = restTemplate.postForEntity(
                                "https://api.assemblyai.com/v2/upload", uploadEntity, String.class);

                JsonNode uploadNode = mapper.readTree(uploadResponse.getBody());
                String uploadUrl = uploadNode.get("upload_url").asText();

                System.out.println("File uploaded successfully. Requesting transcription...");

                // 2. Request the transcription
                HttpHeaders transcribeHeaders = new HttpHeaders();
                transcribeHeaders.set("Authorization", assemblyApiKey);
                transcribeHeaders.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> body = new HashMap<>();
                body.put("audio_url", uploadUrl);

                HttpEntity<Map<String, String>> transcribeEntity = new HttpEntity<>(body, transcribeHeaders);
                ResponseEntity<String> transcribeResponse = restTemplate.postForEntity(
                                "https://api.assemblyai.com/v2/transcript", transcribeEntity, String.class);

                JsonNode transcribeNode = mapper.readTree(transcribeResponse.getBody());
                String transcriptId = transcribeNode.get("id").asText();

                System.out.println("Transcription in progress. Polling for completion...");

                // 3. Poll until completed
                String pollingUrl = "https://api.assemblyai.com/v2/transcript/" + transcriptId;
                HttpEntity<Void> pollEntity = new HttpEntity<>(transcribeHeaders);

                while (true) {
                        ResponseEntity<String> pollResponse = restTemplate.exchange(
                                        pollingUrl, HttpMethod.GET, pollEntity, String.class);
                        JsonNode pollNode = mapper.readTree(pollResponse.getBody());
                        String status = pollNode.get("status").asText();

                        if ("completed".equals(status)) {
                                System.out.println("Transcription finished!");
                                return pollNode.get("text").asText();
                        } else if ("error".equals(status)) {
                                throw new RuntimeException("AssemblyAI failed: " + pollNode.get("error").asText());
                        }

                        // Wait 4 seconds before checking status again to prevent rate-limiting
                        Thread.sleep(4000);
                }
        }
}