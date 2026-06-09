package com.swapit.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SwapRequestResponse(
        long id,
        long customerId,
        String status,
        Appliance appliance,
        PreValuation preValuation,
        Booking booking,
        PickupRequest pickupRequest,
        Tracking tracking,
        FinalValuation finalValuation,
        Credit credit,
        PickupResultReport pickupResultReport,
        RecyclingReport recyclingReport,
        List<Notification> notifications
) {
    public record Appliance(
            String applianceType,
            String brand,
            String modelName,
            String estimatedAge,
            String exteriorCondition,
            String conditionGrade,
            String aiAnalysisStatus,
            double aiConfidence,
            String uploadedFileName
    ) {
    }

    public record PreValuation(
            int minEstimatedValue,
            int maxEstimatedValue,
            String currency,
            List<String> basis
    ) {
    }

    public record Booking(
            LocalDate bookingDate,
            String bookingTime,
            String address,
            String detailAddress,
            Double pickupLat,
            Double pickupLng
    ) {
    }

    public record PickupRequest(
            long pickupRequestId,
            String pickupType,
            String status,
            Long crewId,
            String crewName,
            String address,
            String scheduledAt
    ) {
    }

    public record Tracking(
            String message,
            LocalDateTime estimatedArrivalAt,
            DriverLocation driverLocation,
            List<TrackingEvent> events
    ) {
    }

    public record DriverLocation(
            double lat,
            double lng,
            double heading,
            double speed,
            LocalDateTime updatedAt
    ) {
    }

    public record TrackingEvent(
            String eventType,
            String message,
            LocalDateTime createdAt
    ) {
    }

    public record FinalValuation(
            Integer amount,
            String currency,
            String status,
            List<String> reasons
    ) {
    }

    public record Credit(
            int amount,
            String currency,
            String status
    ) {
    }

    public record RecyclingReport(
            String summary,
            List<String> steps
    ) {
    }

    public record PickupResultReport(
            String resultType,
            String summary,
            List<String> details
    ) {
    }

    public record Notification(
            long notificationId,
            String title,
            String message,
            boolean read,
            LocalDateTime createdAt
    ) {
    }
}
