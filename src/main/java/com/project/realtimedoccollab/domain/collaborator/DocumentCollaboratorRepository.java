package com.project.realtimedoccollab.domain.collaborator;

import com.project.realtimedoccollab.domain.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentCollaboratorRepository extends JpaRepository<DocumentCollaborator, Long> {
    boolean existsByDocumentIdAndUserId(Long documentId, UUID userId);

    Optional<DocumentCollaborator> findByDocumentIdAndUserId(Long documentId, UUID userId);

    boolean existsByUserIdAndDocument(UUID userId, Document document);

    Optional<DocumentCollaborator> findByUserIdAndDocumentId(UUID userId, Long documentId);
}
