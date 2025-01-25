package com.example.ollama_chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ChatMessage {
    @Id
    @GeneratedValue
    private Long id;
    private String role;
    private String content;
    private String model;
    private LocalDateTime timestamp;
}
