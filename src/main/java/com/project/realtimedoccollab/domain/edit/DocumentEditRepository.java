package com.project.realtimedoccollab.domain.edit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentEditRepository extends JpaRepository<DocumentEdit, Long> {
    List<DocumentEdit> findTop50ByDocumentIdOrderByEditedAtDesc(Long documentId);
}
