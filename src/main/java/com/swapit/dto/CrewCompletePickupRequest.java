package com.swapit.dto;

public record CrewCompletePickupRequest(
        String pickupPhotoFileName,
        String hubPhotoFileName,
        String inspectionMemo,
        String hubMemo
) {
}
