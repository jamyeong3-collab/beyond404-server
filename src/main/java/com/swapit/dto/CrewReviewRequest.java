package com.swapit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CrewReviewRequest(
        @Min(1) @Max(5) int rating,
        @Size(max = 120) String comment
) {
}
