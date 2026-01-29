package com.example.web.socket_demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.web.socket_demo.model.Document;
import com.example.web.socket_demo.model.User;
import com.example.web.socket_demo.service.DocumentService;
import com.example.web.socket_demo.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DocumentWebSocketController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentMap<Long, ConcurrentHashMap.KeySetView<String, Boolean>> docEditors =
        new ConcurrentHashMap<>();
    @MessageMapping("/document/{docId}")
    public void handleDocumentEdit(@DestinationVariable Long docId, @Payload String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String content = jsonNode.get("content").asText();
            Long userId = jsonNode.get("userId").asLong();

            // Update the document content in DB
            Document document = documentService.findById(docId).orElse(null);
            if (document != null) {
                document.setContent(content);
                documentService.save(document, userId);
                // Broadcast the updated content to all subscribers
                messagingTemplate.convertAndSend("/topic/document/" + docId, content);
            }
        } catch (Exception e) {
            // Handle parsing error
            e.printStackTrace();
        }
    }

    @MessageMapping("/join/{docId}")
    public void joinDocument(@DestinationVariable Long docId, @Payload Long userId) {
        User user = userService.findById(userId).orElse(null);
        if (user != null) {
            docEditors.computeIfAbsent(docId, k -> ConcurrentHashMap.newKeySet()).add(user.getUsername());
            List<String> editors = new ArrayList<>(docEditors.get(docId));
            messagingTemplate.convertAndSend("/topic/editors/" + docId, editors);
        }
    }
}