package com.project.realtimedoccollab.notification;

public enum NotificationType {
    DOCUMENT_SHARED,        // someone added you as a collaborator
    COLLABORATOR_JOINED,    // someone joined a document you own
    COLLABORATOR_LEFT,      // someone left a document you own
    DOCUMENT_EDITED,        // a document you collaborate on was edited (for offline users)
    ROLE_CHANGED            // your role on a document was changed (VIEWER ↔ EDITOR)
}
