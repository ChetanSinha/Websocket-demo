package com.example.web.socket_demo.controller;

import com.example.web.socket_demo.model.Document;
import com.example.web.socket_demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class ApiDocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return documentService.findById(id)
                .map(document -> ResponseEntity.ok(document))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Document createDocument(@RequestBody Document document) {
        return documentService.save(document, 1L); // Default userId for API
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document documentDetails) {
        return documentService.findById(id)
                .map(document -> {
                    document.setDocName(documentDetails.getDocName());
                    document.setContent(documentDetails.getContent());
                    Document saved = documentService.save(document, 1L); // Default userId for API
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        if (documentService.findById(id).isPresent()) {
            documentService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}