package com.swapit.dto;

public record LoginIdCheckResponse(
        boolean available,
        String message
) {
}
