package com.example.web.socket_demo.repository;

import com.example.web.socket_demo.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}