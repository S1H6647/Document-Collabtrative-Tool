package com.project.realtimedoccollab.domain.collaborator;

import com.project.realtimedoccollab.auth.user.UserPrincipal;
import com.project.realtimedoccollab.domain.document.Document;
import com.project.realtimedoccollab.domain.document.DocumentRepository;
import com.project.realtimedoccollab.domain.dto.AddCollaboratorRequest;
import com.project.realtimedoccollab.exception.ResourceNotFoundException;
import com.project.realtimedoccollab.user.User;
import com.project.realtimedoccollab.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentCollaboratorRepository documentCollaboratorRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CollaboratorService collaboratorService;

    @Test
    void addCollaborator_ShouldAddDirectly_WhenUserExists() {
        // Arrange
        Long documentId = 1L;
        String ownerEmail = "owner@example.com";
        String collaboratorEmail = "collab@example.com";

        User owner = User.builder().id(UUID.randomUUID()).email(ownerEmail).build();
        User collaborator = User.builder().id(UUID.randomUUID()).email(collaboratorEmail).build();

        Document document = Document.builder()
                .id(documentId)
                .owner(owner)
                .build();

        UserPrincipal principal = UserPrincipal.from(owner);
        AddCollaboratorRequest request = new AddCollaboratorRequest(collaboratorEmail, CollaboratorRole.EDITOR);

        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findUserByEmail(collaboratorEmail)).thenReturn(Optional.of(collaborator));
        when(documentCollaboratorRepository.existsByDocumentIdAndUserId(documentId, collaborator.getId())).thenReturn(false);

        // Act
        collaboratorService.addCollaborator(documentId, request, principal);

        // Assert
        verify(documentCollaboratorRepository).save(argThat(collaboratorArg ->
                collaboratorArg.getUser().getEmail().equals(collaboratorEmail) &&
                        collaboratorArg.getRole() == CollaboratorRole.EDITOR &&
                        collaboratorArg.getDocument().getId().equals(documentId)
        ));
    }

    @Test
    void addCollaborator_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        Long documentId = 1L;
        String ownerEmail = "owner@example.com";
        String unknownEmail = "unknown@example.com";

        User owner = User.builder().id(UUID.randomUUID()).email(ownerEmail).build();
        Document document = Document.builder().id(documentId).owner(owner).build();
        UserPrincipal principal = UserPrincipal.from(owner);
        AddCollaboratorRequest request = new AddCollaboratorRequest(unknownEmail, CollaboratorRole.VIEWER);

        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(userRepository.findUserByEmail(unknownEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                collaboratorService.addCollaborator(documentId, request, principal)
        );

        verify(documentCollaboratorRepository, never()).save(any());
    }
}

