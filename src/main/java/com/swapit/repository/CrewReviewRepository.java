package com.swapit.repository;

import com.swapit.domain.entity.CrewReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewReviewRepository extends JpaRepository<CrewReviewEntity, Long> {
    Optional<CrewReviewEntity> findBySwapRequest_Id(Long swapRequestId);

    List<CrewReviewEntity> findAllByCrewIdOrderByCreatedAtDesc(Long crewId);

    @Query("select avg(r.rating) from CrewReviewEntity r where r.crewId = :crewId")
    Double findAverageRatingByCrewId(@Param("crewId") Long crewId);
}
