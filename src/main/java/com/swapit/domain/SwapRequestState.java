package com.swapit.domain;

import com.swapit.domain.enums.SwapRequestStatus;
import com.swapit.dto.SwapRequestResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SwapRequestState {
    private final long id;
    private final long customerId;
    private SwapRequestStatus status;

    private String uploadedFileName;
    private String applianceType;
    private String brand;
    private String modelName;
    private String estimatedAge;
    private String exteriorCondition;
    private String conditionGrade;
    private String aiAnalysisStatus;
    private double aiConfidence;
    private boolean creditPolicyAgreed;
    private LocalDateTime consentedAt;
    private String exteriorPhotoFileName;
    private String labelPhotoFileName;

    private int minEstimatedValue;
    private int maxEstimatedValue;
    private boolean preValuationAccepted;

    private Long pickupRequestId;
    private String pickupType;
    private String pickupStatus;
    private Long crewId;
    private String crewName;
    private LocalDate bookingDate;
    private String bookingTime;
    private String address;
    private String detailAddress;
    private Double pickupLat;
    private Double pickupLng;
    private final List<SwapRequestResponse.NearbyCrew> nearbyCrews = new ArrayList<>();

    private String trackingMessage;
    private String trackingPhase;
    private LocalDateTime estimatedArrivalAt;
    private SwapRequestResponse.DriverLocation driverLocation;
    private SwapRequestResponse.LocationPoint processingCenter;
    private final List<SwapRequestResponse.TrackingEvent> trackingEvents = new ArrayList<>();

    private Integer finalCreditValue;
    private String finalValuationStatus;
    private final List<String> finalValuationReasons = new ArrayList<>();
    private boolean reReviewRequested;
    private boolean reReviewCompleted;
    private String reReviewReason;

    private String creditStatus;
    private String pickupResultType;
    private String pickupResultSummary;
    private final List<String> pickupResultDetails = new ArrayList<>();
    private String pickupPhotoFileName;
    private String hubPhotoFileName;
    private String pickupInspectionMemo;
    private String hubMemo;
    private int dispatchMatchScore;
    private int dispatchPriorityRank;
    private int crewRejectCount;
    private int crewCancelCount;
    private int crewPenaltyCount;
    private String dispatchAlertMessage;
    private String dispatchRecommendedReason;
    private Integer settlementBaseFee;
    private Integer settlementDistanceFee;
    private Integer settlementIncentive;
    private Integer settlementPenalty;
    private Integer settlementTotalAmount;
    private String settlementStatus;
    private final List<SwapRequestResponse.Notification> notifications = new ArrayList<>();
    private long notificationSequence = 1;

    public SwapRequestState(long id, long customerId, String applianceType) {
        this.id = id;
        this.customerId = customerId;
        this.status = SwapRequestStatus.CREATED;
        this.applianceType = valueOrDefault(applianceType, "washing_machine");
        this.brand = "LG";
        this.modelName = "Unknown";
        this.estimatedAge = "Needs review";
        this.exteriorCondition = "Needs review";
        this.conditionGrade = "unknown";
        this.aiAnalysisStatus = "PENDING";
        this.aiConfidence = 0.0;
        this.minEstimatedValue = 0;
        this.maxEstimatedValue = 0;
        this.trackingPhase = "REQUEST_CREATED";
        this.trackingMessage = "Your exchange request has been created.";
        this.estimatedArrivalAt = LocalDateTime.now().plusMinutes(35);
        addTrackingEvent("CREATED", "Exchange request created");
        addNotification("SwapIt started", "Upload an appliance photo to continue toward valuation and pickup.");
    }

    public long getId() {
        return id;
    }

    public Long getPickupRequestId() {
        return pickupRequestId;
    }

    public String getPickupStatus() {
        return pickupStatus;
    }

    public Long getCrewId() {
        return crewId;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void applyMockInspection(
            String fileName,
            String requestedApplianceType,
            String imageUrl,
            String exteriorPhotoFileName,
            String labelPhotoFileName,
            Boolean agreedToCreditPolicy
    ) {
        this.exteriorPhotoFileName = valueOrDefault(exteriorPhotoFileName, valueOrDefault(fileName, imageUrl));
        this.labelPhotoFileName = valueOrDefault(labelPhotoFileName, this.labelPhotoFileName);
        this.uploadedFileName = this.exteriorPhotoFileName;
        if (requestedApplianceType != null && !requestedApplianceType.isBlank()) {
            this.applianceType = requestedApplianceType;
        }
        this.creditPolicyAgreed = Boolean.TRUE.equals(agreedToCreditPolicy);
        this.consentedAt = this.creditPolicyAgreed ? LocalDateTime.now() : this.consentedAt;
        this.brand = "LG";
        this.modelName = mockModelName(this.applianceType);
        this.estimatedAge = "3-5 years";
        this.exteriorCondition = "Minor exterior wear detected";
        this.conditionGrade = "good";
        this.aiAnalysisStatus = "COMPLETED";
        this.aiConfidence = 0.86;
        this.minEstimatedValue = 1800;
        this.maxEstimatedValue = 3200;
        this.status = SwapRequestStatus.PRE_VALUATION_READY;
        this.trackingMessage = "AI inspection is ready. Review the estimate and choose a pickup method.";
        addTrackingEvent("PHOTO_ANALYZED", "Appliance photo analyzed");
        addNotification("AI inspection complete", "Your appliance estimate is ready for review.");
    }

    public void updateAppliance(
            String applianceType,
            String brand,
            String modelName,
            String estimatedAge,
            String exteriorCondition
    ) {
        this.applianceType = valueOrDefault(applianceType, this.applianceType);
        this.brand = valueOrDefault(brand, this.brand);
        this.modelName = valueOrDefault(modelName, this.modelName);
        this.estimatedAge = valueOrDefault(estimatedAge, this.estimatedAge);
        this.exteriorCondition = valueOrDefault(exteriorCondition, this.exteriorCondition);
        this.conditionGrade = this.exteriorCondition.toLowerCase().contains("damage") ? "damaged" : "good";
        addTrackingEvent("APPLIANCE_CONFIRMED", "Appliance details confirmed");
    }

    public void acceptPreValuation() {
        this.preValuationAccepted = true;
        this.status = SwapRequestStatus.PRE_VALUATION_ACCEPTED;
        this.trackingMessage = "Pickup selection is ready. Choose booking or instant call.";
        addTrackingEvent("PRE_VALUATION_ACCEPTED", "Pre-valuation accepted");
    }

    public void confirmBooking(LocalDate bookingDate, String bookingTime, String address, String detailAddress, Double pickupLat, Double pickupLng) {
        this.pickupRequestId = ensurePickupRequestId();
        this.pickupType = "BOOKING";
        this.pickupStatus = "CONFIRMED";
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.address = address;
        this.detailAddress = detailAddress;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.status = SwapRequestStatus.BOOKING_CONFIRMED;
        this.trackingPhase = "AWAITING_CREW_ASSIGNMENT";
        this.trackingMessage = "Scheduled pickup confirmed. Nearby crews can now view this request.";
        addTrackingEvent("BOOKING_CONFIRMED", "Pickup booking confirmed");
        addNotification("Booking confirmed", scheduledLabel() + " pickup has been confirmed.");
    }

    public void requestInstantCall(String address, String detailAddress, Double pickupLat, Double pickupLng) {
        this.pickupRequestId = ensurePickupRequestId();
        this.pickupType = "INSTANT_CALL";
        this.pickupStatus = "REQUESTED";
        this.address = address;
        this.detailAddress = detailAddress;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.status = SwapRequestStatus.INSTANT_CALL_REQUESTED;
        this.trackingPhase = "SEARCHING_NEARBY_CREW";
        this.trackingMessage = "Looking for nearby LG-certified pickup crews.";
        addTrackingEvent("INSTANT_CALL_REQUESTED", "Instant pickup call requested");
        addNotification("Instant call requested", "Nearby crews are being matched to your location.");
    }

    public void acceptByCrew(long crewId, String crewName) {
        this.pickupRequestId = ensurePickupRequestId();
        this.crewId = crewId;
        this.crewName = valueOrDefault(crewName, "LG pickup partner");
        this.pickupStatus = "ASSIGNED";
        this.status = SwapRequestStatus.CREW_ASSIGNED;
        this.trackingPhase = "CREW_ASSIGNED";
        this.trackingMessage = this.crewName + " has been assigned to your pickup.";
        addTrackingEvent("CREW_ASSIGNED", "Crew assigned");
        addNotification("Crew assigned", this.crewName + " is now assigned to your pickup.");
    }

    public void departCrew() {
        this.pickupStatus = "IN_PROGRESS";
        this.status = SwapRequestStatus.PICKUP_IN_PROGRESS;
        this.trackingPhase = "EN_ROUTE_TO_PICKUP";
        this.trackingMessage = "The crew is on the way to the pickup location.";
        this.estimatedArrivalAt = LocalDateTime.now().plusMinutes(18);
        addTrackingEvent("CREW_DEPARTED", "Crew departed toward pickup");
        addNotification("Crew departed", "The assigned crew is heading to your location.");
    }

    public void updateCrewLocation(double lat, double lng, double heading, double speed) {
        this.driverLocation = new SwapRequestResponse.DriverLocation(lat, lng, heading, speed, LocalDateTime.now());

        if ("ARRIVED".equals(pickupStatus)) {
            this.trackingPhase = "EN_ROUTE_TO_PROCESSING_CENTER";
            this.trackingMessage = "Pickup completed. The crew is moving to the processing center.";
            this.estimatedArrivalAt = LocalDateTime.now().plusMinutes(20);
        } else if ("ASSIGNED".equals(pickupStatus) || "IN_PROGRESS".equals(pickupStatus)) {
            this.trackingPhase = "EN_ROUTE_TO_PICKUP";
            this.trackingMessage = "The crew is on the way to the pickup location.";
        }

        addTrackingEvent("CREW_LOCATION_UPDATED", "Crew location updated");
    }

    public void arriveCrew() {
        this.pickupStatus = "ARRIVED";
        this.status = SwapRequestStatus.CREW_ARRIVED;
        this.trackingPhase = "PICKUP_CONFIRMED";
        this.trackingMessage = "The crew arrived and is collecting the appliance.";
        this.estimatedArrivalAt = LocalDateTime.now().plusMinutes(20);
        addTrackingEvent("CREW_ARRIVED", "Crew arrived at pickup");
        addNotification("Crew arrived", "The assigned crew has arrived at your location.");
    }

    public void completePickup(String pickupPhotoFileName, String hubPhotoFileName, String inspectionMemo, String hubMemo) {
        this.pickupPhotoFileName = valueOrDefault(pickupPhotoFileName, this.pickupPhotoFileName);
        this.hubPhotoFileName = valueOrDefault(hubPhotoFileName, this.hubPhotoFileName);
        this.pickupInspectionMemo = valueOrDefault(inspectionMemo, this.pickupInspectionMemo);
        this.hubMemo = valueOrDefault(hubMemo, this.hubMemo);
        this.uploadedFileName = valueOrDefault(this.pickupPhotoFileName, this.uploadedFileName);
        this.pickupStatus = "COMPLETED";
        this.status = SwapRequestStatus.FINAL_INSPECTION_IN_PROGRESS;
        this.finalValuationStatus = "INSPECTING";
        this.trackingPhase = "COMPLETED";
        this.trackingMessage = "Pickup is complete and the item is in final inspection.";
        this.settlementBaseFee = 18000;
        this.settlementDistanceFee = 3500;
        this.settlementIncentive = 2000;
        this.settlementPenalty = crewPenaltyCount > 0 ? 3000 : 0;
        this.settlementTotalAmount = settlementBaseFee + settlementDistanceFee + settlementIncentive - settlementPenalty;
        this.settlementStatus = "READY";
        addTrackingEvent("PICKUP_COMPLETED", valueOrDefault(this.pickupInspectionMemo, "Pickup completed"));
        addNotification("Final inspection", "The appliance has been picked up and is now being inspected.");
    }

    public void completeMockFinalValuation() {
        completeFinalValuation(1900, List.of(
                "Exterior condition and visible wear were reviewed.",
                "Reusable components and parts value were estimated.",
                "Material recovery value was considered.",
                "Pickup and processing costs were included."
        ));
    }

    public void completeFinalValuation(Integer amount, List<String> reasons) {
        this.finalCreditValue = amount == null ? 1900 : amount;
        this.finalValuationStatus = "READY";
        this.finalValuationReasons.clear();
        this.finalValuationReasons.addAll(reasons == null || reasons.isEmpty()
                ? List.of("Final valuation was prepared from inspection results.")
                : reasons);
        this.creditStatus = "READY";
        this.status = SwapRequestStatus.FINAL_VALUATION_READY;
        this.trackingMessage = "Final valuation is ready.";
        createPickupResultReport();
        addTrackingEvent("FINAL_VALUATION_READY", "Final valuation ready");
        addNotification("Inspection finished", "Review the final valuation and proceed to credit.");
    }

    public void requestReReview(String reason) {
        if (reReviewRequested) {
            return;
        }
        this.reReviewRequested = true;
        this.reReviewReason = reason;
        this.status = SwapRequestStatus.RE_REVIEW_REQUESTED;
        this.finalValuationStatus = "RE_REVIEWING";
        this.trackingMessage = "Re-review requested.";
        addTrackingEvent("RE_REVIEW_REQUESTED", "Re-review requested: " + reason);
        addNotification("Re-review requested", "Your review request has been received.");
    }

    public void completeReReview() {
        this.reReviewCompleted = true;
        this.status = SwapRequestStatus.RE_REVIEW_COMPLETED;
        completeFinalValuation(finalCreditValue == null ? 1900 : finalCreditValue, List.of(
                "Re-review completed.",
                "The appliance condition was checked again.",
                "The final amount is now ready for confirmation."
        ));
        addNotification("Re-review complete", "Your re-review result is now ready.");
    }

    public void issueCredit() {
        if (finalCreditValue == null) {
            completeMockFinalValuation();
        }
        this.creditStatus = "ISSUED";
        this.status = SwapRequestStatus.CREDIT_ISSUED;
        this.trackingMessage = "ThinQ credit has been issued.";
        addTrackingEvent("CREDIT_ISSUED", "Credit issued");
        addNotification("Credit issued", finalCreditValue + " credits are now available.");
    }

    public void setProcessingCenter(String label, double lat, double lng) {
        this.processingCenter = new SwapRequestResponse.LocationPoint(label, lat, lng);
    }

    public void setNearbyCrews(List<SwapRequestResponse.NearbyCrew> crews) {
        this.nearbyCrews.clear();
        if (crews != null) {
            this.nearbyCrews.addAll(crews);
        }
    }

    public void setDispatchContext(
            int dispatchMatchScore,
            int dispatchPriorityRank,
            int crewRejectCount,
            int crewCancelCount,
            int crewPenaltyCount,
            String dispatchAlertMessage,
            String dispatchRecommendedReason
    ) {
        this.dispatchMatchScore = dispatchMatchScore;
        this.dispatchPriorityRank = dispatchPriorityRank;
        this.crewRejectCount = crewRejectCount;
        this.crewCancelCount = crewCancelCount;
        this.crewPenaltyCount = crewPenaltyCount;
        this.dispatchAlertMessage = dispatchAlertMessage;
        this.dispatchRecommendedReason = dispatchRecommendedReason;
    }

    public SwapRequestResponse toResponse() {
        SwapRequestResponse.Booking booking = bookingDate == null && address == null && pickupLat == null && pickupLng == null
                ? null
                : new SwapRequestResponse.Booking(bookingDate, bookingTime, address, detailAddress, pickupLat, pickupLng);
        SwapRequestResponse.PickupRequest pickupRequest = pickupRequestId == null
                ? null
                : new SwapRequestResponse.PickupRequest(
                pickupRequestId,
                pickupType,
                pickupStatus,
                crewId,
                crewName,
                address,
                scheduledLabel(),
                List.copyOf(nearbyCrews)
        );
        SwapRequestResponse.TrackingMetrics trackingMetrics = new SwapRequestResponse.TrackingMetrics(
                calculateCrewToPickupDistanceMeters(),
                calculateCrewToProcessingCenterDistanceMeters(),
                isDriverLocationLive()
        );
        SwapRequestResponse.DispatchInfo dispatchInfo = pickupRequestId == null
                ? null
                : new SwapRequestResponse.DispatchInfo(
                dispatchAlertMessage == null ? "Crew dispatch info will appear after pickup matching starts." : dispatchAlertMessage,
                dispatchMatchScore,
                dispatchPriorityRank,
                crewRejectCount,
                crewCancelCount,
                crewPenaltyCount,
                dispatchRecommendedReason == null ? "Nearby crew routing and live location are considered for priority dispatch." : dispatchRecommendedReason
        );
        SwapRequestResponse.FinalValuation finalValuation = finalValuationStatus == null
                ? null
                : new SwapRequestResponse.FinalValuation(
                finalCreditValue,
                "INR",
                finalValuationStatus,
                List.copyOf(finalValuationReasons)
        );
        SwapRequestResponse.Credit credit = creditStatus == null
                ? null
                : new SwapRequestResponse.Credit(finalCreditValue == null ? 0 : finalCreditValue, "INR", creditStatus);
        SwapRequestResponse.PickupResultReport pickupResultReport = pickupResultSummary == null
                ? null
                : new SwapRequestResponse.PickupResultReport(
                pickupResultType,
                pickupResultSummary,
                List.copyOf(pickupResultDetails)
        );
        SwapRequestResponse.Settlement settlement = settlementStatus == null
                ? null
                : new SwapRequestResponse.Settlement(
                settlementBaseFee,
                settlementDistanceFee,
                settlementIncentive,
                settlementPenalty,
                settlementTotalAmount,
                settlementStatus
        );

        return new SwapRequestResponse(
                id,
                customerId,
                status.name(),
                new SwapRequestResponse.Appliance(
                        applianceType,
                        brand,
                        modelName,
                        estimatedAge,
                        exteriorCondition,
                        conditionGrade,
                        aiAnalysisStatus,
                        aiConfidence,
                        uploadedFileName
                ),
                new SwapRequestResponse.UserConsent(
                        creditPolicyAgreed,
                        "If intentional damage is found during pickup or inspection, legal or credit penalties may apply.",
                        consentedAt
                ),
                new SwapRequestResponse.CaptureEvidence(
                        exteriorPhotoFileName,
                        labelPhotoFileName,
                        pickupPhotoFileName,
                        hubPhotoFileName,
                        pickupInspectionMemo,
                        hubMemo
                ),
                new SwapRequestResponse.PreValuation(
                        minEstimatedValue,
                        maxEstimatedValue,
                        "INR",
                        List.of(
                                "Product group: " + applianceLabel(applianceType),
                                "Brand: " + brand,
                                "Exterior condition: " + exteriorCondition,
                                "Raw material recovery value and reuse potential were included.",
                                "The final amount is confirmed after pickup and inspection."
                        )
                ),
                booking,
                pickupRequest,
                dispatchInfo,
                new SwapRequestResponse.Tracking(
                        trackingMessage,
                        estimatedArrivalAt,
                        driverLocation,
                        processingCenter,
                        trackingPhase,
                        trackingMetrics,
                        List.copyOf(nearbyCrews),
                        List.copyOf(trackingEvents)
                ),
                finalValuation,
                credit,
                pickupResultReport,
                new SwapRequestResponse.RecyclingReport(
                        pickupResultSummary == null ? "Pickup and processing results will appear here." : pickupResultSummary,
                        pickupResultDetails.isEmpty()
                                ? List.of("Reusable parts separated", "Materials sorted for recovery", "LG processing flow continued")
                                : List.copyOf(pickupResultDetails)
                ),
                settlement,
                List.copyOf(notifications)
        );
    }

    private long ensurePickupRequestId() {
        if (pickupRequestId == null) {
            pickupRequestId = id * 1000 + 1;
        }
        return pickupRequestId;
    }

    private String scheduledLabel() {
        if (bookingDate == null || bookingTime == null) {
            return "Instant pickup request";
        }
        return bookingDate + " " + bookingTime;
    }

    private void createPickupResultReport() {
        this.pickupResultType = "MATERIAL_RECOVERED";
        this.pickupResultSummary = "Pickup processing results are ready.";
        this.pickupResultDetails.clear();
        this.pickupResultDetails.add("Reusable parts were separated.");
        this.pickupResultDetails.add("Metal and plastic materials were sorted for recovery.");
        this.pickupResultDetails.add("The appliance was moved into the LG processing flow.");
    }

    private void addTrackingEvent(String eventType, String message) {
        trackingEvents.add(new SwapRequestResponse.TrackingEvent(eventType, message, LocalDateTime.now()));
    }

    private void addNotification(String title, String message) {
        notifications.add(new SwapRequestResponse.Notification(notificationSequence++, title, message, false, LocalDateTime.now()));
    }

    private Double calculateCrewToPickupDistanceMeters() {
        if (driverLocation == null || pickupLat == null || pickupLng == null) {
            return null;
        }
        return distanceMeters(driverLocation.lat(), driverLocation.lng(), pickupLat, pickupLng);
    }

    private Double calculateCrewToProcessingCenterDistanceMeters() {
        if (driverLocation == null || processingCenter == null) {
            return null;
        }
        return distanceMeters(driverLocation.lat(), driverLocation.lng(), processingCenter.lat(), processingCenter.lng());
    }

    private boolean isDriverLocationLive() {
        return driverLocation != null
                && driverLocation.updatedAt() != null
                && driverLocation.updatedAt().isAfter(LocalDateTime.now().minusSeconds(30));
    }

    private double distanceMeters(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadius * c * 10.0) / 10.0;
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String mockModelName(String applianceType) {
        return switch (valueOrDefault(applianceType, "washing_machine")) {
            case "refrigerator" -> "GL-T422VPZX";
            case "air_conditioner" -> "US-Q19BNZE3";
            case "tv" -> "OLED55C4";
            case "microwave" -> "MH8265DIS";
            default -> "FHP1411Z9P";
        };
    }

    private static String applianceLabel(String applianceType) {
        return switch (valueOrDefault(applianceType, "washing_machine")) {
            case "refrigerator" -> "Refrigerator";
            case "air_conditioner" -> "Air conditioner";
            case "tv" -> "TV";
            case "microwave" -> "Microwave";
            default -> "Washing machine";
        };
    }
}
