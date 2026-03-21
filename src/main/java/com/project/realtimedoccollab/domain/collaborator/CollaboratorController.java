package com.project.realtimedoccollab.domain.collaborator;

import com.project.realtimedoccollab.auth.user.UserPrincipal;
import com.project.realtimedoccollab.domain.dto.AddCollaboratorRequest;
import com.project.realtimedoccollab.domain.dto.UpdateCollaboratorRoleRequest;
import com.project.realtimedoccollab.domain.dto.CollaboratorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/documents/{documentId}/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    @GetMapping
    public ResponseEntity<List<CollaboratorResponse>> getCollaborators(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(collaboratorService.getCollaborators(documentId, userPrincipal));
    }

    @PostMapping
    public ResponseEntity<Void> ownerAddCollaboratorByEmail(
            @PathVariable Long documentId,
            @Valid @RequestBody AddCollaboratorRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        collaboratorService.addCollaborator(documentId, request, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> ownerRemoveCollaborator(
            @PathVariable Long documentId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        collaboratorService.removeCollaborator(documentId, userId, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateCollaboratorRole(
            @PathVariable Long documentId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateCollaboratorRoleRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        collaboratorService.updateCollaboratorRole(documentId, userId, request, userPrincipal);
        return ResponseEntity.noContent().build();
    }
}

