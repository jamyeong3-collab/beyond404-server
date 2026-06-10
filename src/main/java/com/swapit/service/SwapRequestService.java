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
    private static final String DEMO_CREW_NAME = "LG Pickup Partner";

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, SwapRequestState> store = new ConcurrentHashMap<>();
    private final Map<Long, CrewGpsState> crewGpsStore = new ConcurrentHashMap<>();
    private final List<SwapRequestResponse.LocationPoint> processingCenters = List.of(
            new SwapRequestResponse.LocationPoint("Seoul West Processing Center", 37.5481, 126.8914),
            new SwapRequestResponse.LocationPoint("Seoul East Processing Center", 37.5457, 127.1427)
    );

    public SwapRequestService() {
        resetCrewGpsStore();
    }

    public SwapRequestResponse create(CreateSwapRequestRequest request) {
        long id = sequence.getAndIncrement();
        SwapRequestState state = new SwapRequestState(id, DEMO_CUSTOMER_ID, request.applianceType());
        store.put(id, state);
        return state.toResponse();
    }

    public SwapRequestResponse analyzePhoto(long id, PhotoUploadRequest request) {
        SwapRequestState state = findState(id);
        state.applyMockInspection(
                request.fileName(),
                request.applianceType(),
                request.imageUrl(),
                request.exteriorPhotoFileName(),
                request.labelPhotoFileName(),
                request.agreedToCreditPolicy()
        );
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
        enrichGpsContext(state);
        return state.toResponse();
    }

    public SwapRequestResponse requestInstantCall(long id, InstantCallRequest request) {
        SwapRequestState state = findState(id);
        state.requestInstantCall(request.address(), request.detailAddress(), request.pickupLat(), request.pickupLng());
        enrichGpsContext(state);
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
        SwapRequestState state = findState(id);
        enrichGpsContext(state);
        return state.toResponse();
    }

    public SwapRequestResponse getTracking(long id) {
        SwapRequestState state = findState(id);
        enrichGpsContext(state);
        return state.toResponse();
    }

    public List<SwapRequestResponse> getAll() {
        return store.values().stream()
                .sorted(Comparator.comparingLong(SwapRequestState::getId))
                .peek(this::enrichGpsContext)
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
                .peek(this::enrichGpsContext)
                .map(SwapRequestState::toResponse)
                .toList();
    }

    public SwapRequestResponse acceptCall(long pickupRequestId) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        CrewGpsState assignedCrew = crewGpsStore.getOrDefault(DEMO_CREW_ID, new CrewGpsState(DEMO_CREW_ID, DEMO_CREW_NAME, 37.5665, 126.9780, "AVAILABLE"));
        assignedCrew.status = "ASSIGNED";
        state.acceptByCrew(DEMO_CREW_ID, assignedCrew.crewName);
        state.updateCrewLocation(assignedCrew.lat, assignedCrew.lng, assignedCrew.heading, assignedCrew.speed);
        enrichGpsContext(state);
        return state.toResponse();
    }

    public SwapRequestResponse depart(long pickupRequestId) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.departCrew();
        enrichGpsContext(state);
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

        Long crewId = state.getCrewId() == null ? DEMO_CREW_ID : state.getCrewId();
        CrewGpsState crewState = crewGpsStore.computeIfAbsent(
                crewId,
                id -> new CrewGpsState(id, DEMO_CREW_NAME, request.lat(), request.lng(), "ASSIGNED")
        );
        crewState.lat = request.lat();
        crewState.lng = request.lng();
        crewState.heading = request.heading() == null ? 0.0 : request.heading();
        crewState.speed = request.speed() == null ? 0.0 : request.speed();
        crewState.status = "ASSIGNED";

        enrichGpsContext(state);
        return state.toResponse();
    }

    public SwapRequestResponse arrive(long pickupRequestId) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.arriveCrew();
        enrichGpsContext(state);
        return state.toResponse();
    }

    public SwapRequestResponse completePickup(long pickupRequestId, CrewCompletePickupRequest request) {
        SwapRequestState state = findByPickupRequestId(pickupRequestId);
        state.completePickup(
                request.pickupPhotoFileName(),
                request.hubPhotoFileName(),
                request.inspectionMemo(),
                request.hubMemo()
        );

        Long crewId = state.getCrewId() == null ? DEMO_CREW_ID : state.getCrewId();
        CrewGpsState crewState = crewGpsStore.get(crewId);
        if (crewState != null) {
            crewState.status = "AVAILABLE";
        }

        enrichGpsContext(state);
        return state.toResponse();
    }

    public SwapRequestResponse adminCompleteFinalValuation(long id, FinalValuationRequest request) {
        SwapRequestState state = findState(id);
        state.completeFinalValuation(
                request.amount(),
                List.of(
                        valueOrDefault(request.exteriorReason(), "Exterior condition reviewed."),
                        valueOrDefault(request.partsReason(), "Parts reuse potential reviewed."),
                        valueOrDefault(request.materialReason(), "Material recovery value reviewed."),
                        valueOrDefault(request.processingReason(), "Pickup and processing cost reviewed.")
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

    public synchronized Map<String, Object> resetDemoState() {
        store.clear();
        sequence.set(1);
        resetCrewGpsStore();
        return Map.of(
                "message", "Demo pickup state has been reset.",
                "totalSwapRequests", store.size(),
                "availableCrewCalls", getAvailableCalls().size()
        );
    }

    private void enrichGpsContext(SwapRequestState state) {
        if (state.getPickupLat() == null || state.getPickupLng() == null) {
            return;
        }

        SwapRequestResponse.LocationPoint processingCenter = processingCenters.stream()
                .min(Comparator.comparingDouble(center -> distanceMeters(
                        state.getPickupLat(),
                        state.getPickupLng(),
                        center.lat(),
                        center.lng()
                )))
                .orElse(processingCenters.get(0));

        state.setProcessingCenter(processingCenter.label(), processingCenter.lat(), processingCenter.lng());

        List<SwapRequestResponse.NearbyCrew> nearbyCrews = crewGpsStore.values().stream()
                .sorted(Comparator.comparingDouble(crew -> distanceMeters(
                        state.getPickupLat(),
                        state.getPickupLng(),
                        crew.lat,
                        crew.lng
                )))
                .map(crew -> new SwapRequestResponse.NearbyCrew(
                        crew.crewId,
                        crew.crewName,
                        crew.status,
                        crew.lat,
                        crew.lng,
                        distanceMeters(state.getPickupLat(), state.getPickupLng(), crew.lat, crew.lng),
                        state.getCrewId() != null && state.getCrewId().equals(crew.crewId)
                ))
                .toList();

        state.setNearbyCrews(nearbyCrews);

        SwapRequestResponse.NearbyCrew topCrew = nearbyCrews.isEmpty() ? null : nearbyCrews.get(0);
        int priorityRank = 0;
        if (state.getCrewId() != null) {
            priorityRank = 1;
            for (int index = 0; index < nearbyCrews.size(); index++) {
                if (state.getCrewId().equals(nearbyCrews.get(index).crewId())) {
                    priorityRank = index + 1;
                    break;
                }
            }
        } else if (topCrew != null) {
            priorityRank = 1;
        }

        double baseDistance = topCrew == null ? 1800.0 : topCrew.distanceMeters();
        int matchScore = (int) Math.max(52, Math.min(97, Math.round(96 - (baseDistance / 120.0))));
        String dispatchAlertMessage = state.getPickupStatus() == null
                ? "Dispatch starts after the user chooses booking or instant call."
                : switch (state.getPickupStatus()) {
            case "REQUESTED", "CONFIRMED" -> "A priority dispatch alert was sent to the best-matched crew.";
            case "ASSIGNED" -> "The assigned crew is sharing live route updates with the user.";
            case "IN_PROGRESS" -> "The crew is moving toward the pickup point with live GPS updates.";
            case "ARRIVED" -> "Pickup is in progress and the next route is the recycling hub.";
            default -> "Crew dispatch is complete.";
        };
        String dispatchReason = topCrew == null
                ? "No nearby crew data is available yet."
                : "Closest crew distance " + Math.round(topCrew.distanceMeters()) + "m, live location freshness, and acceptance history were reflected.";

        state.setDispatchContext(
                matchScore,
                priorityRank,
                0,
                0,
                0,
                dispatchAlertMessage,
                dispatchReason
        );
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

    private void resetCrewGpsStore() {
        crewGpsStore.clear();
        crewGpsStore.put(101L, new CrewGpsState(101L, "LG Pickup Partner", 37.5665, 126.9780, "AVAILABLE"));
        crewGpsStore.put(102L, new CrewGpsState(102L, "Mapo Crew", 37.5563, 126.9220, "AVAILABLE"));
        crewGpsStore.put(103L, new CrewGpsState(103L, "Gangseo Crew", 37.5585, 126.8321, "AVAILABLE"));
    }

    private static final class CrewGpsState {
        private final Long crewId;
        private final String crewName;
        private double lat;
        private double lng;
        private double heading;
        private double speed;
        private String status;

        private CrewGpsState(Long crewId, String crewName, double lat, double lng, String status) {
            this.crewId = crewId;
            this.crewName = crewName;
            this.lat = lat;
            this.lng = lng;
            this.status = status;
            this.heading = 0.0;
            this.speed = 0.0;
        }
    }
}
