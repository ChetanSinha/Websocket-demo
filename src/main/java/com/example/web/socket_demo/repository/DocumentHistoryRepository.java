package com.example.web.socket_demo.repository;

import com.example.web.socket_demo.model.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {

    List<DocumentHistory> findByDocumentIdOrderByVersionDesc(Long documentId);

    @Query("SELECT MAX(d.version) FROM DocumentHistory d WHERE d.documentId = :documentId")
    Integer findMaxVersionByDocumentId(@Param("documentId") Long documentId);
}