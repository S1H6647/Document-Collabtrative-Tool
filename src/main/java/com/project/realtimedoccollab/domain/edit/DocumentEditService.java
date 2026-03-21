package com.project.realtimedoccollab.domain.edit;

import com.project.realtimedoccollab.domain.document.Document;
import com.project.realtimedoccollab.domain.document.DocumentRepository;
import com.project.realtimedoccollab.user.User;
import com.project.realtimedoccollab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentEditService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentEditRepository documentEditRepository;

    @Async("editPersistenceExecutor")
    public void persistEditAsync(Document document, User editor, String content) {
        try {
            DocumentEdit edit = DocumentEdit.builder()
                    .document(document)
                    .editor(editor)
                    .contentSnapshot(content)
                    .build();

            documentEditRepository.save(edit);

            log.debug("[{}] Edit history saved for document {} by user {}",
                    Thread.currentThread().getName(),
                    document.getId(),
                    editor.getId());
        } catch (Exception e) {
            // NEVER let an async method silently swallow exceptions.
            // The caller won't see this exception — log it explicitly.
            log.error("Async edit persistence failed for document {} by user {}: {}",
                    document.getId(), editor.getId(), e.getMessage(), e);
        }
    }

    public List<DocumentEdit> getRecentEdits(Long documentId) {
        return documentEditRepository.findTop50ByDocumentIdOrderByEditedAtDesc(documentId);
    }
}
