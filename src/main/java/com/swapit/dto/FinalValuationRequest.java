package com.swapit.dto;

public record FinalValuationRequest(
        Integer amount,
        String exteriorReason,
        String partsReason,
        String materialReason,
        String processingReason
) {
}
