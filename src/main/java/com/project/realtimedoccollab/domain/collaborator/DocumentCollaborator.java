package com.project.realtimedoccollab.domain.collaborator;

import com.project.realtimedoccollab.domain.document.Document;
import com.project.realtimedoccollab.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "document_collaborators",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"})
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DocumentCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaboratorRole role;       // VIEWER, EDITOR

    @Column(nullable = false, updatable = false)
    private Instant addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = Instant.now();
    }
}
