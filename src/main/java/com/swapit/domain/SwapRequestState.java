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

    private String trackingMessage;
    private LocalDateTime estimatedArrivalAt;
    private SwapRequestResponse.DriverLocation driverLocation;
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
    private final List<SwapRequestResponse.Notification> notifications = new ArrayList<>();
    private long notificationSequence = 1;

    public SwapRequestState(long id, long customerId, String applianceType) {
        this.id = id;
        this.customerId = customerId;
        this.status = SwapRequestStatus.CREATED;
        this.applianceType = valueOrDefault(applianceType, "washing_machine");
        this.brand = "LG";
        this.modelName = "Unknown";
        this.estimatedAge = "확인 필요";
        this.exteriorCondition = "확인 필요";
        this.conditionGrade = "unknown";
        this.aiAnalysisStatus = "PENDING";
        this.aiConfidence = 0.0;
        this.minEstimatedValue = 0;
        this.maxEstimatedValue = 0;
        this.trackingMessage = "교환 신청이 생성되었어요.";
        this.estimatedArrivalAt = LocalDateTime.now().plusMinutes(35);
        addTrackingEvent("CREATED", "교환 신청 생성");
        addNotification("SwapIt 신청 시작", "교환할 가전을 촬영하고 예상 보상가를 확인해주세요.");
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

    public void applyMockInspection(String fileName, String requestedApplianceType, String imageUrl) {
        this.uploadedFileName = valueOrDefault(fileName, imageUrl);
        if (requestedApplianceType != null && !requestedApplianceType.isBlank()) {
            this.applianceType = requestedApplianceType;
        }
        this.brand = "LG";
        this.modelName = mockModelName(this.applianceType);
        this.estimatedAge = "1~3년";
        this.exteriorCondition = "사용 흔적 있음";
        this.conditionGrade = "good";
        this.aiAnalysisStatus = "COMPLETED";
        this.aiConfidence = 0.86;
        this.minEstimatedValue = 1500;
        this.maxEstimatedValue = 2400;
        this.status = SwapRequestStatus.PRE_VALUATION_READY;
        this.trackingMessage = "AI가 가전 정보를 인식했어요. 정보를 확인한 뒤 감정 단계로 진행해주세요.";
        addTrackingEvent("PHOTO_ANALYZED", "가전 사진 분석 완료");
        addNotification("AI 인식 완료", "가전 정보와 예상 보상가 범위를 확인해주세요.");
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
        this.conditionGrade = this.exteriorCondition.contains("파손") ? "damaged" : "good";
        addTrackingEvent("APPLIANCE_CONFIRMED", "고객 가전 정보 확인");
    }

    public void acceptPreValuation() {
        this.preValuationAccepted = true;
        this.status = SwapRequestStatus.PRE_VALUATION_ACCEPTED;
        this.trackingMessage = "예상 보상가를 확인했어요. 수거 방식을 선택해주세요.";
        addTrackingEvent("PRE_VALUATION_ACCEPTED", "예상 보상가 확인");
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
        this.trackingMessage = "수거 예약이 확정되었어요.";
        addTrackingEvent("BOOKING_CONFIRMED", "수거 예약 확정");
        addNotification("수거 예약 완료", scheduledLabel() + " 수거 예약이 확정되었어요.");
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
        this.trackingMessage = "근처 LG 인증 수거 파트너를 찾고 있어요.";
        addTrackingEvent("INSTANT_CALL_REQUESTED", "바로 콜 요청");
        addNotification("바로 콜 요청 완료", "근처 수거 크루를 찾고 있어요.");
    }

    public void acceptByCrew(long crewId, String crewName) {
        this.pickupRequestId = ensurePickupRequestId();
        this.crewId = crewId;
        this.crewName = valueOrDefault(crewName, "LG 인증 수거 파트너");
        this.pickupStatus = "ASSIGNED";
        this.status = SwapRequestStatus.CREW_ASSIGNED;
        this.trackingMessage = this.crewName + "님이 배정되었어요.";
        addTrackingEvent("CREW_ASSIGNED", "크루 배정");
        addNotification("수거 크루 배정", this.crewName + "님이 배정되었어요.");
    }

    public void departCrew() {
        this.pickupStatus = "IN_PROGRESS";
        this.status = SwapRequestStatus.PICKUP_IN_PROGRESS;
        this.trackingMessage = "기사님이 이동 중이에요.";
        this.estimatedArrivalAt = LocalDateTime.now().plusMinutes(18);
        addTrackingEvent("CREW_DEPARTED", "크루 출발");
        addNotification("수거 크루 출발", "LG 인증 수거 파트너가 이동 중이에요.");
    }

    public void updateCrewLocation(double lat, double lng, double heading, double speed) {
        this.driverLocation = new SwapRequestResponse.DriverLocation(lat, lng, heading, speed, LocalDateTime.now());
        this.trackingMessage = "기사님이 이동 중이에요.";
        addTrackingEvent("CREW_LOCATION_UPDATED", "크루 위치 업데이트");
    }

    public void arriveCrew() {
        this.pickupStatus = "ARRIVED";
        this.status = SwapRequestStatus.CREW_ARRIVED;
        this.trackingMessage = "기사님이 도착했어요.";
        addTrackingEvent("CREW_ARRIVED", "크루 도착");
        addNotification("수거 크루 도착", "LG 인증 수거 파트너가 도착했어요.");
    }

    public void completePickup(String pickupPhotoFileName, String inspectionMemo) {
        this.uploadedFileName = valueOrDefault(pickupPhotoFileName, this.uploadedFileName);
        this.pickupStatus = "COMPLETED";
        this.status = SwapRequestStatus.FINAL_INSPECTION_IN_PROGRESS;
        this.finalValuationStatus = "INSPECTING";
        this.trackingMessage = "수거 완료 후 최종 검수 중이에요.";
        addTrackingEvent("PICKUP_COMPLETED", valueOrDefault(inspectionMemo, "수거 완료"));
        addNotification("최종 검수 중", "수거품을 확인하고 있어요. 완료되면 알려드릴게요.");
    }

    public void completeMockFinalValuation() {
        completeFinalValuation(1900, List.of(
                "외관 상태: 전면 사용 흔적은 있으나 주요 파손은 확인되지 않았습니다.",
                "부품 재사용 가능성: 일부 내부 부품은 재사용 가능성이 있어 보상가에 반영했습니다.",
                "원자재 가치: 금속과 플라스틱 회수 가능 가치를 기준으로 산정했습니다.",
                "처리 비용: 수거, 분류, 안전 해체 비용을 차감했습니다."
        ));
    }

    public void completeFinalValuation(Integer amount, List<String> reasons) {
        this.finalCreditValue = amount == null ? 1900 : amount;
        this.finalValuationStatus = "READY";
        this.finalValuationReasons.clear();
        this.finalValuationReasons.addAll(reasons == null || reasons.isEmpty() ? List.of("최종 검수 결과를 기준으로 산정했습니다.") : reasons);
        this.creditStatus = "READY";
        this.status = SwapRequestStatus.FINAL_VALUATION_READY;
        this.trackingMessage = "최종 감정가가 확정되었어요.";
        createPickupResultReport();
        addTrackingEvent("FINAL_VALUATION_READY", "최종 감정가 확정");
        addNotification("검수 완료", "감정 결과를 확인해보세요.");
    }

    public void requestReReview(String reason) {
        if (reReviewRequested) {
            return;
        }
        this.reReviewRequested = true;
        this.reReviewReason = reason;
        this.status = SwapRequestStatus.RE_REVIEW_REQUESTED;
        this.finalValuationStatus = "RE_REVIEWING";
        this.trackingMessage = "재검수 요청이 접수되었어요.";
        addTrackingEvent("RE_REVIEW_REQUESTED", "재검수 요청: " + reason);
        addNotification("재검수 접수", "담당자가 검수 결과를 다시 확인하고 있어요.");
    }

    public void completeReReview() {
        this.reReviewCompleted = true;
        this.status = SwapRequestStatus.RE_REVIEW_COMPLETED;
        completeFinalValuation(finalCreditValue == null ? 1900 : finalCreditValue, List.of(
                "재검수 결과: 기존 감정 기준이 유지되었습니다.",
                "추가 확인: 고객 요청 사유를 반영해 외관과 부품 상태를 재확인했습니다.",
                "확정 안내: 재검수는 1회만 가능하며, 해당 금액으로 크레딧을 받을 수 있습니다."
        ));
        addNotification("재검수 완료", "재검수 결과를 확인해보세요.");
    }

    public void issueCredit() {
        if (finalCreditValue == null) {
            completeMockFinalValuation();
        }
        this.creditStatus = "ISSUED";
        this.status = SwapRequestStatus.CREDIT_ISSUED;
        this.trackingMessage = "ThinQ 크레딧이 발급되었어요.";
        addTrackingEvent("CREDIT_ISSUED", "크레딧 발급");
        addNotification("크레딧 발급 완료", "₹" + finalCreditValue + " 크레딧을 받았어요.");
    }

    public SwapRequestResponse toResponse() {
        SwapRequestResponse.Booking booking = bookingDate == null
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
                scheduledLabel()
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
                new SwapRequestResponse.PreValuation(
                        minEstimatedValue,
                        maxEstimatedValue,
                        "INR",
                        List.of(
                                "제품군: " + applianceLabel(applianceType),
                                "브랜드: " + brand,
                                "외관 상태: " + exteriorCondition,
                                "사진 기반 예상 범위이며 최종 금액은 수거 후 검수로 확정됩니다."
                        )
                ),
                booking,
                pickupRequest,
                new SwapRequestResponse.Tracking(
                        trackingMessage,
                        estimatedArrivalAt,
                        driverLocation,
                        List.copyOf(trackingEvents)
                ),
                finalValuation,
                credit,
                pickupResultReport,
                new SwapRequestResponse.RecyclingReport(
                        pickupResultSummary == null ? "수거 후 처리 결과가 생성될 예정입니다." : pickupResultSummary,
                        pickupResultDetails.isEmpty()
                                ? List.of("재활용 가능 부품 선별", "금속/플라스틱 원자재 회수", "LG 순환 처리 프로세스")
                                : List.copyOf(pickupResultDetails)
                ),
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
            return "즉시 수거 요청";
        }
        return bookingDate + " " + bookingTime;
    }

    private void createPickupResultReport() {
        this.pickupResultType = "MATERIAL_RECOVERED";
        this.pickupResultSummary = "수거품 처리 결과가 생성되었어요.";
        this.pickupResultDetails.clear();
        this.pickupResultDetails.add("재사용 가능한 부품을 선별했습니다.");
        this.pickupResultDetails.add("금속과 플라스틱 원자재 회수 대상으로 분류했습니다.");
        this.pickupResultDetails.add("안전 해체 후 LG 순환 처리 프로세스로 이동합니다.");
    }

    private void addTrackingEvent(String eventType, String message) {
        trackingEvents.add(new SwapRequestResponse.TrackingEvent(eventType, message, LocalDateTime.now()));
    }

    private void addNotification(String title, String message) {
        notifications.add(new SwapRequestResponse.Notification(notificationSequence++, title, message, false, LocalDateTime.now()));
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
            case "refrigerator" -> "냉장고";
            case "air_conditioner" -> "에어컨";
            case "tv" -> "TV";
            case "microwave" -> "전자레인지";
            default -> "세탁기";
        };
    }
}
