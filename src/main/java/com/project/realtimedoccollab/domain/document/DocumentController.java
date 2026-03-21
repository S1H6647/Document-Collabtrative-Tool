package com.project.realtimedoccollab.domain.document;

import com.project.realtimedoccollab.auth.user.UserPrincipal;
import com.project.realtimedoccollab.domain.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * @param userPrincipal -> currently authenticated user
     * @return DocumentSummaryResponse -> all the created documents
     */
    @GetMapping
    public ResponseEntity<List<DocumentSummaryResponse>> getMyDocuments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(documentService.getDocumentsByOwner(userPrincipal.getUser()));
    }

    /**
     * @param request       -> DTO for creating a document
     * @param userPrincipal -> currently authenticated user
     * @return Document Response -> overview of the created document
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(
            @Valid @RequestBody CreateDocumentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        DocumentResponse response = documentService.createDocument(request, userPrincipal.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * @param documentId    -> id of the document to be updated
     * @param userPrincipal -> currently authenticated user
     * @return Document Response -> Document with its content
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        DocumentResponse response = documentService.getDocument(documentId, userPrincipal.getUser());
        return ResponseEntity.ok(response);
    }

    /**
     * @param documentId    -> id of the document to be updated
     * @param request       -> updated title DTO
     * @param userPrincipal -> currently authenticated user
     * @return Document Summary Response -> Summary of the updated document
     */
    @PatchMapping("/{documentId}")
    public ResponseEntity<DocumentSummaryResponse> updateTitle(
            @PathVariable Long documentId,
            @Valid @RequestBody UpdateTitleRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        var response = documentService.updateTitle(documentId, request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{documentId}/content")
    public ResponseEntity<DocumentResponse> editDocumentById(
            @PathVariable Long documentId,
            @Valid @RequestBody UpdateDocumentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        var response = documentService.editDocumentById(documentId, request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> saveDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody SaveDocumentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        var response = documentService.saveDocument(documentId, request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    /**
     * @param documentId    -> id fo the document to be deleted
     * @param userPrincipal -> currently authenticated user
     * @return No content
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        documentService.deleteDocument(documentId, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    // GET/documents/{id}/historyCollaboratorPaginated edit history
    @GetMapping("/{documentId}/history")
    public ResponseEntity<EditHistoryResponse> getEditHistory(
            @PathVariable Long documentId
    ) {
        var response = documentService.getEditHistory(documentId);
        return ResponseEntity.ok(response);
    }
}
