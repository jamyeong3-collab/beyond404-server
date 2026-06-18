package com.swapit.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "crew_reviews")
public class CrewReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_review_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "swap_request_id", nullable = false, unique = true)
    private SwapRequestEntity swapRequest;

    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 255)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected CrewReviewEntity() {
    }

    private CrewReviewEntity(SwapRequestEntity swapRequest, Long crewId, Integer rating, String comment) {
        OffsetDateTime now = OffsetDateTime.now();
        this.swapRequest = swapRequest;
        this.crewId = crewId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static CrewReviewEntity create(SwapRequestEntity swapRequest, Long crewId, Integer rating, String comment) {
        return new CrewReviewEntity(swapRequest, crewId, rating, comment);
    }

    public void updateReview(Long crewId, Integer rating, String comment) {
        this.crewId = crewId;
        this.rating = rating;
        this.comment = comment;
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getCrewId() {
        return crewId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
