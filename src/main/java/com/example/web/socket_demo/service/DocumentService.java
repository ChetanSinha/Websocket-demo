package com.example.web.socket_demo.service;

import com.example.web.socket_demo.model.Document;
import com.example.web.socket_demo.model.DocumentHistory;
import com.example.web.socket_demo.repository.DocumentHistoryRepository;
import com.example.web.socket_demo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentHistoryRepository documentHistoryRepository;

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    public Document save(Document document, Long userId) {
        boolean isNew = document.getId() == null;
        if (isNew) {
            document.setVersion(1);
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());
        } else {
            Document existing = documentRepository.findById(document.getId()).orElse(null);
            if (existing != null && !existing.getContent().equals(document.getContent())) {
                // Save history with old content
                documentHistoryRepository.save(new DocumentHistory(
                    document.getId(),
                    existing.getVersion(),
                    existing.getContent(),
                    LocalDateTime.now(),
                    userId
                ));
                document.setVersion(existing.getVersion() + 1);
            }
            document.setUpdatedAt(LocalDateTime.now());
        }
        return documentRepository.save(document);
    }

    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    public List<DocumentHistory> findHistoryByDocumentId(Long documentId) {
        return documentHistoryRepository.findByDocumentIdOrderByVersionDesc(documentId);
    }

    public Optional<DocumentHistory> findHistoryById(Long id) {
        return documentHistoryRepository.findById(id);
    }
}