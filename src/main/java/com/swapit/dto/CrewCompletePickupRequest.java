package com.swapit.dto;

public record CrewCompletePickupRequest(
        String pickupPhotoFileName,
        String inspectionMemo
) {
}
