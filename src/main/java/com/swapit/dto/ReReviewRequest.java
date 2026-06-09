package com.swapit.dto;

import jakarta.validation.constraints.NotBlank;

public record ReReviewRequest(
        @NotBlank String reason
) {
}
