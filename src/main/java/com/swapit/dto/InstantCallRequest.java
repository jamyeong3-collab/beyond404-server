package com.swapit.dto;

import jakarta.validation.constraints.NotBlank;

public record InstantCallRequest(
        @NotBlank String address,
        String detailAddress,
        Double pickupLat,
        Double pickupLng
) {
}
