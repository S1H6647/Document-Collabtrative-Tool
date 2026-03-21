package com.project.realtimedoccollab.domain.edit;

import com.project.realtimedoccollab.domain.document.Document;
import com.project.realtimedoccollab.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "document_edits")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DocumentEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private User editor;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contentSnapshot;     // full document content at this point in time

    @Column(nullable = false, updatable = false)
    private Instant editedAt;

    @PrePersist
    protected void onCreate() {
        editedAt = Instant.now();
    }
}
