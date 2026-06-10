package com.swapit.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SwapRequestResponse(
        long id,
        long customerId,
        String status,
        Appliance appliance,
        UserConsent userConsent,
        CaptureEvidence captureEvidence,
        PreValuation preValuation,
        Booking booking,
        PickupRequest pickupRequest,
        DispatchInfo dispatchInfo,
        Tracking tracking,
        FinalValuation finalValuation,
        Credit credit,
        PickupResultReport pickupResultReport,
        RecyclingReport recyclingReport,
        Settlement settlement,
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

    public record UserConsent(
            boolean agreedToCreditPolicy,
            String notice,
            LocalDateTime agreedAt
    ) {
    }

    public record CaptureEvidence(
            String exteriorPhotoFileName,
            String labelPhotoFileName,
            String pickupPhotoFileName,
            String hubPhotoFileName,
            String pickupInspectionMemo,
            String hubMemo
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
            String scheduledAt,
            List<NearbyCrew> nearbyCrews
    ) {
    }

    public record DispatchInfo(
            String alertMessage,
            int matchScore,
            int priorityRank,
            int rejectCount,
            int cancelCount,
            int penaltyCount,
            String recommendedReason
    ) {
    }

    public record Tracking(
            String message,
            LocalDateTime estimatedArrivalAt,
            DriverLocation driverLocation,
            LocationPoint processingCenter,
            String phase,
            TrackingMetrics metrics,
            List<NearbyCrew> nearbyCrews,
            List<TrackingEvent> events
    ) {
    }

    public record LocationPoint(
            String label,
            double lat,
            double lng
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

    public record NearbyCrew(
            Long crewId,
            String crewName,
            String status,
            double lat,
            double lng,
            double distanceMeters,
            boolean assigned
    ) {
    }

    public record TrackingEvent(
            String eventType,
            String message,
            LocalDateTime createdAt
    ) {
    }

    public record TrackingMetrics(
            Double crewToPickupMeters,
            Double crewToProcessingCenterMeters,
            boolean locationLive
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

    public record Settlement(
            Integer baseFee,
            Integer distanceFee,
            Integer incentive,
            Integer penalty,
            Integer totalAmount,
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
