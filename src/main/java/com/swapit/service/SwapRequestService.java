package com.swapit.service;

import com.swapit.domain.SwapRequestState;
import com.swapit.dto.BookingRequest;
import com.swapit.dto.CrewCompletePickupRequest;
import com.swapit.dto.CrewLocationRequest;
import com.swapit.dto.CreateSwapRequestRequest;
import com.swapit.dto.FinalValuationRequest;
import com.swapit.dto.InstantCallRequest;
import com.swapit.dto.PhotoUploadRequest;
import com.swapit.dto.ReReviewRequest;
import com.swapit.dto.SwapRequestResponse;
import com.swapit.dto.UpdateApplianceRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SwapRequestService {
    private static final long DEMO_CUSTOMER_ID = 1L;
    private static final long DEMO_CREW_ID = 101L;

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, SwapRequestState> store = new ConcurrentHashMap<>();

    public SwapRequestResponse create(CreateSwapRequestRequest request) {
        long id = sequence.getAndIncrement();
        SwapRequestState state = new SwapRequestState(id, DEMO_CUSTOMER_ID, request.applianceType());
        store.put(id, state);
        return state.toResponse();
    }

    public SwapRequestResponse analyzePhoto(long id, PhotoUploadRequest request) {
        SwapRequestState state = findState(id);
        state.applyMockInspection(request.fileName(), request.applianceType(), request.imageUrl());
        return state.toResponse();
    }

    public SwapRequestResponse updateAppliance(long id, UpdateApplianceRequest request) {
        SwapRequestState state = findState(id);
        state.updateAppliance(
                request.applianceType(),
                request.brand(),
                request.modelName(),
                request.estimatedAge(),
                request.exteriorCondition()
        );
        return state.toResponse();
    }

    public SwapRequestResponse acceptPreValuation(long id) {
        SwapRequestState state = findState(id);
        state.acceptPreValuation();
        return state.toResponse();
    }

    public SwapRequestResponse confirmBooking(long id, BookingRequest request) {
        SwapRequestState state = findState(id);
        state.confirmBooking(
                request.bookingDate(),
                request.bookingTime(),
                request.address(),
                request.detailAddress(),
                request.pickupLat(),
                request.pickupLng()
        );
        return state.toResponse();
    }

    public SwapRequestResponse requestInstantCall(long id, InstantCallRequest request) {
        SwapRequestState state = findState(id);
        state.requestInstantCall(request.address(), request.detailAddress(), request.pickupLat(), request.pickupLng());
        return state.toResponse();
    }

    public SwapRequestResponse completeMockFinalValuation(long id) {
        SwapRequestState state = findState(id);
        state.completeMockFinalValuation();
        return state.toResponse();
    }

    public SwapRequestResponse requestReReview(long id, ReReviewRequest request) {
        SwapRequestState state = findState(id);
        state.requestReReview(request.reason());
        return state.toResponse();
    }

    public SwapRequestResponse completeMockReReview(long id) {
        SwapRequestState state = findState(id);
        state.completeReReview();
        return state.toResponse();
    }

    public SwapRequestResponse issueCredit(long id) {
        SwapRequestState state = findState(id);
        state.issueCredit();
        return state.toResponse();
    }

    public SwapRequestResponse get(long id) {
        return findState(id).toResponse();
    }

    public SwapRequestResponse getTracking(long id) {
        return findState(id).toResponse();
    }

    public List<SwapRequestResponse> getAll() {
        return store.values().stream()
                .sorted(Comparator.comparingLong(SwapRequestState::getId))
                .map(SwapRequestState::toResponse)
                .toList();
    }

    public List<SwapRequestResponse> getAvailableCalls() {
        return store.values().stream()
                .filter(state -> state.getPickupRequestId() != null)
                .filter(state -> {
                    String status = state.getPickupStatus();
                    return "REQUESTED".equals(status) || "CONFIRMED".equals(status);
                })
                .sorted(Comparator.comparingLong(SwapRequestState::getId))
                .map(SwapRequestState::toResponse)
                .toList();
    }

    public SwapRequestResponse acceptCall(long pickupRequestId) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.acceptByCrew(DEMO_CREW_ID, "Aarav Sharma");
        return state.toResponse();
    }

    public SwapRequestResponse depart(long pickupRequestId) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.departCrew();
        return state.toResponse();
    }

    public SwapRequestResponse updateLocation(long pickupRequestId, CrewLocationRequest request) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.updateCrewLocation(
                request.lat(),
                request.lng(),
                request.heading() == null ? 0.0 : request.heading(),
                request.speed() == null ? 0.0 : request.speed()
        );
        return state.toResponse();
    }

    public SwapRequestResponse arrive(long pickupRequestId) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.arriveCrew();
        return state.toResponse();
    }

    public SwapRequestResponse completePickup(long pickupRequestId, CrewCompletePickupRequest request) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.completePickup(request.pickupPhotoFileName(), request.inspectionMemo());
        return state.toResponse();
    }

    public SwapRequestResponse adminCompleteFinalValuation(long id, FinalValuationRequest request) {
        SwapRequestState state = findState(id);
        state.completeFinalValuation(
                request.amount(),
                List.of(
                        valueOrDefault(request.exteriorReason(), "외관 상태를 확인했습니다."),
                        valueOrDefault(request.partsReason(), "부품 재사용 가능성을 확인했습니다."),
                        valueOrDefault(request.materialReason(), "원자재 회수 가능 가치를 반영했습니다."),
                        valueOrDefault(request.processingReason(), "수거와 안전 해체 비용을 반영했습니다.")
                )
        );
        return state.toResponse();
    }

    public List<SwapRequestResponse.Notification> getNotifications(long userId) {
        return store.values().stream()
                .filter(state -> state.toResponse().customerId() == userId || userId == DEMO_CREW_ID)
                .flatMap(state -> state.toResponse().notifications().stream())
                .toList();
    }

    private SwapRequestState findState(long id) {
        SwapRequestState state = store.get(id);
        if (state == null) {
            throw new NoSuchElementException("Swap request not found: " + id);
        }
        return state;
    }

    private SwapRequestState findByPickupRequestId(long pickupRequestId) {
        return store.values().stream()
                .filter(state -> state.getPickupRequestId() != null && state.getPickupRequestId() == pickupRequestId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Pickup request not found: " + pickupRequestId));
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
