package com.project.realtimedoccollab.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTitleRequest(
        @NotBlank(message = "Title should not be blank")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title
) {
}
