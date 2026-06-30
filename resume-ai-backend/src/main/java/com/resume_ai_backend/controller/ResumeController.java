package com.resume_ai_backend.controller;

import com.resume_ai_backend.dto.ResumeRequest;
import com.resume_ai_backend.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/ai/resume")
@CrossOrigin(origins = "*")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateResume(@RequestBody ResumeRequest request) throws IOException {
        if (request == null || request.description() == null || request.description().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Description cannot be empty");
        }

        if (request.description().length() > 3000) {
            return ResponseEntity.badRequest().body("Description too long. Max 3000 characters allowed.");
        }

        String response = resumeService.generateResumeResponse(request.description());

        if (response == null || response.trim().isEmpty()) {
            return ResponseEntity.internalServerError()
                    .body("Failed to generate resume. Please try again.");
        }
        return ResponseEntity.ok(response);
    }
}