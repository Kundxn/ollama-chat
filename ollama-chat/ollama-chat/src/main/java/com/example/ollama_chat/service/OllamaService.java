package com.example.ollama_chat.service;

import com.example.ollama_chat.model.ChatMessage;
import com.example.ollama_chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OllamaService {
    private final String OLLAMA_API_URL = "http://localhost:11434/api";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository messageRepository;

    @Autowired
    public OllamaService(ChatMessageRepository messageRepository) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.messageRepository = messageRepository;
    }

    public ChatMessage generateResponse(ChatMessage userMessage) {
        // Save user message
        userMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(userMessage);

        // Prepare Ollama request
        Map<String, Object> request = new HashMap<>();
        request.put("model", userMessage.getModel());
        request.put("prompt", userMessage.getContent());
        request.put("stream", false);

        try {
            // Call Ollama API
            Map<String, Object> response = restTemplate.postForObject(
                    OLLAMA_API_URL + "/generate",
                    request,
                    Map.class
            );

            // Create and save assistant's response
            ChatMessage assistantMessage = new ChatMessage();
            assistantMessage.setRole("assistant");
            assistantMessage.setContent(response != null ? (String) response.get("response") : "Error generating response");
            assistantMessage.setModel(userMessage.getModel());
            assistantMessage.setTimestamp(LocalDateTime.now());
            return messageRepository.save(assistantMessage);
        } catch (Exception e) {
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setRole("assistant");
            errorMessage.setContent("Error: " + e.getMessage());
            errorMessage.setModel(userMessage.getModel());
            errorMessage.setTimestamp(LocalDateTime.now());
            return messageRepository.save(errorMessage);
        }
    }

    public List<String> getAvailableModels() {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    OLLAMA_API_URL + "/tags",
                    Map.class
            );

            List<String> modelNames = new ArrayList<>();
            if (response != null && response.containsKey("models")) {
                List<Map<String, Object>> models = (List<Map<String, Object>>) response.get("models");
                for (Map<String, Object> model : models) {
                    modelNames.add((String) model.get("name"));
                }
            }
            return modelNames;
        } catch (Exception e) {
            return Collections.singletonList("llama2");
        }
    }

    public List<ChatMessage> getChatHistory() {
        return messageRepository.findAll();
    }

    public void clearChatHistory() {
        messageRepository.deleteAll();
    }
}
