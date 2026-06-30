package com.resume_ai_backend.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final ChatClient chatClient;
    private String cachedTemplate;

    public ResumeServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostConstruct
    public void init() throws IOException {
        cachedTemplate = loadPromptFromFile("ResumePrompt.txt");
        log.info("Resume prompt template loaded successfully");
    }

    @Override
    public String generateResumeResponse(String userResumeDescription) throws IOException {
        log.info("Resume generation started, input length: {}", userResumeDescription.length());
        long start = System.currentTimeMillis();

        String finalPrompt = putvaluesToTemplate(cachedTemplate, Map.of("userInput", userResumeDescription));

        String response = chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();

        log.info("Resume generated in {} ms", System.currentTimeMillis() - start);

        return cleanJsonResponse(response);
    }

    public String loadPromptFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String putvaluesToTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    private String cleanJsonResponse(String response) {
        if (response == null) return null;
        return response.trim()
                .replaceAll("^```json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("```\\s*$", "")
                .trim();
    }
}
