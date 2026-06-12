package com.swapit.repository;

import com.swapit.domain.entity.ValuationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValuationRepository extends JpaRepository<ValuationEntity, Long> {
    Optional<ValuationEntity> findFirstBySwapRequest_IdOrderByCreatedAtDesc(Long swapRequestId);
}
