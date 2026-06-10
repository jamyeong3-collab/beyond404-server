package com.swapit.dto;

import jakarta.validation.constraints.NotBlank;

public record PhotoUploadRequest(
        @NotBlank String fileName,
        String exteriorPhotoFileName,
        String labelPhotoFileName,
        String imageUrl,
        String applianceType,
        Boolean agreedToCreditPolicy
) {
}
