package com.example.ollama_chat.controller;

import com.example.ollama_chat.model.ChatMessage;
import com.example.ollama_chat.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private OllamaService ollamaService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("models", ollamaService.getAvailableModels());
        model.addAttribute("messages", ollamaService.getChatHistory());
        return "index";
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<ChatMessage> chat(@RequestBody ChatMessage message) {
        message.setRole("user");
        ChatMessage response = ollamaService.generateResponse(message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/models")
    @ResponseBody
    public List<String> getModels() {
        return ollamaService.getAvailableModels();
    }

    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Void> clearChat() {
        ollamaService.clearChatHistory();
        return ResponseEntity.ok().build();
    }
}
