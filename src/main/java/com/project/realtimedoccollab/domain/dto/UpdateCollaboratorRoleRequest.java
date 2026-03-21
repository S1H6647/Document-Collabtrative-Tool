package com.project.realtimedoccollab.domain.dto;

import com.project.realtimedoccollab.domain.collaborator.CollaboratorRole;
import jakarta.validation.constraints.NotNull;

public record UpdateCollaboratorRoleRequest(
        @NotNull(message = "Role is required")
        CollaboratorRole role
) {
}

