package com.project.realtimedoccollab.domain.dto;

import java.util.List;

public record EditHistoryResponse(
        long documentId,
        List<DocumentEditEntry> edits,
        int currentPage,
        int pageSize,
        long totalEdits,
        int totalPages
) {
    public static EditHistoryResponse of(long documentId, List<DocumentEditEntry> edits) {
        int totalEdits = edits.size();
        return new EditHistoryResponse(
                documentId,
                edits,
                1,
                totalEdits,
                totalEdits,
                totalEdits == 0 ? 0 : 1
        );
    }
}
