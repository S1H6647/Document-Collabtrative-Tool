package com.project.realtimedoccollab.domain.dto;

import com.project.realtimedoccollab.domain.collaborator.CollaboratorRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddCollaboratorRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        CollaboratorRole role
) {
}

