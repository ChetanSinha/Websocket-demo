package com.example.web.socket_demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_history")
public class DocumentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id")
    private Long documentId;

    private int version;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    private Long userId;

    // Constructors, getters, setters

    public DocumentHistory() {}

    public DocumentHistory(Long documentId, int version, String content, LocalDateTime updatedAt, Long userId) {
        this.documentId = documentId;
        this.version = version;
        this.content = content;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}