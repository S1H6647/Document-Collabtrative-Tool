package com.project.realtimedoccollab.domain.collaborator;

import com.project.realtimedoccollab.auth.user.UserPrincipal;
import com.project.realtimedoccollab.domain.document.Document;
import com.project.realtimedoccollab.domain.document.DocumentRepository;
import com.project.realtimedoccollab.domain.dto.AddCollaboratorRequest;
import com.project.realtimedoccollab.domain.dto.UpdateCollaboratorRoleRequest;
import com.project.realtimedoccollab.domain.dto.CollaboratorResponse;
import com.project.realtimedoccollab.exception.ResourceNotFoundException;
import com.project.realtimedoccollab.user.User;
import com.project.realtimedoccollab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final DocumentRepository documentRepository;
    private final DocumentCollaboratorRepository documentCollaboratorRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addCollaborator(Long documentId, AddCollaboratorRequest request, UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        Document document = getOwnedDocument(documentId, currentUser);

        User collaboratorUser = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        UUID collaboratorUserId = collaboratorUser.getId();

        if (document.getOwner().getId().equals(collaboratorUserId)) {
            return;
        }

        if (documentCollaboratorRepository.existsByDocumentIdAndUserId(documentId, collaboratorUserId)) {
            return;
        }

        CollaboratorRole role = request.role() != null ? request.role() : CollaboratorRole.VIEWER;

        DocumentCollaborator collaborator = DocumentCollaborator.builder()
                .document(document)
                .user(collaboratorUser)
                .role(role)
                .build();

        documentCollaboratorRepository.save(collaborator);
    }

    @Transactional
    public void removeCollaborator(Long documentId, UUID collaboratorUserId, UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        Document document = getOwnedDocument(documentId, currentUser);

        if (document.getOwner().getId().equals(collaboratorUserId)) {
            throw new AccessDeniedException("Owner cannot be removed as collaborator!");
        }

        documentCollaboratorRepository.findByDocumentIdAndUserId(documentId, collaboratorUserId)
                .ifPresent(documentCollaboratorRepository::delete);
    }

    @Transactional
    public void updateCollaboratorRole(
            Long documentId,
            UUID collaboratorUserId,
            UpdateCollaboratorRoleRequest request,
            UserPrincipal userPrincipal) {

        User currentUser = userPrincipal.getUser();
        Document document = getOwnedDocument(documentId, currentUser);

        if (document.getOwner().getId().equals(collaboratorUserId)) {
            throw new AccessDeniedException("Owner role cannot be changed!");
        }

        DocumentCollaborator collaborator = documentCollaboratorRepository
                .findByDocumentIdAndUserId(documentId, collaboratorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found!"));

        collaborator.setRole(request.role());
    }

    @Transactional(readOnly = true)
    public List<CollaboratorResponse> getCollaborators(Long documentId, UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found!"));

        boolean isOwner = document.getOwner().getId().equals(currentUser.getId());
        boolean isCollaborator = documentCollaboratorRepository.existsByDocumentIdAndUserId(documentId, currentUser.getId());

        if (!isOwner && !isCollaborator) {
            throw new AccessDeniedException("You don't have permission to view collaborators for this document!");
        }

        return document.getCollaborators().stream()
                .map(c -> new CollaboratorResponse(
                        c.getUser().getId(),
                        c.getUser().getEmail(),
                        c.getRole().name(),
                        c.getUser().getName()
                ))
                .toList();
    }

    private Document getOwnedDocument(Long documentId, User currentUser) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found!"));

        if (!document.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to manage collaborators for this document!");
        }

        return document;
    }
}

