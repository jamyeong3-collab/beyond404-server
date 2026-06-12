package com.swapit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pickup_requests")
public class PickupRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "swap_request_id", nullable = false)
    private SwapRequestEntity swapRequest;

    @Column(name = "crew_id")
    private Long crewId;

    @Column(name = "pickup_type", nullable = false, length = 30)
    private String pickupType;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "booking_time", length = 20)
    private String bookingTime;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "pickup_lat")
    private Double pickupLat;

    @Column(name = "pickup_lng")
    private Double pickupLng;

    @Column(name = "eta_minutes")
    private Integer etaMinutes;

    @Column(name = "crew_name", length = 80)
    private String crewName;

    @Column(name = "crew_phone", length = 30)
    private String crewPhone;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "matched_at")
    private OffsetDateTime matchedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    protected PickupRequestEntity() {
    }

    private PickupRequestEntity(SwapRequestEntity swapRequest) {
        this.swapRequest = swapRequest;
        this.createdAt = OffsetDateTime.now();
    }

    public static PickupRequestEntity create(SwapRequestEntity swapRequest) {
        return new PickupRequestEntity(swapRequest);
    }

    public void applyBooking(
            String pickupType,
            String status,
            LocalDate bookingDate,
            String bookingTime,
            String address,
            String detailAddress,
            Double pickupLat,
            Double pickupLng
    ) {
        this.pickupType = pickupType;
        this.status = status;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.address = address;
        this.detailAddress = detailAddress;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
    }

    public Long getId() {
        return id;
    }

    public SwapRequestEntity getSwapRequest() {
        return swapRequest;
    }

    public Long getCrewId() {
        return crewId;
    }

    public String getPickupType() {
        return pickupType;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public String getAddress() {
        return address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public String getCrewName() {
        return crewName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
