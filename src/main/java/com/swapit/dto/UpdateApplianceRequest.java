package com.swapit.dto;

public record UpdateApplianceRequest(
        String applianceType,
        String brand,
        String modelName,
        String estimatedAge,
        String exteriorCondition
) {
}
