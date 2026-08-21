package com.assignment.meetingsummarizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/audio")
@CrossOrigin(origins = "*")
public class AudioUploadController {

    @Autowired
    private TranscriptionService transcriptionService;

    @Autowired
    private SummaryService summaryService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "Please select a file to upload.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            System.out.println("Processing file: " + file.getOriginalFilename());

            // 1. Get the raw text from AssemblyAI
            String transcript = transcriptionService.transcribeAudio(file);
            System.out.println("Transcription complete. Generating summary...");

            // 2. Get the structured summary from Gemini
            String summary = summaryService.summarize(transcript);

            // 3. Put them into a JSON-friendly map
            response.put("transcript", transcript);
            response.put("summary", summary);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Could not process the file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}