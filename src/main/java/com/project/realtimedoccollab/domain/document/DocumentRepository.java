package com.project.realtimedoccollab.domain.document;

import com.project.realtimedoccollab.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOwnerOrderByUpdatedAtDesc(User owner);

    List<Document> findByCollaboratorsUserId(UUID userId);
}
