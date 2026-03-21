package com.project.realtimedoccollab.domain.dto;

import java.util.UUID;

public record CollaboratorResponse(
        UUID id,
        String email,
        String role,
        String name
) {
}
