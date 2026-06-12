package com.swapit.repository;

import com.swapit.domain.entity.ApplianceImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplianceImageRepository extends JpaRepository<ApplianceImageEntity, Long> {
    Optional<ApplianceImageEntity> findFirstBySwapRequest_IdOrderByUploadedAtDesc(Long swapRequestId);
}
