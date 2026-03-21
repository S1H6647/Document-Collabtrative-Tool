package com.project.realtimedoccollab.domain.dto;

import com.project.realtimedoccollab.domain.edit.DocumentEdit;

import java.time.Instant;

public record DocumentEditEntry(
        long editId,
        String editorName,
        String editSummary,      // short description of what changed
        String contentSnapshot,  // the actual rich text JSON at that point
        Instant editedAt
) {
    public static DocumentEditEntry from(DocumentEdit documentEdit, String editSummary) {
        return new DocumentEditEntry(
                documentEdit.getId(),
                documentEdit.getEditor().getName(),
                editSummary,
                documentEdit.getContentSnapshot(),
                documentEdit.getEditedAt()
        );
    }
}
